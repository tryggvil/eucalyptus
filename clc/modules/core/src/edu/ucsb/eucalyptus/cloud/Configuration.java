/*
 * Author: Chris Grzegorczyk grze@cs.ucsb.edu
 */

package edu.ucsb.eucalyptus.cloud;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import edu.ucsb.eucalyptus.util.BaseDirectory;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Calendar;

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

@XStreamAlias( "eucalyptus" )
public class Configuration {

  @XStreamOmitField
  private static Logger LOG = Logger.getLogger( Configuration.class );
  @XStreamOmitField
  public static String CONFIG_FILE = BaseDirectory.CONF.toString() + File.separator + "eucalyptus.xml";
  @XStreamAsAttribute
  @XStreamAlias( "admin-email" )
  private String adminEmail;
  @XStreamAsAttribute
  @XStreamAlias( "reply-to-email" )
  private String replyToEmail;

  public Configuration() {}

  public String getReplyToEmail()
  {
    return replyToEmail;
  }

  public String getAdminEmail()
  {
    return adminEmail;
  }

  private static Configuration config = null;

  static
  {
    config = getConfiguration();
  }

  private static XStream xstream = getXStream();

  private static XStream getXStream()
  {
    if ( xstream == null )
    {
      xstream = new XStream();
      xstream.processAnnotations( Configuration.class );
    }
    return xstream;
  }

  public static void storeConfiguration() throws FileNotFoundException
  {
    getXStream().toXML( config, new FileOutputStream( CONFIG_FILE ) );
  }

  private static Calendar last = Calendar.getInstance();

  public static Configuration getConfiguration()
  {
    try
    {
      synchronized ( Configuration.class )
      {
        Calendar now = Calendar.getInstance();
        if ( last == null )
          last = Calendar.getInstance();
        Calendar checkTime = ( Calendar ) last.clone();
        checkTime.add( Calendar.SECOND, 60 );
        if ( config == null || now.after( checkTime ) )
        {
          config = ( Configuration ) getXStream().fromXML( new FileReader( CONFIG_FILE ) );
          last = Calendar.getInstance();
        }
        return config;
      }
    }
    catch ( FileNotFoundException e )
    {
      e.printStackTrace();
      LOG.fatal( "Failed to find configuration file: " + new File( CONFIG_FILE ).getAbsolutePath(), e );
      System.exit( 1 );
      return config;
    }
  }
}
