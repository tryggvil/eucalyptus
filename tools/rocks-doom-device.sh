#!/bin/bash

#Copyright (c) 2009  Eucalyptus Systems, Inc.	
#
#This program is free software: you can redistribute it and/or modify
#it under the terms of the GNU General Public License as published by 
#the Free Software Foundation, only version 3 of the License.  
# 
#This file is distributed in the hope that it will be useful, but WITHOUT
#ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
#FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
#for more details.  
#
#You should have received a copy of the GNU General Public License along
#with this program.  If not, see <http://www.gnu.org/licenses/>.
# 
#Please contact Eucalyptus Systems, Inc., 130 Castilian
#Dr., Goleta, CA 93101 USA or visit <http://www.eucalyptus.com/licenses/> 
#if you need additional information or have any questions.
#
#This file may incorporate work covered under the following copyright and
#permission notice:
#
#  Software License Agreement (BSD License)
#
#  Copyright (c) 2008, Regents of the University of California
#  
#
#  Redistribution and use of this software in source and binary forms, with
#  or without modification, are permitted provided that the following
#  conditions are met:
#
#    Redistributions of source code must retain the above copyright notice,
#    this list of conditions and the following disclaimer.
#
#    Redistributions in binary form must reproduce the above copyright
#    notice, this list of conditions and the following disclaimer in the
#    documentation and/or other materials provided with the distribution.
#
#  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
#  IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
#  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
#  PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
#  OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
#  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
#  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
#  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
#  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
#  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
#  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. USERS OF
#  THIS SOFTWARE ACKNOWLEDGE THE POSSIBLE PRESENCE OF OTHER OPEN SOURCE
#  LICENSED MATERIAL, COPYRIGHTED MATERIAL OR PATENTED MATERIAL IN THIS
#  SOFTWARE, AND IF ANY SUCH MATERIAL IS DISCOVERED THE PARTY DISCOVERING
#  IT MAY INFORM DR. RICH WOLSKI AT THE UNIVERSITY OF CALIFORNIA, SANTA
#  BARBARA WHO WILL THEN ASCERTAIN THE MOST APPROPRIATE REMEDY, WHICH IN
#  THE REGENTS’ DISCRETION MAY INCLUDE, WITHOUT LIMITATION, REPLACEMENT
#  OF THE CODE SO IDENTIFIED, LICENSING OF THE CODE SO IDENTIFIED, OR
#  WITHDRAWAL OF THE CODE CAPABILITY TO THE EXTENT NEEDED TO COMPLY WITH
#  ANY SUCH LICENSES OR RIGHTS.

# we are on rocks V, so let's generate the roll. WARNING: this is going
# to be used by developers who wants to generated binaries! If you just
# want to use eucalyptys, please get the binaries. I repeat: you are
# risking to DESTROY a working environment using this script.
#
# eucalyptus  web site http://open.eucalyptus.com
#
#

set -e

usage () {
	echo "Usage: $0 [-d <top_dir>][uninstall]"
	echo
	echo "   eucalyptus will be install in /top_dir/eucalyptus-<version>"
	echo "   top_dir by default is /opt"
	echo
}

ACTION="install"
TOP_DIR="/opt/eucalyptus"
VERSION="1.4"

