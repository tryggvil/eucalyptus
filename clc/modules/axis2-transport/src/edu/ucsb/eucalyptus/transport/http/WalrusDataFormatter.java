package edu.ucsb.eucalyptus.transport.http;

import edu.ucsb.eucalyptus.transport.query.WalrusQueryDispatcher;
import edu.ucsb.eucalyptus.util.WalrusDataMessage;
import edu.ucsb.eucalyptus.util.WalrusDataMessenger;
import edu.ucsb.eucalyptus.util.WalrusProperties;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.MessageFormatter;
import org.apache.axis2.transport.http.util.URLTemplatingUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.GZIPOutputStream;
import org.apache.log4j.Logger;

/*******************************************************************************
 * Copyright (c) 2009  Eucalyptus Systems, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
 *
 *
 * This file is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Please contact Eucalyptus Systems, Inc., 130 Castilian
 * Dr., Goleta, CA 93101 USA or visit <http://www.eucalyptus.com/licenses/>
 * if you need additional information or have any questions.
 *
 * This file may incorporate work covered under the following copyright and
 * permission notice:
 *
 *   Software License Agreement (BSD License)
 *
 *   Copyright (c) 2008, Regents of the University of California
 *   All rights reserved.
 *
 *   Redistribution and use of this software in source and binary forms, with
 *   or without modification, are permitted provided that the following
 *   conditions are met:
 *
 *     Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 *   IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *   TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *   PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 *   OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *   EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *   PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *   LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *   NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *   SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. USERS OF
 *   THIS SOFTWARE ACKNOWLEDGE THE POSSIBLE PRESENCE OF OTHER OPEN SOURCE
 *   LICENSED MATERIAL, COPYRIGHTED MATERIAL OR PATENTED MATERIAL IN THIS
 *   SOFTWARE, AND IF ANY SUCH MATERIAL IS DISCOVERED THE PARTY DISCOVERING
 *   IT MAY INFORM DR. RICH WOLSKI AT THE UNIVERSITY OF CALIFORNIA, SANTA
 *   BARBARA WHO WILL THEN ASCERTAIN THE MOST APPROPRIATE REMEDY, WHICH IN
 *   THE REGENTS’ DISCRETION MAY INCLUDE, WITHOUT LIMITATION, REPLACEMENT
 *   OF THE CODE SO IDENTIFIED, LICENSING OF THE CODE SO IDENTIFIED, OR
 *   WITHDRAWAL OF THE CODE CAPABILITY TO THE EXTENT NEEDED TO COMPLY WITH
 *   ANY SUCH LICENSES OR RIGHTS.
 ******************************************************************************/

public class WalrusDataFormatter implements MessageFormatter {

    private static Logger LOG = Logger.getLogger( WalrusDataFormatter.class );

    public byte[] getBytes(MessageContext messageContext, OMOutputFormat format) throws AxisFault {
        //not used
        return null;
    }

    public void writeTo(MessageContext messageContext, OMOutputFormat format,
                        OutputStream outputStream, boolean preserve) throws AxisFault {

        Integer status = (Integer) messageContext.getProperty(Axis2HttpWorker.HTTP_STATUS);
        if(status == null) {
            Boolean getType = (Boolean) messageContext.getProperty(WalrusProperties.STREAMING_HTTP_GET);
            if(getType != null && getType.equals(Boolean.FALSE)) {
                try {
                    outputStream.flush();
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }   else {
                String key = (String) messageContext.getProperty("GET_KEY");
                String randomKey = (String) messageContext.getProperty("GET_RANDOM_KEY");
                Boolean isCompressed = (Boolean) messageContext.getProperty("GET_COMPRESSED");
                if(isCompressed == null)
                    isCompressed = false;

                GZIPOutputStream gzipOutStream = null;
                if(isCompressed) {
                    try {
                        gzipOutStream = new GZIPOutputStream(outputStream);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                        return;
                    }
                }

                WalrusDataMessenger messenger = WalrusQueryDispatcher.getReadMessenger();
                LinkedBlockingQueue<WalrusDataMessage> getQueue = messenger.getQueue(key, randomKey);

                WalrusDataMessage dataMessage;
                try {
                    while ((dataMessage = getQueue.take())!=null) {
                        if(WalrusDataMessage.isStart(dataMessage)) {
                            //TODO: should read size and verify
                        } else if(WalrusDataMessage.isData(dataMessage)) {
                            byte[] data = dataMessage.getPayload();
                            if(isCompressed) {
                                try {
                                    gzipOutStream.write(data);
                                } catch(Exception ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                for (byte b: data) {
                                    try {
                                        outputStream.write(b);
                                    } catch  (IOException e) {
                                        e.printStackTrace();
                                        throw new AxisFault("An error occured while writing the request");
                                    }
                                }
                            }
                        } else if(WalrusDataMessage.isEOF(dataMessage)) {
                            try {
                                if(isCompressed) {
                                    gzipOutStream.finish();
                                    gzipOutStream.flush();
                                } else {
                                    outputStream.flush();
                                }
                                messenger.removeQueue(key, randomKey);
                                break;
                            } catch  (IOException e) {
                                e.printStackTrace();
                                throw new AxisFault("An error occured while writing the request");
                            }
                        }
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public String getContentType(MessageContext messageContext, OMOutputFormat format,
                                 String soapAction) {

        String contentType = "text/plain";
        return contentType;
    }


    public URL getTargetAddress(MessageContext messageContext, OMOutputFormat format, URL targetURL)
            throws AxisFault {
        // Check whether there is a template in the URL, if so we have to replace then with data
        // values and create a new target URL.
        targetURL = URLTemplatingUtil.getTemplatedURL(targetURL, messageContext, false);

        return targetURL;
    }


    public String formatSOAPAction(MessageContext messageContext, OMOutputFormat format,
                                   String soapAction) {
        return soapAction;
    }

}
