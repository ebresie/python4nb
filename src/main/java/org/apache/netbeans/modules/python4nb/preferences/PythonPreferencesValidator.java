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

package org.apache.netbeans.modules.python4nb.preferences;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.project.Project;
import org.apache.netbeans.modules.python4nb.platform.PythonSupport;
//import org.netbeans.modules.javascript.nodejs.ui.customizer.NodeJsCustomizerProvider;
import org.apache.netbeans.modules.python4nb.util.FileUtils;
import org.apache.netbeans.modules.python4nb.util.ValidationUtils;
//import org.netbeans.modules.web.clientproject.api.WebClientProjectConstants;
import org.apache.netbeans.modules.python4nb.util.ValidationResult;
import org.openide.util.NbBundle;

public final class PythonPreferencesValidator {

    private static final String START_FILE = "start.file"; // NOI18N
    private static final String DEBUG_PORT = "debug.port"; // NOI18N
    public static final String CUSTOMIZER_IDENT = "Python"; // NOI18N
    private final ValidationResult result = new ValidationResult();


    public static String getCustomizerCategory(ValidationResult result) {
//        assert !result.isFaultless() : result.getFirstErrorMessage() + " + " + result.getFirstWarningMessage();
          if (result == null || !result.isFaultless()) {
              throw new IllegalArgumentException("Invalid validation results: " + result);
          }
        List<ValidationResult.Message> messages = new ArrayList<>();
        messages.addAll(result.getErrors());
        messages.addAll(result.getWarnings());
        for (ValidationResult.Message message : messages) {
            switch (message.getSource().toString()) {
                case ValidationUtils.PYTHON_PATH:
                case ValidationUtils.PYTHON_SOURCES_PATH:
//                case DEBUG_PORT:
//                    return NodeJsCustomizerProvider.CUSTOMIZER_IDENT;
//                case START_FILE:
//                    return WebClientProjectConstants.CUSTOMIZER_RUN_IDENT;
                default:
                    assert false : "Unknown validation source: " + message.getSource().toString();
            }
        }
        assert false;
        return /* NodeJsCustomizerProvider.*/ CUSTOMIZER_IDENT;
    }

    public ValidationResult getResult() {
        return result;
    }

    public PythonPreferencesValidator validate(Project project, boolean validateNodeSources) {
        PythonPreferences preferences = PythonSupport.forProject(project).getPreferences();
        if (!preferences.isEnabled()) {
            return this;
        }
        validateNode(preferences.isDefaultPython(), preferences.getPython(), validateNodeSources ? preferences.getPythonSources() : null);
        return this;
    }

    public PythonPreferencesValidator validateNode(String node) {
        ValidationUtils.validatePython(result, node);
        return this;
    }

    public PythonPreferencesValidator validateCustomizer(boolean enabled, boolean defaultNode, String node, String nodeSources, int debugPort) {
        if (!enabled) {
            return this;
        }
        validateNode(defaultNode, node, nodeSources);
        validateDebugPort(debugPort);
        return this;
    }

    @NbBundle.Messages("NodeJsPreferencesValidator.startFile.name=Start file")
    public PythonPreferencesValidator validateRun(String startFile, String args) {
        String warning = FileUtils.validateFile(Bundle.NodeJsPreferencesValidator_startFile_name(), startFile, false);
        if (warning != null) {
            result.addWarning(new ValidationResult.Message(START_FILE, warning));
        }
        return this;
    }

    @NbBundle.Messages("NodeJsPreferencesValidator.debugPort.invalid=Debug port is invalid")
    public PythonPreferencesValidator validateDebugPort(int debugPort) {
        if (debugPort < 0
                || debugPort > 65535) {
            result.addWarning(new ValidationResult.Message(DEBUG_PORT, Bundle.NodeJsPreferencesValidator_debugPort_invalid()));
        }
        return this;
    }

    private void validateNode(boolean defaultNode, String node, String nodeSources) {
        if (defaultNode) {
            return;
        }
        ValidationUtils.validatePython(result, node);
        ValidationUtils.validatePythonSources(result, nodeSources);
    }

}
