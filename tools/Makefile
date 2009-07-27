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
# Makefile for eucalyptus/tools
#
#

include ../Makedefs

all:
	@echo nothing to do here

build: all

deploy:

clean:
	rm -f *~ *.o

distclean:
	@rm -rf eucalyptus

install: deploy
	@if test -e /etc/init.d/eucalyptus ; then \
		mv /etc/init.d/eucalyptus /etc/init.d/eucalyptus.old ; \
		echo "Moving away old init.d script"; fi
	@if test -e $(etcdir)/init.d/eucalyptus ; then \
		mv $(etcdir)/init.d/eucalyptus $(etcdir)/init.d/eucalyptus.old ; fi
	@$(INSTALL) -m 755 eucalyptus-cloud $(etcdir)/init.d
	@$(INSTALL) -m 755 eucalyptus-nc $(etcdir)/init.d
	@$(INSTALL) -m 755 eucalyptus-cc $(etcdir)/init.d
	@$(INSTALL) -m 644 httpd.conf $(etcdir)/eucalyptus
	@if test -e $(etcdir)/eucalyptus/eucalyptus.conf ; then \
		cp $(etcdir)/eucalyptus/eucalyptus.conf $(etcdir)/eucalyptus/eucalyptus.conf.old ; fi
	@$(INSTALL) -m 644 eucalyptus.conf $(etcdir)/eucalyptus
	@$(INSTALL) -m 755 add_key.pl $(datarootdir)/eucalyptus
	@$(INSTALL) -m 755 gen_libvirt_xml $(datarootdir)/eucalyptus
	@$(INSTALL) -m 755 gen_kvm_libvirt_xml $(datarootdir)/eucalyptus
	@$(INSTALL) -m 755 detach.pl $(datarootdir)/eucalyptus
	@$(INSTALL) -m 755 partition2disk $(datarootdir)/eucalyptus
	@$(INSTALL) -m 755 get_xen_info $(datarootdir)/eucalyptus/get_xen_info
	@$(INSTALL) -m 755 get_sys_info $(datarootdir)/eucalyptus/get_sys_info
	@$(INSTALL) -m 755 euca_ipt $(datarootdir)/eucalyptus
	@$(INSTALL) -m 755 populate_arp.pl $(datarootdir)/eucalyptus
	@$(INSTALL) -m 755 euca_conf $(sbindir)
	@$(INSTALL) -m 755 euca_sync_key $(sbindir)

