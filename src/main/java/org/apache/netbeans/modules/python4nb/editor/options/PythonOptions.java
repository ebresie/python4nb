/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.netbeans.modules.python4nb.editor.options;

import java.util.List;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NullAllowed;
//import org.netbeans.modules.javascript.nodejs.exec.ExpressExecutable;
import org.apache.netbeans.modules.python4nb.util.FileUtils;
import org.apache.netbeans.modules.python4nb.util.PythonUtils;
import org.openide.util.NbPreferences;

/** TODO: Some of this code is based on nbPython code.  Need to 
determine how to handle this.  Either need to get permission/approval
for use or will need to rework/replace it with python4nb equivalent. */

public final class PythonOptions {
    public static String PYTHON_COMMAND = "python.command";
    public static final String PYTHON_PATH = "python.path"; // NOI18N
    public static String PYTHON_DEFAULT = "python.default"; 
    public static final String PYTHON_SOURCES_PATH = "python.sources.path"; // NOI18N
    public static final String PIP_PATH = "pip.path"; // NOI18N
//    public static final String NPM_IGNORE_NODE_MODULES = "npm.ignore.node_modules"; // NOI18N
//    public static final String EXPRESS_PATH = "express.path"; // NOI18N

    // Do not change arbitrary - consult with layer's folder OptionsExport
    // Path to Preferences node for storing these preferences
    public static final String PREFERENCES_PATH = "python.preferences"; // NOI18N

    private final Preferences preferences;
    private String defaultPython = "";
    private static PythonOptions instance;

    private volatile boolean nodeSearched = false;
    private volatile boolean pipSearched = false;
//    private volatile boolean expressSearched = false;


    private PythonOptions() {
        preferences = NbPreferences.forModule(PythonOptions.class)
                .node(PREFERENCES_PATH);
    }

    public static PythonOptions getInstance() {
        if(instance == null)
            instance = new PythonOptions();
        return instance;
    }

    public void addPreferenceChangeListener(PreferenceChangeListener listener) {
        preferences.addPreferenceChangeListener(listener);
    }

    public void removePreferenceChangeListener(PreferenceChangeListener listener) {
        preferences.removePreferenceChangeListener(listener);
    }

    @CheckForNull
    public String getPython() {
        String path = preferences.get(PYTHON_PATH, null);
        if (path == null
                && !nodeSearched) {
            nodeSearched = true;
            path = PythonUtils.getPython();
            if (path != null) {
                setPython(path);
            }
        }
        return path;
    }

    public void setPython(String node) {
        preferences.put(PYTHON_PATH, node);
    }

    @CheckForNull
    public String getPythonSources() {
        return preferences.get(PYTHON_SOURCES_PATH, null);
    }

    public void setPythonSources(@NullAllowed String pythonSources) {
        if (pythonSources == null) {
            preferences.remove(PYTHON_SOURCES_PATH);
        } else {
            preferences.put(PYTHON_SOURCES_PATH, pythonSources);
        }
    }

    @CheckForNull
    public String getPip() {
        String path = preferences.get(PIP_PATH, null);
        if (path == null
                && !pipSearched) {
            pipSearched = true;
            path = PythonUtils.getPip();
            if (path != null) {
                setPip(path);
            }
        }
        return path;
    }

    public void setPip(String pip) {
        preferences.put(PIP_PATH, pip);
    }

//    public boolean isPipIgnorePythonModules() {
////        return preferences.getBoolean(NPM_IGNORE_NODE_MODULES, true);
//        return true;
//    }
//
//    public void setNpmIgnoreNodeModules(boolean npmIgnoreNodeModules) {
////        preferences.putBoolean(NPM_IGNORE_NODE_MODULES, npmIgnoreNodeModules);
//    }

//    @CheckForNull
//    public String getExpress() {
//        String path = preferences.get(EXPRESS_PATH, null);
//        if (path == null
//                && !expressSearched) {
//            expressSearched = true;
//            List<String> files = FileUtils.findFileOnUsersPath(ExpressExecutable.EXPRESS_NAME);
//            if (!files.isEmpty()) {
//                path = files.get(0);
//                setExpress(path);
//            }
//        }
//        return path;
//    }
//
//    public void setExpress(String express) {
//        preferences.put(EXPRESS_PATH, express);
//    }

    public String getPythonDefault() {
        return preferences.get(PYTHON_DEFAULT, defaultPython);
    }

    public void setPythonDefault(String defaultPlatform) {
        preferences.put(PYTHON_DEFAULT, defaultPlatform);
    }
    
    public String getPythonCommand() {
        return preferences.get(PYTHON_COMMAND, defaultPython);
    }
    public void setPythonCommand(String command)  {
        preferences.put(PYTHON_COMMAND, command);
    }

}
