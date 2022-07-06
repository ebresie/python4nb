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

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import org.apache.netbeans.modules.python4nb.preferences.PythonPreferencesValidator;
import org.apache.netbeans.modules.python4nb.project.PythonProject;
import org.apache.netbeans.modules.python4nb.project.PythonProjectProperties;
import org.netbeans.api.project.Project;
//import org.netbeans.modules.javascript.nodejs.preferences.NodeJsPreferencesValidator;
import org.apache.netbeans.modules.python4nb.util.ValidationResult;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ui.CustomizerProvider;
//import org.netbeans.modules.web.common.api.ValidationResult;
import org.netbeans.spi.project.ui.CustomizerProvider2;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

public final class PythonCustomizerProvider implements ProjectCustomizer.CompositeCategoryProvider, CustomizerProvider {

    public static final String CUSTOMIZER_IDENT = "Python"; // NOI18N

//    public static final String CUSTOMIZER_FOLDER_PATH = "Projects/org-netbeans-modules-python-project//Customizer"; //NO18N
    public static final String CUSTOMIZER_FOLDER_PATH = "Projects/org-apache-netbeans-modules-python4nb-project//Customizer"; //NO18N
    private static final Map<Project, Dialog> PROJECT_2_DIALOG = new HashMap<>();    
    
    private final PythonProject project;
    
    public PythonCustomizerProvider (final PythonProject project) {
        assert project != null;
        this.project = project;
    }
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
//
//    @ProjectCustomizer.CompositeCategoryProvider.Registration(
//           projectType = "org-netbeans-modules-python4nb-project", // NOI18N
//            
//            position = 320)
//    public static PythonCustomizerProvider createCustomizer() {
//        return new PythonCustomizerProvider();
//    }

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

    @Override
     public void showCustomizer() {
        showCustomizer(null);
    }

    public void showCustomizer(String preselectedCategory) {
        Dialog dialog = PROJECT_2_DIALOG.get(project);
        if (dialog != null) {
            dialog.setVisible(true);
            return;
        }        
        final PythonProjectProperties uiProperties = new PythonProjectProperties(project);
        final Lookup context = Lookups.fixed(project, uiProperties);

        final OptionListener optionListener = new OptionListener(project);
        final StoreListener storeListener = new StoreListener(uiProperties);
        dialog = ProjectCustomizer.createCustomizerDialog(CUSTOMIZER_FOLDER_PATH, context, preselectedCategory,
                optionListener, storeListener, null);
        dialog.addWindowListener(optionListener);
        dialog.setTitle(MessageFormat.format(
                NbBundle.getMessage(PythonCustomizerProvider.class, "LBL_Customizer_Title"),
                ProjectUtils.getInformation(project).getDisplayName()));

        PROJECT_2_DIALOG.put(project, dialog);
        dialog.setVisible(true);
    }
    
    private class StoreListener implements ActionListener {
        
        private final PythonProjectProperties uiProperties;

        StoreListener(PythonProjectProperties uiProperties) {
            this.uiProperties = uiProperties;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            uiProperties.save();
        }
    }

    private static class OptionListener extends WindowAdapter implements ActionListener {
        private final Project project;

        OptionListener(Project project) {
            this.project = project;
        }

        // Listening to OK button ----------------------------------------------
        @Override
        public void actionPerformed( ActionEvent e ) {
            // Close & dispose the the dialog
            Dialog dialog = PROJECT_2_DIALOG.get(project);
            if (dialog != null) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        }

        // Listening to window events ------------------------------------------
        @Override
        public void windowClosed(WindowEvent e) {
            PROJECT_2_DIALOG.remove(project);
        }

        @Override
        public void windowClosing(WindowEvent e) {
            //Dispose the dialog otherwsie the {@link WindowAdapter#windowClosed}
            //may not be called
            Dialog dialog = PROJECT_2_DIALOG.get(project);
            if (dialog != null) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        }
    }


}
