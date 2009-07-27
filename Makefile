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
# top-level Eucalyptus makefile
#
#

include Makedefs

# notes: storage has to preceed node and node has to preceed cluster
SUBDIRS			=	tools \
				util \
				net \
				storage	 \
				gatherlog \
				node  \
				cluster \
				clc

# files we are going to package
DIST_FILES		=	CHANGELOG \
				configure \
				eucalyptus.spec.in \
				INSTALL \
				install-sh \
				LICENSE \
				Makedefs.in \
				Makefile \
				README \
				VERSION
DIST_NAME		= $(DIST_DIR).tgz

.PHONY: all clean distclean build dist

all: build

help:
	@echo; echo "Available targets:"
	@echo "   all          this is the default target: it builds eucalyptus"
	@echo "   install      install eucalyptus"
	@echo "   clean        remove objects file and compile by-products"
	@echo "   distclean    restore the source tree to a pristine state"
	@echo 


tags:
	@echo making tags for emacs and vi
	find cluster net node storage tools util -name "*.[chCH]" -print | ctags -L -
	find cluster net node storage tools util -name "*.[chCH]" -print | etags -L -

build: Makedefs 
	@for subdir in $(SUBDIRS); do \
		(cd $$subdir && $(MAKE) $@) || exit $$? ; done

deploy: build
	@for subdir in $(SUBDIRS); do \
		(cd $$subdir && $(MAKE) $@) || exit $$? ; done

install: deploy
	@$(INSTALL) -d $(prefix)
	@$(INSTALL) -d $(etcdir)/eucalyptus/cloud.d
	@$(INSTALL) -m 0644 VERSION $(etcdir)/eucalyptus/eucalyptus-version
	@$(INSTALL) -d $(etcdir)/init.d
	@$(INSTALL) -d $(vardir)/run/eucalyptus/net
	@$(INSTALL) -d $(vardir)/lib/eucalyptus/keys
	@$(INSTALL) -d $(vardir)/log/eucalyptus
	@$(INSTALL) -d $(datarootdir)/eucalyptus
	@$(INSTALL) -d $(usrdir)/sbin
	@$(INSTALL) -d $(usrdir)/lib/eucalyptus
	@for subdir in $(SUBDIRS); do \
		(cd $$subdir && $(MAKE) $@) || exit $$? ; done

dist:
	@rm -rf $(DIST_ROOT) $(DIST_NAME)
	@$(INSTALL) -d $(DIST_ROOT)
	@$(INSTALL) $(DIST_FILES) $(DIST_ROOT)
	@for subdir in $(SUBDIRS); do \
		(cd $$subdir && $(MAKE) $@) || exit $$? ; done

clean:
	@for subdir in $(SUBDIRS); do \
		(cd $$subdir && $(MAKE) $@) || exit $$? ; done

distclean: clean
	@for subdir in $(SUBDIRS); do \
		(cd $$subdir && $(MAKE) $@) || exit $$? ; done
	@rm -f config.cache config.log config.status Makedefs tags TAGS eucalyptus*spec
	@# they where part of CLEAN
	@rm -rf lib 

# the following target is used to remove eucalyptuys from your system
uninstall:
	@echo something to do here


Makedefs: Makedefs.in config.status
	./config.status

config.status: configure
	@if test ! -x ./config.status; then \
		echo "you have to run ./configure!"; exit 1; fi
	./config.status --recheck

# DO NOT DELETE
