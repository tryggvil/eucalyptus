package edu.ucsb.eucalyptus.cloud.cluster;
/*
 * Author: Chris Grzegorczyk grze@cs.ucsb.edu
 */

import edu.ucsb.eucalyptus.cloud.*;
import edu.ucsb.eucalyptus.cloud.entities.ClusterInfo;
import edu.ucsb.eucalyptus.cloud.net.AddressUpdateCallback;
import edu.ucsb.eucalyptus.keys.*;
import edu.ucsb.eucalyptus.msgs.*;
import edu.ucsb.eucalyptus.transport.Axis2MessageDispatcher;
import edu.ucsb.eucalyptus.transport.util.Defaults;
import edu.ucsb.eucalyptus.constants.HasName;
import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;

import java.net.*;
import java.security.*;
import java.security.cert.*;
import java.util.*;
import java.util.concurrent.*;

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

public class Cluster implements HasName {

  private static Logger LOG = Logger.getLogger( Cluster.class );

  private ClusterInfo clusterInfo;
  private ConcurrentNavigableMap<String, NodeInfo> nodeMap;
  private ClusterState state;
  private ClusterNodeState nodeState;
  private ClusterMessageQueue messageQueue;
  private ResourceUpdateCallback rscUpdater;
  private AddressUpdateCallback addrUpdater;
  private VmUpdateCallback vmUpdater;
  private NodeLogCallback nodeLogUpdater;
  private NodeCertCallback nodeCertUpdater;
  private Thread rscThread;
  private Thread vmThread;
  private Thread addrThread;
  private Thread mqThread;
  private Thread logThread;
  private Thread keyThread;
  private boolean stopped = false;
  private boolean reachable = false;

  public Cluster( ClusterInfo clusterInfo ) {
    this.clusterInfo = clusterInfo;
    this.state = new ClusterState( this );
    this.nodeState = new ClusterNodeState( this );
    this.messageQueue = new ClusterMessageQueue( this );
    this.rscUpdater = new ResourceUpdateCallback( this );
    this.addrUpdater = new AddressUpdateCallback( this );
    this.vmUpdater = new VmUpdateCallback( this );
    this.nodeLogUpdater = new NodeLogCallback( this );
    this.nodeCertUpdater = new NodeCertCallback( this );
    this.nodeMap = new ConcurrentSkipListMap<String, NodeInfo>();
  }

  public ClusterNodeState getNodeState() {
    return nodeState;
  }

  public void setNodeState( final ClusterNodeState nodeState ) {
    this.nodeState = nodeState;
  }

  private void waitForCerts() {
    Axis2MessageDispatcher dispatcher = Defaults.getMessageDispatcher( Defaults.getInsecureOutboundEndpoint( clusterInfo.getInsecureUri(), ClusterInfo.NAMESPACE, 15, 1, 1 ) );
    GetKeysResponseType reply = null;
    do {
      try {
        reply = ( GetKeysResponseType ) dispatcher.getClient().send( new GetKeysType( "self" ) );
        reachable = true;
      } catch ( Exception e ) {
        LOG.debug( e, e );
      }
      try {Thread.sleep( 5000 );} catch ( InterruptedException ignored ) {}
    } while ( ( reply == null || !this.checkCerts( reply ) ) && !stopped );
  }

  private boolean checkCerts( final GetKeysResponseType reply ) {
    NodeCertInfo certs = reply.getCerts();
    if ( certs == null ) return false;
    String ccCert = new String( Base64.decode( certs.getCcCert() ) );
    String ncCert = new String( Base64.decode( certs.getNcCert() ) );
    boolean ret = true;
    LOG.info( "===============================================================" );
    LOG.info( " Trying to verify the certificates for " + this.getClusterInfo().getName() );
    LOG.info( "---------------------------------------------------------------" );
    try {
      X509Certificate x509 = AbstractKeyStore.pemToX509( ccCert );
      String alias = ServiceKeyStore.getInstance().getCertificateAlias( x509 );
      LOG.info( "FOUND: alias " + alias );
    }
    catch ( GeneralSecurityException e ) {
      LOG.error( e );
      ret = false;
    }
    LOG.info( "---------------------------------------------------------------" );
    try {
      String alias = ServiceKeyStore.getInstance().getCertificateAlias( ncCert );
      LOG.info( "FOUND: alias " + alias );
    }
    catch ( GeneralSecurityException e ) {
      ret = false;
      LOG.error( e );
    }
    LOG.info( "===============================================================" );
    return ret;
  }

  class ClusterStartupWatchdog extends Thread {
    Cluster cluster = null;
    ClusterStartupWatchdog( final Cluster cluster ) {
      super( cluster.getName() + "-ClusterStartupWatchdog" );
      this.cluster = cluster;
    }

    public void run() {
      LOG.info( "Calling startup on cluster: " + cluster.getName() );
      cluster.startThreads();
    }
  }

