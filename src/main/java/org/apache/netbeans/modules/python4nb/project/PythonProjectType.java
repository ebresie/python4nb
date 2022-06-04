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

import java.io.File;
import java.io.IOException;
import org.netbeans.api.project.Project;

// Portions of this code are based on nbPython Code.  

/**
 * Provides additional attributes and methods relating to Python Project Types.
 * @author ebres
 */
public class PythonProjectType {

    public static final String TYPE = PythonProjectType.class.getPackage().getName();
    public static final String PROJECT_CONFIGURATION_NAMESPACE = "http://nbpython.dev.java.net/ns/php-project/1"; // NOI18N
    private static final String PROJECT_CONFIGURATION_NAME = "data"; // NOI18N

    private static final String PRIVATE_CONFIGURATION_NAMESPACE = "http://nbpython.dev.java.net/ns/php-project-private/1"; // NOI18N
    private static final String PRIVATE_CONFIGURATION_NAME = "data"; // NOI18N
    
    //Probably it should become a part of python api.
    public static final String SOURCES_TYPE_PYTHON = "python"; // NOI18N


    public Project createProject(File root ) throws IOException {
        return new PythonProject(root);
    }

    public String getPrimaryConfigurationDataElementName( boolean shared ) {
        /*
         * Copied from MakeProjectType.
         */
        return shared ? PROJECT_CONFIGURATION_NAME : PRIVATE_CONFIGURATION_NAME;
    }

    public String getPrimaryConfigurationDataElementNamespace( boolean shared ) {
        /*
         * Copied from MakeProjectType.
         */
        return shared ? PROJECT_CONFIGURATION_NAMESPACE : PRIVATE_CONFIGURATION_NAMESPACE;
    }

    public String getType() {
        return TYPE;
    }

}
