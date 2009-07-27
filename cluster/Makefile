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
#
#

include ../Makedefs

WSDLPATH=../wsdl/
CCWSDLNAME=eucalyptus_cc.wsdl
CCWSDL=$(WSDLPATH)/$(CCWSDLNAME)
NCWSDLNAME=eucalyptus_nc.wsdl
NCWSDL=$(WSDLPATH)/$(NCWSDLNAME)
SERVICE_SO=libEucalyptusCC.so
SERVICE_NAME=EucalyptusCC
CLIENT=CCclient
CLIENTKILLALL=euca_killall
#WSDL2C=${AXIS2C_HOME}/bin/tools/wsdl2c/WSDL2C.sh
NCLIBS=../util/data.o ../node/client-marshal-adb.o ../util/ipc.o
SCLIBS=../storage/storage.o
VNLIBS=../net/vnetwork.o ../util/misc.o
WSSECLIBS=../util/euca_auth.o
CC_LIBS 	= ${LIBS} -lcurl

all: server client

build: all

generated/stubs: $(CCWSDL)
	rm -rf generated
	mkdir -p generated
	@echo Generating client stubs
	sh $(WSDL2C) -uri $(CCWSDL) -g -d adb -u -uw -f -o generated | grep -v 'the classpath'
	@echo Generating NCclient stubs
	sh $(WSDL2C) -uri $(NCWSDL) -g -d adb -u -uw -f -o generated | grep -v 'the classpath'
	@echo Generating server stubs
	sh $(WSDL2C) -uri $(CCWSDL) -sd -ss -d adb -u -uw -f -o generated | grep -v 'the classpath'
	@echo Adding marshalling calls to server stubs
	../tools/add_marshalling.pl generated/axis2_skel_*.c
	@echo Compiling the stubs
	cd generated; patch -p1 < ../axis2_svc_generated.patch; $(CC) -c $(CFLAGS) $(INCLUDES) *.c
	@echo "this is a cute little makefile marker" >generated/stubs
	@echo Stubs generated.

.c.o:
	$(CC) -c $(CFLAGS) $(INCLUDES) $<

server: $(SERVICE_SO)

$(SERVICE_SO): generated/stubs ../storage/storage.o server-marshal.o handlers.o $(AXIS2C_HOME)/lib/libaxutil.so
	$(CC) -shared generated/*.o server-marshal.o handlers.o $(SCLIBS) $(NCLIBS) $(VNLIBS) $(WSSECLIBS) $(CC_LIBS) -o $(SERVICE_SO)

client: $(CLIENT)

$(CLIENT): generated/stubs $(CLIENT).c cc-client-marshal-adb.c handlers.o $(AXIS2C_HOME)/lib/libaxutil.so
	$(CC) -o $(CLIENTKILLALL) $(CFLAGS) $(INCLUDES) $(CC_LIBS) generated/adb_*.o generated/axis2_stub_*.o  ../util/misc.o ../util/euca_auth.o $(CLIENT).c cc-client-marshal-adb.c -DMODE=0
	$(CC) -o $(CLIENT)_full $(CFLAGS) $(INCLUDES) $(CC_LIBS) generated/adb_*.o generated/axis2_stub_*.o  ../util/misc.o ../util/euca_auth.o $(CLIENT).c cc-client-marshal-adb.c -DMODE=1

deploy:
	$(INSTALL) -d $(AXIS2C_SERVICES)/$(SERVICE_NAME)/
	$(INSTALL) $(SERVICE_SO) $(AXIS2C_SERVICES)/$(SERVICE_NAME)/
	$(INSTALL) $(CCWSDL) $(AXIS2C_SERVICES)/$(SERVICE_NAME)/
#no ws-sec
	cat generated/services.xml | ../tools/add_wsdllocation.pl $(AXIS2C_SERVICES)/$(SERVICE_NAME)/$(CCWSDLNAME) > $(AXIS2C_SERVICES)/$(SERVICE_NAME)/services_noWS.xml

#ws-sec
	cat generated/services.xml | ../tools/add_wsdllocation.pl $(AXIS2C_SERVICES)/$(SERVICE_NAME)/$(CCWSDLNAME) | ../tools/add_service_policy.pl ../tools/service-policy-template.xml | sed "s:CLIENT-CERT:cloud-cert.pem:g" | sed "s:CLIENT-KEY:cloud-pk.pem:g" | sed "s:SERVER-CERT:cluster-cert.pem:g" | sed "s:SERVER-KEY:cluster-pk.pem:g" | sed "s:CLIENT-USERNAME:eucalyptus:g" > $(AXIS2C_SERVICES)/$(SERVICE_NAME)/services_WS.xml
	cat ../tools/client-policy-template.xml | sed "s:EUCALYPTUS_HOME:$(EUCALYPTUS):g" | sed "s:AXIS2C_HOME:$(AXIS2C_HOME):g" | sed "s:CLIENT-CERT:cloud-cert.pem:g" | sed "s:SERVER-CERT:cluster-cert.pem:g" | sed "s:SERVER-KEY:cluster-pk.pem:g" | sed "s:CLIENT-KEY:cloud-pk.pem:g" | sed "s:CLIENT-USERNAME:eucalyptus:g" > cc-client-policy.xml
##	cp ../tools/*.pem $(EUCALYPTUS)/var/lib/eucalyptus/keys/
#	cp cc-client-policy.xml $(EUCALYPTUS)/var/lib/eucalyptus/keys/
#	chmod -R 0600 $(EUCALYPTUS)/var/lib/eucalyptus/keys/*
# ws
	$(INSTALL) $(AXIS2C_SERVICES)/$(SERVICE_NAME)/services_WS.xml $(AXIS2C_SERVICES)/$(SERVICE_NAME)/services.xml
# no ws
#	cp $(AXIS2C_SERVICES)/$(SERVICE_NAME)/services_noWS.xml $(AXIS2C_SERVICES)/$(SERVICE_NAME)/services.xml

clean:
	rm -f $(SERVICE_SO) *.o $(CLIENTKILLALL) $(CLIENT)_full *~* *#*

distclean:
	rm -rf generated $(SERVICE_SO) *.o $(CLIENT) *~* *#*

install: deploy
	@$(INSTALL) -m 0555 $(CLIENTKILLALL) $(sbindir)
	@$(INSTALL) cc-client-policy.xml $(vardir)/lib/eucalyptus/keys/
