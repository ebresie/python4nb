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
package org.apache.netbeans.modules.python4nb.util;

import java.io.File;
import org.apache.netbeans.modules.python4nb.platform.PythonSourceRoots;
import org.netbeans.api.annotations.common.NullAllowed;
//import org.netbeans.modules.javascript.nodejs.platform.PythonSourceRoots;
import org.apache.netbeans.modules.python4nb.util.ValidationResult;
import org.apache.netbeans.modules.python4nb.util.ExternalExecutableValidator;
//import org.netbeans.modules.web.common.ui.api.ExternalExecutableValidator;
import org.openide.util.NbBundle;

public final class ValidationUtils {

    public static final String PYTHON_PATH = "python.path"; // NOI18N
    public static final String PYTHON_SOURCES_PATH = "python.sources.path"; // NOI18N
    public static final String PIP_PATH = "pip.path"; // NOI18N
//    public static final String EXPRESS_PATH = "express.path"; // NOI18N


    private ValidationUtils() {
    }

    @NbBundle.Messages("ValidationUtils.python.name=Python")
    public static void validatePython(ValidationResult result, String node) {
        // TODO: Determine if needed for python
//        String warning = ExternalExecutableValidator.validateCommand(node, Bundle.ValidationUtils_node_name());
//        if (warning != null) {
//            result.addWarning(new ValidationResult.Message(PYTHON_PATH, warning));
//        }
    }

    @NbBundle.Messages({
        "ValidationUtils.python.sources.invalid=Python sources must be a directory",
        "# {0} - lib subdirectory",
        "ValidationUtils.python.sources.lib.invalid=Python sources must contain \"{0}\" subdirectory.",
    })
    public static void validatePythonSources(ValidationResult result, @NullAllowed String pythonSources) {
        if (pythonSources == null) {
            return;
        }
        File sources = new File(pythonSources);
        if (!sources.isDirectory()) {
            result.addWarning(new ValidationResult.Message(PYTHON_SOURCES_PATH, Bundle.ValidationUtils_python_sources_invalid()));
        } else if (!new File(sources, PythonSourceRoots.LIB_DIRECTORY).isDirectory()) {
            result.addWarning(new ValidationResult.Message(PYTHON_SOURCES_PATH, Bundle.ValidationUtils_python_sources_lib_invalid(PythonSourceRoots.LIB_DIRECTORY)));
        }
    }

    @NbBundle.Messages("ValidationUtils.pip.name=pip")
    public static void validatePip(ValidationResult result, String pip) {
        String warning = ExternalExecutableValidator.validateCommand(pip, Bundle.ValidationUtils_pip_name());
        if (warning != null) {
            result.addWarning(new ValidationResult.Message(PIP_PATH, warning));
        }
    }

//    @NbBundle.Messages("ValidationUtils.express.name=Express")
//    public static void validateExpress(ValidationResult result, String express) {
//        String warning = ExternalExecutableValidator.validateCommand(express, Bundle.ValidationUtils_express_name());
//        if (warning != null) {
//            result.addWarning(new ValidationResult.Message(EXPRESS_PATH, warning));
//        }
//    }

}
