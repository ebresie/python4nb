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

import org.netbeans.api.annotations.common.NullAllowed;
import org.apache.netbeans.modules.python4nb.util.ValidationUtils;
import org.apache.netbeans.modules.python4nb.util.ValidationResult;


public class PythonOptionsValidator {

    private final ValidationResult result = new ValidationResult();


    // do not validate Express since it is really optional
    public PythonOptionsValidator validate(boolean validatePython, boolean includingPythonSources) {
        if (validatePython) {
            PythonOptionsValidator.this.validatePython(includingPythonSources);
        }
        return validatePip();
    }

    public PythonOptionsValidator validatePython(boolean includingPythonSources) {
        PythonOptions pythonOptions = PythonOptions.getInstance();
        return validatePython(pythonOptions.getPython(), includingPythonSources ? pythonOptions.getPythonSources() : null);
    }

    public PythonOptionsValidator validatePython(String node, @NullAllowed String nodeSources) {
        ValidationUtils.validatePython(result, node);
        ValidationUtils.validatePythonSources(result, nodeSources);
        return this;
    }

    /* going to assume Pythons "PIP" used for installing python modules 
    to be similalr to Node Js's NPM and treat as such in python context */
    public PythonOptionsValidator validatePip() {
        return validatePip(PythonOptions.getInstance().getPip());
    }

    public PythonOptionsValidator validatePip(String pip) {
        ValidationUtils.validatePip(result, pip);
        return this;
    }

    // TODO: Determine if need "Python" like "Express elements.  For now will disable
//    public PythonOptionsValidator validateExpress() {
//        return validateExpress(PythonOptions.getInstance().getExpress());
//    }
//
//    public PythonOptionsValidator validateExpress(String express) {
//        ValidationUtils.validateExpress(result, express);
//        return this;
//    }

    public ValidationResult getResult() {
        return result;
    }

}
