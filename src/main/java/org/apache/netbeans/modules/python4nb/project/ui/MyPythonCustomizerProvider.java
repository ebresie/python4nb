/*
 * Copyright 2022 ebres.
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
package org.apache.netbeans.modules.python4nb.project.ui;

import org.apache.netbeans.modules.python4nb.project.PythonProject;
import org.netbeans.spi.project.ui.CustomizerProvider2;

/**
 *
 * @author ebres
 */
public class MyPythonCustomizerProvider implements CustomizerProvider2 {

    public final PythonProject project ;
    public MyPythonCustomizerProvider(PythonProject project) {
        this.project = project;
    }

    @Override
    public void showCustomizer(String string, String string1) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void showCustomizer() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