  public void start() {
    ( new ClusterStartupWatchdog( this ) ).start();
  }

  public void startThreads() {
    //:: should really be organized as a thread group etc etc :://
    LOG.warn( "Starting cluster: " + this.clusterInfo.getUri() );
    this.waitForCerts();
    if( !stopped ) {
    if ( this.mqThread == null || this.mqThread.isAlive() )
      this.mqThread = this.startNamedThread( messageQueue );

    if ( this.rscThread == null || this.rscThread.isAlive() )
      this.rscThread = this.startNamedThread( rscUpdater );

    if ( this.vmThread == null || this.vmThread.isAlive() )
      this.vmThread = this.startNamedThread( vmUpdater );

    if ( this.addrThread == null || this.addrThread.isAlive() )
      this.addrThread = this.startNamedThread( addrUpdater );

    if ( this.keyThread == null || this.keyThread.isAlive() )
      this.keyThread = this.startNamedThread( nodeCertUpdater );//, nodeCertUpdater.getClass().getSimpleName() + "-" + this.getName() ) ).start();

//    if ( this.logThread != null && !this.logThread.isAlive() )
//      ( this.logThread = new Thread( nodeLogUpdater, nodeLogUpdater.getClass().getSimpleName() + "-" + this.getName() ) ).start();

    }
  }

  private Thread startNamedThread( Runnable r ) {
    Thread t = new Thread( r );
    t.setName( String.format( "%s-%s@%X", r.getClass().getSimpleName(), this.getName(), t.hashCode() ) );
    t.start();
    LOG.warn( "Starting threads for [ " + this.getName() + " ] " + t.getName() );
    return t;
  }

  public void stop() {
    LOG.warn( "Stopping cluster: " + this.clusterInfo.getUri() );
    this.reachable = false;
    this.stopped = true;
    this.rscUpdater.stop();
    this.addrUpdater.stop();
    this.vmUpdater.stop();
    this.nodeLogUpdater.stop();
    this.nodeCertUpdater.stop();
    this.messageQueue.stop();
  }

  public ClusterInfoType getInfo() {
    String state = String.format( "%4s %s", this.isReachable() ? "UP" : "DOWN", this.clusterInfo.getHost() );
    return new ClusterInfoType( this.clusterInfo.getName(), state );
  }

  public ClusterState getState() {
    return state;
  }

  public ClusterMessageQueue getMessageQueue() {
    return messageQueue;
  }

  public int compareTo( final Object o ) {
    Cluster that = ( Cluster ) o;
    return this.getName().compareTo( that.getName() );
  }

  public void updateNodeInfo( List<String> nodeTags ) {
    NodeInfo ret = null;
    for ( String tag : nodeTags )
      if ( ( ret = this.nodeMap.putIfAbsent( tag, new NodeInfo( tag ) ) ) != null )
        ret.touch();
  }

  public void updateNodeCerts( NavigableSet<NodeCertInfo> nodeCerts ) {
    NodeInfo ret = null;
    for ( NodeCertInfo cert : nodeCerts ) {
      NodeInfo newNodeInfo = new NodeInfo( cert );
      if ( ( ret = this.nodeMap.putIfAbsent( cert.getServiceTag(), newNodeInfo ) ) != null )
        ret.setCerts( cert );
    }
  }

  public void updateNodeLogs( NavigableSet<NodeLogInfo> nodeCerts ) {
    NodeInfo ret = null;
    for ( NodeLogInfo log : nodeCerts )
      if ( ( ret = this.nodeMap.putIfAbsent( log.getServiceTag(), new NodeInfo( log ) ) ) != null )
        ret.setLogs( log );
  }

  public NavigableSet<String> getNodeTags() {
    return this.nodeMap.navigableKeySet();
  }

  public NavigableSet<NodeInfo> getNodes() {
    return new ConcurrentSkipListSet<NodeInfo>( this.nodeMap.values() );
  }

  public NodeInfo getNode( String serviceTag ) {
    return this.nodeMap.get( serviceTag );
  }

  public ClusterStateType getWeb() {
    String host = this.getClusterInfo().getUri();
    int port = 0;
    try {
      URI uri = new URI( this.getClusterInfo().getUri() );
      host = uri.getHost();
      port = uri.getPort();
    }
    catch ( URISyntaxException e ) {}
    return new ClusterStateType( this.getName(), host, port );
  }

  public ClusterInfo getClusterInfo() {
    return clusterInfo;
  }

  public String getName() {
    return this.clusterInfo.getName();
  }

  public boolean isReachable() {
    return reachable;
  }

  public void setReachable( final boolean reachable ) {
    this.reachable = reachable;
  }

  @Override
  public String toString() {
    return "Cluster{" +
           "clusterInfo=" + clusterInfo +
           "\n" + this.getName() + ".state=" + state +
           "\n" + this.getName() + ".messageQueue=" + messageQueue +
           "\n" + this.getName() + ".nodeMap=" + nodeMap +
           '}';
  }
}
