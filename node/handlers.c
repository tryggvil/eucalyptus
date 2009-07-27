/*
Copyright (c) 2009  Eucalyptus Systems, Inc.	

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by 
the Free Software Foundation, only version 3 of the License.  
 
This file is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.  

You should have received a copy of the GNU General Public License along
with this program.  If not, see <http://www.gnu.org/licenses/>.
 
Please contact Eucalyptus Systems, Inc., 130 Castilian
Dr., Goleta, CA 93101 USA or visit <http://www.eucalyptus.com/licenses/> 
if you need additional information or have any questions.

This file may incorporate work covered under the following copyright and
permission notice:

  Software License Agreement (BSD License)

  Copyright (c) 2008, Regents of the University of California
  

  Redistribution and use of this software in source and binary forms, with
  or without modification, are permitted provided that the following
  conditions are met:

    Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.

    Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
  IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
  PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
  OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. USERS OF
  THIS SOFTWARE ACKNOWLEDGE THE POSSIBLE PRESENCE OF OTHER OPEN SOURCE
  LICENSED MATERIAL, COPYRIGHTED MATERIAL OR PATENTED MATERIAL IN THIS
  SOFTWARE, AND IF ANY SUCH MATERIAL IS DISCOVERED THE PARTY DISCOVERING
  IT MAY INFORM DR. RICH WOLSKI AT THE UNIVERSITY OF CALIFORNIA, SANTA
  BARBARA WHO WILL THEN ASCERTAIN THE MOST APPROPRIATE REMEDY, WHICH IN
  THE REGENTS’ DISCRETION MAY INCLUDE, WITHOUT LIMITATION, REPLACEMENT
  OF THE CODE SO IDENTIFIED, LICENSING OF THE CODE SO IDENTIFIED, OR
  WITHDRAWAL OF THE CODE CAPABILITY TO THE EXTENT NEEDED TO COMPLY WITH
  ANY SUCH LICENSES OR RIGHTS.
*/
#include <stdio.h>
#include <stdlib.h>
#include <string.h> /* strlen, strcpy */
#include <time.h>
#include <limits.h> /* INT_MAX */
#include <sys/types.h> /* fork */
#include <sys/wait.h> /* waitpid */
#include <unistd.h>
#include <fcntl.h>
#include <assert.h>
#include <errno.h>
#include <sys/stat.h>
#include <pthread.h>
#include <sys/vfs.h> /* statfs */
#include <signal.h> /* SIGINT */

#include "ipc.h"
#include "misc.h"
#define HANDLERS_FANOUT
#include <handlers.h>
#include <storage.h>
#include <eucalyptus.h>

// declarations of available handlers
extern struct handlers xen_libvirt_handlers;
extern struct handlers kvm_libvirt_handlers;

// a NULL-terminated array of available handlers
static struct handlers * available_handlers [] = {
    &xen_libvirt_handlers,
    &kvm_libvirt_handlers,
    NULL
};

// the chosen handlers
static struct handlers * H = NULL;