# let's parse the command line
while [ $# -gt 0 ]; do
	if [ "$1" = "-d" ]; then
		shift
		if [ $# -lt 1 ]; then
			usage
			exit 1
		fi
		TOP_DIR="$1"
		shift
		continue
	fi
	if [ "$1" = "uninstall" ]; then
		ACTION="uninstall"
		shift
		continue
	fi
	usage
	exit 1
done

# get full path of this script
SCRIPT_DIR=`dirname "$0"`
if [ "${SCRIPT_DIR:0:1}" != "/" ]; then
    SCRIPT_DIR="$PWD/$SCRIPT_DIR"
fi
# remove the trailing "/.", if there
SCRIPT_DIR=${SCRIPT_DIR/%\/./}
SRC_DIR="$SCRIPT_DIR/.."

# let's be sure we are in the right place
if [ ! -e "$SRC_DIR/wsdl/eucalyptus_nc.wsdl" ]; then
	echo "Are you in the eucalyptus source directory?"
	exit 1
fi

# where eucalyptus will live: let's re-create it
EUCA_DIR="${TOP_DIR}"
if [ -d $EUCA_DIR  -a "$ACTION" != "uninstall" ]; then
	echo "$EUCA_DIR exists! You need to remove it to compile"
	exit 1
fi

# now let's check if we have to install or uninstall
if [ "$ACTION" = "uninstall" ]; then
	rm -rf $EUCA_DIR
	exit 0
fi

# let's go for the full install
mkdir $EUCA_DIR

# let's get the stuff we need where we need them 
cd $SCRIPT_DIR
wget -q -nd -r -A `uname -m`.tgz  http://open.eucalyptus.com/dependencies/${VERSION}

for x in `/bin/ls *tgz`; do
	(cd $EUCA_DIR; tar xzf $SCRIPT_DIR/$x);
done

# we need to get the Makedefs right: we need the versions of the tools
rm -f $SRC_DIR/Makedefs
for x in `/bin/ls $EUCA_DIR`; do
	if [ "`echo $x | cut -f 1 -d -`" = "axis2" ]; then
		echo "export AXIS2_HOME=$EUCA_DIR/$x" >> ${SRC_DIR}/Makedefs
		export AXIS2_HOME="$EUCA_DIR/$x"
	fi
	if [ "`echo $x | cut -f 1 -d -`" = "axis2c" ]; then
		echo "export AXIS2C_HOME=$EUCA_DIR/$x" >> ${SRC_DIR}/Makedefs
		export AXIS2C_HOME="$EUCA_DIR/$x"
	fi
	if [ "`echo $x | cut -f 1 -d -`" = "libvirt" ]; then
		echo "export LIBVIRT_HOME=$EUCA_DIR/$x" >> ${SRC_DIR}/Makedefs
		export LIBVIRT_HOME="$EUCA_DIR/$x"
	fi
	if [ "`echo $x | cut -f 1 -d -`" = "gwt" ]; then
		echo "export GWT_HOME=$EUCA_DIR/$x" >> ${SRC_DIR}/Makedefs
		export GWT_HOME="$EUCA_DIR/$x"
	fi
done

# let's check we got all parts
if [ -z "$AXIS2C_HOME" -o -z "$GWT_HOME" -o -z "$AXIS2_HOME" -o -z "$LIBVIRT_HOME" ]; then
	echo "Missing needed pacakges!"
	exit 1
fi

# now let's create the directory structure we need
mkdir -p $EUCA_DIR/etc/eucalyptus/cloud.d
mkdir -p $EUCA_DIR/etc/init.d
mkdir -p $EUCA_DIR/var/run/eucalyptus/net
mkdir -p $EUCA_DIR/var/lib/eucalyptus/keys/admin
mkdir -p $EUCA_DIR/var/log/eucalyptus
mkdir -p $EUCA_DIR/usr/sbin
mkdir -p $EUCA_DIR/usr/share/eucalyptus

# let's go for it we build first the C webservices (and deploy them)
cd $SRC_DIR

# it looks like at the time of the shim, we added this dependency too
export EUCALYPTUS="$EUCA_DIR"
export LD_LIBRARY_PATH="${LD_LIBRARY_PATH}:${AXIS2C_HOME}/lib"
make build
make deploy

# now let's work the cloud
cd $SRC_DIR/clc

# let's build the beast
ant deps
ant build

# axis2 and gwt are not needed anylonger, while libvirt has its own rpm
# and only the NC needs it
rm -rf ${AXIS2_HOME}
rm -rf ${LIBVIRT_HOME}
rm -rf ${GWT_HOME}

