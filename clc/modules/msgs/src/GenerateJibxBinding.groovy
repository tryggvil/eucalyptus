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

import java.lang.reflect.Modifier
/*
 * Author: Chris Grzegorczyk grze@cs.ucsb.edu
 */

def bindingFile = new File("resources/msgs-binding.xml");
bindingFile.write("");
//Enumeration<JarEntry> pathList = (new JarFile("lib/eucalyptus.jar")).entries();
def pathList = new File("build/edu/ucsb/eucalyptus/msgs/").list().toList();
def classList = [];
pathList.each({
              def hithere = it.replace('.class', '');
              classList << "edu.ucsb.eucalyptus.msgs.$hithere";
              });

def binding = {
  ns ->
  def cleanNs = ns.replaceAll('(http://)|(/$)', "").replaceAll("[./-]", "_");
  bindingFile.append "<binding xmlns:euca=\"$ns\" name=\"$cleanNs\">"
  bindingFile.append "  <namespace uri=\"$ns\" default=\"elements\" prefix=\"euca\"/>"
}

def baseMapping = {
  name, className ->
  bindingFile.append "<mapping abstract=\"true\" class=\"$className\">";
}

def childMapping = {
  name, className, extendsName, isAbstract ->
  bindingFile.append "<mapping "
  if ( isAbstract ) bindingFile.append "abstract=\"true\""
  else bindingFile.append "name=\"$name\""
  bindingFile.append " extends=\"$extendsName\" class=\"$className\" >";
  bindingFile.append "    <structure map-as=\"$extendsName\"/>"
}

def valueBind = {
  name ->
  bindingFile.append "    <value style=\"element\" name=\"$name\" field=\"$name\" usage=\"optional\"/>";
}

def typeBind = {
  name, type ->
  bindingFile.append "    <structure name=\"$name\" field=\"$name\" map-as=\"$type\" usage=\"optional\"/>";
}

def stringCollection = {
  name ->
  bindingFile.append "    <structure name=\"$name\" usage=\"optional\"><collection factory=\"org.jibx.runtime.Utility.arrayListFactory\" field=\"$name\" item-type=\"java.lang.String\" usage=\"required\">";
  bindingFile.append "<structure name=\"item\"><value name=\"entry\"/></structure></collection></structure>";
}

def typedCollection = {
  name, itemType ->
  bindingFile.append "    <structure name=\"$name\" usage=\"optional\"><collection field=\"$name\" factory=\"org.jibx.runtime.Utility.arrayListFactory\" usage=\"required\">";
  bindingFile.append "<structure name=\"item\" map-as=\"$itemType\"/></collection></structure>";
}


binding("http://msgs.eucalyptus.ucsb.edu");
classList.each({
               Class itsClass = Class.forName(it);

               if ( itsClass.getSuperclass().getSimpleName().equals("Object") )
                 baseMapping(itsClass.getSimpleName(), itsClass.getName());
               else if ( itsClass.getSuperclass().getSimpleName().equals("EucalyptusData") )
                 childMapping(itsClass.getSimpleName().replaceAll("Type", ""), itsClass.getName(), itsClass.getSuperclass().getName(), true);
               else
                 childMapping(itsClass.getSimpleName().replaceAll("Type", ""), itsClass.getName(), itsClass.getSuperclass().getName(), false);

               def fieldList = itsClass.getDeclaredFields().findAll({Modifier.isPrivate(it.getModifiers())})
               fieldList.each({
                              Class itsType = it.getType();
                              if ( itsType.getSuperclass().equals(edu.ucsb.eucalyptus.msgs.EucalyptusData) ) {
                                typeBind(it.getName(), itsType.getName());
                                } else if ( it.getType().equals(java.util.ArrayList.class) ) {
                                if ( it.getGenericType() != null ) {
                                  if ( it.getGenericType().getActualTypeArguments()[ 0 ].equals(java.lang.String) )
                                    stringCollection(it.getName());
                                  else
                                    typedCollection(it.getName(), it.getGenericType().getActualTypeArguments()[ 0 ].getName());
                                }
                              } else {
                                valueBind(it.getName());
                              }
                              /** date       **/
                              /** arraylist       **/
                              /** other       **/
                              })
               bindingFile.append("</mapping>");

               })
bindingFile.append("</binding>");



