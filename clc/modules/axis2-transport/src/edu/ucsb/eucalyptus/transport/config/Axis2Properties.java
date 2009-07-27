/*
 * Author: Chris Grzegorczyk grze@cs.ucsb.edu
 */

package edu.ucsb.eucalyptus.transport.config;

import edu.ucsb.eucalyptus.transport.binding.Binding;
import edu.ucsb.eucalyptus.transport.binding.BindingManager;
import edu.ucsb.eucalyptus.util.BaseDirectory;
import edu.ucsb.eucalyptus.util.BindingUtil;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.log4j.Logger;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.jibx.runtime.JiBXException;

import java.io.File;
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

public class Axis2Properties {

  private static Logger LOG = Logger.getLogger( Axis2Properties.class );

  //:: converted to WSDLConstatns.MEPxxxx :://
  private Mep mep;

  //:: different configuration :://
  private String conf;
  private ConfigurationContext context;

  //:: policy and its related application details :://
  private String wssecPolicy;
  private Mep wssecFlow;
  private Policy inPolicy;
  private Policy outPolicy;

  private String namespace;
  private Binding binding;

  public Axis2Properties( Map<String, String> props, ConfigurationContext ctx )
  {
    this.namespace = Key.NAMESPACE.getString( props );
    this.mep = Key.MEP.getMep( props );
    this.wssecFlow = Key.WSSEC_FLOW.getMep( props );
    this.wssecPolicy = Key.WSSEC_POLICY.getString( props );
    this.conf = Key.CONF.getString( props );
    this.context = ctx;
    this.build();
  }

  public String getNamespace()
  {
    return namespace;
  }

  public Binding getBinding()
  {
    return binding;
  }

  public Mep getMep()
  {
    return mep;
  }

  public ConfigurationContext getAxisContext()
  {
    return context;
  }

  public Policy getInPolicy()
  {
    return inPolicy;
  }

  public Policy getOutPolicy()
  {
    return outPolicy;
  }

  public Mep getWssecFlow()
  {
    return wssecFlow;
  }

  private void build()
  {
    try
    {
      this.binding = BindingManager.getBinding( BindingUtil.sanitizeNamespace( this.namespace ) );
    }
    catch ( JiBXException e )
    {
      LOG.error( e, e );
    }
    if ( this.wssecPolicy != null )
      try
      {
        File policyFile = new File( this.wssecPolicy );
        if ( policyFile.exists() && policyFile.length() > 1 && ( this.wssecFlow.appliesToIn() || this.wssecFlow.appliesToOut() ) )
        {
          StAXOMBuilder builder = new StAXOMBuilder( this.wssecPolicy );
          Policy policy = PolicyEngine.getPolicy( builder.getDocumentElement() );
          if ( this.wssecFlow.appliesToIn() )
            this.inPolicy = policy;
          if ( this.wssecFlow.appliesToOut() )
            this.outPolicy = policy;
        }
      }
      catch ( Exception e )
      {
      }

    if ( this.conf != null )
      try
      {
        File confFile = new File( this.conf );
        if ( confFile.exists() && confFile.length() > 1 )
          this.context = ConfigurationContextFactory.createConfigurationContextFromFileSystem( BaseDirectory.VAR.toString(), this.conf );
      }
      catch ( Exception e )
      {
        LOG.error( e, e );
      }
  }
}
