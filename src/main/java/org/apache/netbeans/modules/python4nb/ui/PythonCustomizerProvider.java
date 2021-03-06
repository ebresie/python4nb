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
package org.apache.netbeans.modules.python4nb.ui;

import javax.swing.JComponent;
import org.apache.netbeans.modules.python4nb.preferences.PythonPreferencesValidator;
import org.netbeans.api.project.Project;
//import org.netbeans.modules.javascript.nodejs.preferences.NodeJsPreferencesValidator;
import org.apache.netbeans.modules.python4nb.util.ValidationResult;
//import org.netbeans.modules.web.common.api.ValidationResult;
import org.netbeans.spi.project.ui.CustomizerProvider2;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public final class PythonCustomizerProvider implements ProjectCustomizer.CompositeCategoryProvider {

    public static final String CUSTOMIZER_IDENT = "Python"; // NOI18N


    @NbBundle.Messages("PythonCustomizerProvider.name=Python")
    @Override
    public ProjectCustomizer.Category createCategory(Lookup context) {
        return ProjectCustomizer.Category.create(CUSTOMIZER_IDENT,
                Bundle.PythonCustomizerProvider_name(), null);
    }

    @Override
    public JComponent createComponent(ProjectCustomizer.Category category, Lookup context) {
        Project project = context.lookup(Project.class);
        assert project != null;
        return new PythonCustomizerPanel(category, project);
    }

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
           projectType = "org-netbeans-modules-python4nb-project", // NOI18N
            
            position = 320)
    public static PythonCustomizerProvider createCustomizer() {
        return new PythonCustomizerProvider();
    }

    public static void openCustomizer(Project project, ValidationResult result) {
        openCustomizer(project, PythonPreferencesValidator.getCustomizerCategory(result));
    }

    public static void openCustomizer(Project project, String category) {
        if (project == null) {
            throw new IllegalArgumentException("Invalid project: " + project);
        }
        if (category == null) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }
        CustomizerProvider2 customizerProvider = project.getLookup().lookup(CustomizerProvider2.class);
        assert customizerProvider != null : "CustomizerProvider2 must be found in lookup of " + project.getClass().getName();
        customizerProvider.showCustomizer(category, null);
    }

}
