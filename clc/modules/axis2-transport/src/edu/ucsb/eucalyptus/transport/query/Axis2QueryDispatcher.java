/*
 * Author: Chris Grzegorczyk grze@cs.ucsb.edu
 */

package edu.ucsb.eucalyptus.transport.query;

import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.HandlerDescription;
import org.apache.log4j.Logger;

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

public class Axis2QueryDispatcher extends GenericHttpDispatcher implements RESTfulDispatcher {

  public static final String NAME = "Axis2QueryDispatcher";
  private static Logger LOG = Logger.getLogger( Axis2QueryDispatcher.class );

  private String queryVersion = "2009-04-04";

  public boolean accepts( final HttpRequest httpRequest, final MessageContext messageContext )
  {
    //:: decide about whether or not to accept the request for processing :://
    if( httpRequest == null || httpRequest.getParameters().isEmpty() ) return false;
    for( Axis2QueryDispatcher.RequiredQueryParams p : Axis2QueryDispatcher.RequiredQueryParams.values() )
      if( !httpRequest.getParameters().containsKey( p.toString() ) ) return false;
    if( OperationParameter.getParameter( httpRequest.getParameters() ) == null ) return false;
    this.queryVersion = httpRequest.getParameters(  ).get( Axis2QueryDispatcher.RequiredQueryParams.Version.toString() );
    return true;
  }

  public String getOperation( HttpRequest httpRequest, MessageContext messageContext )
  {
    return OperationParameter.getParameter( httpRequest.getParameters() );
  }

  public QuerySecurityHandler getSecurityHandler()
  {
    return new EucalyptusQuerySecurityHandler();
  }

  public QueryBinding getBinding()
  {
    return new DefaultQueryBinding();
  }

  public String getNamespace()
  {
    return "http://ec2.amazonaws.com/doc/"+this.queryVersion+"/";
  }

  public void initDispatcher()
  {
    init( new HandlerDescription( NAME ) );
  }

  public enum RequiredQueryParams {
    SignatureVersion, Version
  }

  public enum OperationParameter {

    Operation, Action;
    private static String patterh = buildPattern();

    private static String buildPattern()
    {
      StringBuilder s = new StringBuilder();
      for ( OperationParameter op : OperationParameter.values() ) s.append( "(" ).append( op.name() ).append( ")|" );
      s.deleteCharAt( s.length() - 1 );
      return s.toString();
    }

    public static String toPattern()
    {
      return patterh;
    }

    public static String getParameter( Map<String,String> map )
    {
      for( OperationParameter op : OperationParameter.values() )
        if( map.containsKey( op.toString() ) )
          return map.get( op.toString() );
      return null;
    }
  }
}
