/*
 * Author: Chris Grzegorczyk grze@cs.ucsb.edu
 */

package edu.ucsb.eucalyptus.transport.http;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.SessionContext;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.engine.ListenerManager;
import org.apache.axis2.transport.TransportListener;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.server.HttpFactory;
import org.apache.axis2.transport.http.server.SessionManager;
import org.apache.axis2.transport.http.server.SimpleHttpServer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

public class Axis2HttpListener {

  private static Logger LOG = Logger.getLogger( Axis2HttpListener.class );

  private Map<Integer, HttpPort> serverMap;
  private ConfigurationContext axisConfigContext;

  public Axis2HttpListener( ConfigurationContext configurationContext )
  {
    this.serverMap = new HashMap<Integer, HttpPort>();
    this.axisConfigContext = configurationContext;
  }

  public void addHttpListener( String host, int port ) throws AxisFault
  {
    if ( this.serverMap.containsKey( port ) ) return;
    HttpPort newHttpListener = new HttpPort( this.axisConfigContext, host, port );
    newHttpListener.start();
    this.serverMap.put( port, newHttpListener );
  }

}

class HttpPort implements TransportListener {

  private static Logger LOG = Logger.getLogger( HttpPort.class );

  private ConfigurationContext axisConfigContext;
  private String address;
  private int port;
  private SessionManager sessionManager;
  private HttpFactory httpFactory;
  private SimpleHttpServer httpServer;

  public HttpPort( ConfigurationContext configurationContext, String address, int port ) throws AxisFault
  {
    this.address = address;
    this.port = port;
    this.axisConfigContext = configurationContext;
    ListenerManager listenerManager = this.axisConfigContext.getListenerManager();
    if ( listenerManager == null )
    {
      listenerManager = new ListenerManager();
      listenerManager.init( this.axisConfigContext );
    }
    TransportInDescription httpDescription = new TransportInDescription( Constants.TRANSPORT_HTTP );
    httpDescription.setReceiver( this );
//    httpDescription.addParameter( new Parameter( "port", this.port ) );
    listenerManager.addListener( httpDescription, true );
    this.httpFactory = new HttpFactory( this.axisConfigContext, this.port, new Axis2HttpWorkerFactory() );
    this.httpFactory.setRequestCoreThreadPoolSize( 64 );
    this.httpFactory.setRequestMaxThreadPoolSize( 4096 );
    this.httpFactory.setRequestSocketTimeout( 3600000 );
    this.sessionManager = new SessionManager();
  }

  public void init( ConfigurationContext configurationContext, TransportInDescription transportInDescription ) throws AxisFault
  {
  }

  public void start() throws AxisFault
  {
    try
    {
      this.httpServer = new SimpleHttpServer( httpFactory, port );
      this.httpServer.init();
      this.httpServer.start();
    }
    catch ( IOException e )
    {
      LOG.error( e.getMessage(), e );
      throw AxisFault.makeFault( e );
    }
  }

  public void stop()
  {
    LOG.warn( "stopping http server" );
    if ( this.httpServer != null )
      try
      {
        this.httpServer.destroy();
      }
      catch ( Exception e )
      {
        LOG.error( e.getMessage(), e );
      }

  }

  public EndpointReference getEPRForService( String serviceName, String ip ) throws AxisFault
  {
    return getEPRsForService( serviceName, ip )[ 0 ];
  }

  public EndpointReference[] getEPRsForService( String serviceName, String ip ) throws AxisFault
  {
    String endpointReference = this.address;
    endpointReference += ( this.axisConfigContext.getServiceContextPath().startsWith( "/" ) ? "" : '/' )
                         + this.axisConfigContext.getServiceContextPath() + "/" + serviceName;
    LOG.warn( "endpoint=" + endpointReference );
    return new EndpointReference[]{ new EndpointReference( endpointReference ) };
  }

  public SessionContext getSessionContext( MessageContext messageContext )
  {
    String sessionKey = ( String ) messageContext.getProperty( HTTPConstants.COOKIE_STRING );
    return this.sessionManager.getSessionContext( sessionKey );
  }

  public void destroy()
  {
    this.axisConfigContext = null;
  }

}

