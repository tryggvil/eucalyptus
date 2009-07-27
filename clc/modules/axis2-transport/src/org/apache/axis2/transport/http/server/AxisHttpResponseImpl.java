package org.apache.axis2.transport.http.server;

import org.apache.axis2.transport.OutTransportInfo;
import org.apache.http.*;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;

import java.io.IOException;
import java.io.OutputStream;

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

public class AxisHttpResponseImpl implements AxisHttpResponse, OutTransportInfo {

    private final HttpResponse response;
    private final AxisHttpConnection conn;
    private final HttpProcessor httpproc;
    private final HttpContext context;

    private AutoCommitOutputStream outstream;
    private String contentType;

    private volatile boolean commited;

    public AxisHttpResponseImpl(
            final AxisHttpConnection conn,
            final HttpResponse response,
            final HttpProcessor httpproc,
            final HttpContext context) {
        super();
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        if (conn == null) {
            throw new IllegalArgumentException("HTTP connection may not be null");
        }
        if (httpproc == null) {
            throw new IllegalArgumentException("HTTP processor may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        this.response = response;
        this.conn = conn;
        this.httpproc = httpproc;
        this.context = context;
    }

    private void assertNotCommitted() {
        if (this.commited) {
            throw new IllegalStateException("Response already committed");
        }
    }

    public boolean isCommitted() {
        return this.commited;
    }

    public void commit() throws IOException, HttpException {
        if (this.commited) {
            return;
        }
        this.commited = true;

        this.context.setAttribute(ExecutionContext.HTTP_CONNECTION, this.conn);
        this.context.setAttribute(ExecutionContext.HTTP_RESPONSE, this.response);

        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setChunked(true);
        entity.setContentType(this.contentType);

        Header header = response.getFirstHeader(HTTP.CONTENT_LEN);
        if(header != null) {
            //this is to trick BasicHttpProcessor into not barfing
            String contentLengthAsString = header.getValue();
            entity.setContentLength(Long.parseLong(contentLengthAsString));
            entity.setChunked(false);
            response.removeHeader(header);
        }
        this.response.setEntity(entity);

        this.httpproc.process(this.response, this.context);
        this.conn.sendResponse(this.response);
    }

    public OutputStream getOutputStream() {
        if (this.outstream == null) {
            this.outstream = new AutoCommitOutputStream();
        }
        return this.outstream;
    }

    public void sendError(int sc, final String msg) {
        assertNotCommitted();
        ProtocolVersion ver = this.response.getProtocolVersion();
        this.response.setStatusLine(ver, sc, msg);
    }

    public void sendError(int sc) {
        assertNotCommitted();
        this.response.setStatusCode(sc);
    }

    public void setStatus(int sc) {
        assertNotCommitted();
        this.response.setStatusCode(sc);
    }

    public void setContentType(final String contentType) {
        assertNotCommitted();
        this.contentType = contentType;
    }

    public ProtocolVersion getProtocolVersion() {
        return this.response.getProtocolVersion();
    }

    public void addHeader(final Header header) {
        assertNotCommitted();
        this.response.addHeader(header);
    }

    public void addHeader(final String name, final String value) {
        assertNotCommitted();
        this.response.addHeader(name, value);
    }

    public boolean containsHeader(final String name) {
        return this.response.containsHeader(name);
    }

    public Header[] getAllHeaders() {
        return this.response.getAllHeaders();
    }

    public Header getFirstHeader(final String name) {
        return this.response.getFirstHeader(name);
    }

    public Header[] getHeaders(String name) {
        return this.response.getHeaders(name);
    }

    public Header getLastHeader(final String name) {
        return this.response.getLastHeader(name);
    }

    public HeaderIterator headerIterator() {
        return this.response.headerIterator();
    }

    public HeaderIterator headerIterator(String name) {
        return this.response.headerIterator(name);
    }

    public void removeHeader(final Header header) {
        assertNotCommitted();
        this.response.removeHeader(header);
    }

    public void removeHeaders(final String name) {
        assertNotCommitted();
        this.response.removeHeaders(name);
    }

    public void setHeader(final Header header) {
        assertNotCommitted();
        this.response.setHeader(header);
    }

    public void setHeader(final String name, final String value) {
        assertNotCommitted();
        this.response.setHeader(name, value);
    }

    public void setHeaders(Header[] headers) {
        assertNotCommitted();
        this.response.setHeaders(headers);
    }

    public HttpParams getParams() {
        return this.response.getParams();
    }

    public void setParams(final HttpParams params) {
        this.response.setParams(params);
    }

    class AutoCommitOutputStream extends OutputStream {

        private OutputStream out;

        public AutoCommitOutputStream() {
            super();
        }

        private void ensureCommitted() throws IOException {
            try {
                commit();
            } catch (HttpException ex) {
                throw (IOException) new IOException().initCause(ex);
            }
            if (this.out == null) {
                this.out = conn.getOutputStream();
            }
        }

        public void close() throws IOException {
            ensureCommitted();
            this.out.close();
        }

        public void write(final byte[] b, int off, int len) throws IOException {
            ensureCommitted();
            this.out.write(b, off, len);
        }

        public void write(final byte[] b) throws IOException {
            ensureCommitted();
            this.out.write(b);
        }

        public void write(int b) throws IOException {
            ensureCommitted();
            this.out.write(b);
        }

        public void flush() throws IOException {
            ensureCommitted();
            this.out.flush();
        }

    }

}
