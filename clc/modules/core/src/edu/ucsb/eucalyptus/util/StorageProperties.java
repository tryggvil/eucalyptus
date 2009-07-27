/*
 * Author: Sunil Soman sunils@cs.ucsb.edu
 */

package edu.ucsb.eucalyptus.util;

import edu.ucsb.eucalyptus.cloud.entities.SystemConfiguration;
import edu.ucsb.eucalyptus.msgs.UpdateStorageConfigurationType;
import org.apache.log4j.Logger;

import java.util.UUID;

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

public class StorageProperties {

    private static Logger LOG = Logger.getLogger( StorageProperties.class );

    public static final String SERVICE_NAME = "StorageController";
    public static String SC_ID = SERVICE_NAME + UUID.randomUUID();
    public static String STORAGE_REF = "vm://StorageInternal";
    public static final String EUCALYPTUS_OPERATION = "EucaOperation";
    public static final String EUCALYPTUS_HEADER = "EucaHeader";
    public static String storageRootDirectory = BaseDirectory.VAR.toString() + "/volumes";
    public static String WALRUS_URL = "http://localhost:8773/services/Walrus";
    public static int MAX_TOTAL_VOLUME_SIZE = 50;
    public static int MAX_TOTAL_SNAPSHOT_SIZE = 50;
    public static int MAX_VOLUME_SIZE = 10;
    public static int MAX_SNAPSHOT_SIZE = 10;
    public static final long GB = 1024*1024*1024;
    public static final long MB = 1024*1024;
    public static final long KB = 1024;
    public static final int TRANSFER_CHUNK_SIZE = 102400;

    static {
        String walrusAt = System.getProperty(WalrusProperties.URL_PROPERTY);
        if(walrusAt != null)
            WALRUS_URL = walrusAt;
    }

    public static void update() {
        try {
            //TODO: This assumes that the SC shares the database with the front end. This is NOT true in general. Fix this thru message passing.
            SystemConfiguration systemConfiguration = EucalyptusProperties.getSystemConfiguration();
            UpdateStorageConfigurationType updateConfig = new UpdateStorageConfigurationType();
            Integer maxTotalVolumeSize = systemConfiguration.getStorageMaxTotalVolumeSizeInGb();
            if(maxTotalVolumeSize != null) {
                if(maxTotalVolumeSize > 0) {
                    MAX_TOTAL_VOLUME_SIZE = maxTotalVolumeSize;
                }
            }

            Integer maxTotalSnapSize = systemConfiguration.getStorageMaxTotalSnapshotSizeInGb();
            if(maxTotalSnapSize != null) {
                if(maxTotalSnapSize > 0) {
                    MAX_TOTAL_SNAPSHOT_SIZE = maxTotalSnapSize;
                }
            }

            Integer maxVolumeSize = systemConfiguration.getStorageMaxVolumeSizeInGB();
            if(maxVolumeSize != null) {
                if(maxVolumeSize > 0) {
                    MAX_VOLUME_SIZE = maxVolumeSize;
                }
            }

   	    storageRootDirectory = systemConfiguration.getStorageVolumesDir();
            updateConfig.setMaxTotalVolumeSize(MAX_TOTAL_VOLUME_SIZE);
            updateConfig.setMaxTotalSnapshotSize(MAX_TOTAL_SNAPSHOT_SIZE);
            updateConfig.setMaxVolumeSize(MAX_VOLUME_SIZE);
            updateConfig.setStorageRootDirectory(storageRootDirectory);
            Messaging.send(STORAGE_REF, updateConfig);
        } catch(Exception ex) {
            LOG.warn(ex.getMessage());
        }
    }

    public enum Status {
        creating, available, pending, completed, failed
    }
}
