/*
 * Copyright 2022 Eric Bresie and friends. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.netbeans.modules.python4nb.project;

import org.apache.netbeans.modules.python4nb.platform.PythonPlatform;
import org.apache.netbeans.modules.python4nb.platform.PythonPlatformManager;
import org.netbeans.api.project.Project;

/**
 * Python related Utility.
 */
public class PythonProjectUtil {
    
    private PythonProjectUtil() {}
    
    public static PythonProject getProject (final Project project) {
        assert project != null;
        return project.getLookup().lookup(PythonProject.class);
    }
    
    public static PythonPlatform getActivePlatform (final Project project) {
        final PythonProject pp = getProject(project);
        if (pp == null) {
            return null;    //No Python project
        } 
        final PythonPlatformManager manager = PythonPlatformManager.getInstance();

        String platformId = manager.getDefaultPlatform();
        if (platformId == null) {
            return null;    //No Python platform in the IDE
        }
        return manager.getPlatform(platformId);
    }
   
}
