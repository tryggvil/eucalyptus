/*
 * Author: Chris Grzegorczyk grze@cs.ucsb.edu
 */

package edu.ucsb.eucalyptus.transport;

import edu.ucsb.eucalyptus.transport.config.*;
import edu.ucsb.eucalyptus.util.EucalyptusProperties;
import org.apache.axis2.AxisFault;
import org.apache.axis2.description.*;
import org.apache.log4j.Logger;
import org.mule.api.component.JavaComponent;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.lifecycle.CreateException;
import org.mule.api.service.Service;
import org.mule.api.transport.Connector;
import org.mule.transport.*;

import javax.xml.namespace.QName;
import java.util.*;

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

public class Axis2MessageReceiver extends AbstractMessageReceiver {

  private static Logger LOG = Logger.getLogger( Axis2MessageReceiver.class );
  private AxisService axisService;
  private Axis2InProperties properties;

  @SuppressWarnings( "unchecked" )
  public Axis2MessageReceiver( Connector connector, Service service, InboundEndpoint endpoint ) throws CreateException {
    super( connector, service, endpoint );
    Class serviceClass = ( ( JavaComponent ) this.getService().getComponent() ).getObjectType();
    if ( !endpoint.getProperties().containsKey( Key.WSSEC_POLICY.getKey() ) )
      endpoint.getProperties().put( Key.WSSEC_POLICY.getKey(), ( ( Axis2Connector ) connector ).getDefaultWssecPolicy() );
    this.properties = new Axis2InProperties( serviceClass, ( Map<String, String> ) endpoint.getProperties(), ( ( Axis2Connector ) connector ).getAxisConfig() );
  }

  public void doConnect() throws ConnectException {
    try {
      this.axisService = Axis2ServiceBuilder.getAxisService( this );
    }
    catch ( AxisFault axisFault ) {
      throw new ConnectException( axisFault, this );
    }
    Iterator iter = this.axisService.getOperations();
    while ( iter.hasNext() ) {
      AxisOperation op = (AxisOperation)iter.next();
      if ( EucalyptusProperties.getDisabledOperations().contains( op.getName().getLocalPart() ) )
        this.disableOperation( op.getName().getLocalPart() );
    }
  }

  private Map<String, AxisOperation> disabledOperations = new HashMap<String, AxisOperation>();

  public void disableOperation( String operationName ) {
    this.disabledOperations.put( operationName, this.axisService.getOperation( new QName( operationName ) ) );
    this.axisService.removeOperation( new QName( operationName ) );
  }

  public void enableOperation( String operationName ) {
    AxisOperation op = this.disabledOperations.remove( operationName );
    this.axisService.addOperation( op );
  }

  public void doStart() {
    try {
      Axis2Connector connector = ( Axis2Connector ) this.getConnector();
      this.axisService.setActive( true );
      connector.getAxisConfig().deployService( this.axisService );
      connector.addHttpPortListener( this.getEndpointURI().getAddress(), this.getEndpointURI().getPort() );
    }
    catch ( AxisFault axisFault ) {}
  }

  public Axis2InProperties getProperties() {
    return this.properties;
  }

  public void doStop() {}

  public void doDispose() {}

  public void doDisconnect() throws ConnectException {}

}

