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
#include <handlers.h>
#include <storage.h>
#include <eucalyptus.h>

bunchOfInstances * global_instances = NULL; /* will be initiated upon first call */

int doRunInstance (ncMetadata *meta, char *instanceId, ncInstParams *params, char *imageId, char *imageURL, char *kernelId, char *kernelURL, char *ramdiskId, char *ramdiskURL, char *keyName, ncNetConf *conf, ncInstance **outInst)
{
    ncInstance * instance = NULL;
    * outInst = NULL;
    int error;

    printf ("doRunInstance() invoked (id=%s image=%s)\n", instanceId, imageId);
    if (!(instance = allocate_instance (instanceId, params, imageId, "groovy", NEW, "l33t"))) return 1; /* TODO: return out-of-memory error */
    error = add_instance (&global_instances, instance);
    if ( error ) return error; 

    /* this is a dummy, so it doesn't actually run the instance */

    change_state (instance, BOOTING);
    
    * outInst = instance;
    return 0;
}

int doTerminateInstance (ncMetadata *meta, char *instanceId, int *shutdownState, int *previousState)
{
    ncInstance * instance;

    printf ("doTerminateInstance() invoked (id=%s)\n", instanceId);
    instance = find_instance(&global_instances, instanceId);
    if ( instance == NULL ) return NOT_FOUND;
    * previousState = instance->stateCode;
    change_state (instance, TERMINATING);
    * shutdownState = instance->stateCode;
    
    /* this is a dummy, so it doesn't actually terminate the instance */

    remove_instance (&global_instances, instance);

    /* TODO: fix free_instance (&instance); */

    return 0;
}

int doDescribeResource (ncMetadata *meta, char *resourceType, ncResource **outRes)
{
    printf ("doDescribeResource() invoked\n");

   /* this is a dummy, so it doesn't actually know anything about resources */

    return 0;
}

int doDescribeInstances (ncMetadata *meta, char **instIds, int instIdsLen, ncInstance ***outInsts, int *outInstsLen)
{
    printf ("doDescribeInstances() invoked\n");
    if (instIdsLen == 0) { /* describe all instances */
        ncInstance * instance;
        int i;

        * outInstsLen = total_instances (&global_instances);
        if ( * outInstsLen ) {
            * outInsts = malloc (sizeof(ncInstance *)*(*outInstsLen));
            if ( (* outInsts) == NULL ) return OUT_OF_MEMORY;
            
            for (i=0; (instance = get_instance (&global_instances))!=NULL; i++) {
                (* outInsts)[i] = instance;
            }
        }
        
    } else {
        printf ("specific doDescribeInstances() not implemented\n");
        return 1;
    }
    return 0;
}