static int init (void)
{
    static int initialized = 0;
    if (initialized>0) { /* 0 => hasn't run, -1 => failed, 1 => ok */
        return 0;
    } else if (initialized<0) {
        return 1;
    }

    /* read in configuration - this should be first! */
    int do_warn = 0;
    char * home;
    home = getenv (EUCALYPTUS_ENV_VAR_NAME);
    if (!home) {
        home = strdup (""); /* root by default */
        do_warn = 1;
    } else {
        home = strdup (home);
    }
 
    char config [CHAR_BUFFER_SIZE];
    snprintf(config, CHAR_BUFFER_SIZE, "%s/var/log/eucalyptus/nc.log", home);
    logfile(config, EUCADEBUG); // TODO: right level?
    if (do_warn) 
        logprintfl (EUCAWARN, "env variable %s not set, using /\n", EUCALYPTUS_ENV_VAR_NAME);

    /* from now on we have unrecoverable failure, so no point in retrying
     * to re-init */
    initialized = -1;

    struct stat mystat;
    snprintf(config, CHAR_BUFFER_SIZE, EUCALYPTUS_CONF_LOCATION, home);
    if (stat(config, &mystat)!=0) {
        logprintfl (EUCAFATAL, "could not open configuration file %s\n", config);
        return 1;
    }
    logprintfl (EUCAINFO, "NC is looking for configuration in %s\n", config);
        
    /* determine the hypervisor to use */
    char * hypervisor;
    if (get_conf_var(config, CONFIG_HYPERVISOR, &hypervisor)<1) {
        logprintfl (EUCAFATAL, "value %s is not set in the config file\n", CONFIG_HYPERVISOR);
        return 1;
    }
    struct handlers ** h; 
    for (h = available_handlers; *h; h++ ) {
        if (! strncmp ((*h)->name, hypervisor, CHAR_BUFFER_SIZE) ) { 
            // the name matches!
            H = * h; 
            break;
        }
    }
    if (H==NULL) {
        logprintfl (EUCAFATAL, "requested hypervisor type (%s) is not available\n", hypervisor);
        free (hypervisor);
        return 1;
    }
    free (hypervisor);

    if (H->doInitialize)
        if (H->doInitialize())
            return 1;
    
    initialized = 1;
    return 0;
}

int doDescribeInstances (ncMetadata *meta, char **instIds, int instIdsLen, ncInstance ***outInsts, int *outInstsLen)
{
    if (init()) return 1;
    return H->doDescribeInstances (meta, instIds, instIdsLen, outInsts, outInstsLen);
}

int doRunInstance (ncMetadata *meta, char *instanceId, char *reservationId, ncInstParams *params, char *imageId, char *imageURL, char *kernelId, char *kernelURL, char *ramdiskId, char *ramdiskURL, char *keyName, char *privMac, char *pubMac, int vlan, char *userData, char *launchIndex, char **groupNames, int groupNamesSize, ncInstance **outInst)
{
    if (init()) return 1;
    return H->doRunInstance (meta, instanceId, reservationId, params, imageId, imageURL, kernelId, kernelURL, ramdiskId, ramdiskURL, keyName, privMac, pubMac, vlan, userData, launchIndex, groupNames, groupNamesSize, outInst);
}

int doTerminateInstance (ncMetadata *meta, char *instanceId, int *shutdownState, int *previousState)
{
    if (init()) return 1;
    return H->doTerminateInstance (meta, instanceId, shutdownState, previousState);
}

int doRebootInstance (ncMetadata *meta, char *instanceId) 
{
    if (init()) return 1;
    return H->doRebootInstance (meta, instanceId);
}

int doGetConsoleOutput (ncMetadata *meta, char *instanceId, char **consoleOutput) 
{
    if (init()) return 1;
    return H->doGetConsoleOutput (meta, instanceId, consoleOutput);
}

int doDescribeResource (ncMetadata *meta, char *resourceType, ncResource **outRes)
{
    if (init()) return 1;
    return H->doDescribeResource (meta, resourceType, outRes);
}

int doStartNetwork (ncMetadata *ccMeta, char **remoteHosts, int remoteHostsLen, int port, int vlan)
{
    if (init()) return 1;
    return H->doStartNetwork (ccMeta, remoteHosts, remoteHostsLen, port, vlan);
}

int doAttachVolume (ncMetadata *meta, char *instanceId, char *volumeId, char *remoteDev, char *localDev)
{
    if (init()) return 1;
    return H->doAttachVolume (meta, instanceId, volumeId, remoteDev, localDev);
}

int doDetachVolume (ncMetadata *meta, char *instanceId, char *volumeId, char *remoteDev, char *localDev, int force)
{
    if (init()) return 1;
    return H->doDetachVolume (meta, instanceId, volumeId, remoteDev, localDev, force);
}
