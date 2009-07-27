/*
 * Author: Chris Grzegorczyk grze@cs.ucsb.edu
 */

package edu.ucsb.eucalyptus.transport.query;

import java.io.InputStream;
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

public class HttpRequest {

  private String hostAddr;

  private boolean pureClient = false;
  private String service;
  private String servicePath;
  private String operation;
  private String operationPath;
  private String httpMethod;
  private InputStream inStream;
  private Map<String, String> parameters;
  private Map<String, String> headers;
  private Map bindingArguments;
  private String bindingName;
  private String originalNamespace;
  private String requestURL;

  public String getRequestURL() {
    return requestURL;
  }

  public void setRequestURL( final String requestURL ) {
    this.requestURL = requestURL;
  }

  public String getBindingName() {
    return bindingName;
  }

  public void setBindingName( final String bindingName ) {
    this.bindingName = bindingName;
  }
  public String getHostAddr() {
    return hostAddr;
  }

  public void setHostAddr( final String hostAddr ) {
    this.hostAddr = hostAddr;
  }

  public String getService() {
    return service;
  }

  public void setService( final String service ) {
    this.service = service;
  }

  public String getServicePath() {
    return servicePath;
  }

  public void setServicePath( final String servicePath ) {
    this.servicePath = servicePath;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation( final String operation ) {
    this.operation = operation;
  }

  public String getOperationPath() {
    return operationPath;
  }

  public void setOperationPath( final String operationPath ) {
    this.operationPath = operationPath;
  }

  public String getHttpMethod() {
    return httpMethod;
  }

  public void setHttpMethod( final String httpMethod ) {
    this.httpMethod = httpMethod;
  }

  public InputStream getInStream() {
    return inStream;
  }

  public void setInStream( final InputStream inStream ) {
    this.inStream = inStream;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public void setParameters( final Map<String, String> parameters ) {
    this.parameters = parameters;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public void setHeaders( final Map<String, String> headers ) {
    this.headers = headers;
  }

  public Map getBindingArguments() {
    return bindingArguments;
  }

  public void setBindingArguments( Map bindingArguments ) {
    this.bindingArguments = bindingArguments;
  }

  public boolean isPureClient() {
    return pureClient;
  }

  public void setPureClient( final boolean pureClient ) {
    this.pureClient = pureClient;
  }

  public String getOriginalNamespace() {
    return originalNamespace;
  }

  public void setOriginalNamespace( final String originalNamespace ) {
    this.originalNamespace = originalNamespace;
  }
}
