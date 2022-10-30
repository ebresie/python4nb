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
package org.apache.netbeans.modules.python4nb.project.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.nio.file.FileSystems;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.apache.netbeans.modules.python4nb.project.PythonProject;
import org.apache.netbeans.modules.python4nb.project.PythonProjectProperties;

import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.HelpCtx;

final class PythonSourcePanel extends JPanel implements HelpCtx.Provider {

    private final ProjectCustomizer.Category category;
    private final PythonProject project;
    private final PythonProjectProperties properties;
    private String sourcePath;
    private String testPath;
    private String encoding;

//    private final PythonPreferences preferences;
    final PythonSourcePanel pythonPathPanel;
//    private final SpinnerNumberModel debugPortModel;
//
//    volatile boolean pythonEnabled;
//    volatile boolean defaultPython;
//    volatile String python;
//    volatile int debugPort;
//    volatile boolean syncChanges;
    
  
    // TODO: Add @NbBundle.Messages({ }) annotation once fields are defined
//@NbBundle.Messages({ "PythonCustomizerPanel.enabledCheckBox.text=Enables(TBD)", 
//    "PythonCustomizerPanel.configureNodeButton.text=NodeButton(TBD)", 
//    "PythonCustomizerPanel.defaultPythonRadioButton.text=DefaultPython(TBD)", 
//    "PythonCustomizerPanel.customPythonRadioButton.text=Customer(TBD)", 
//    "PythonCustomizerPanel.debugPortLabel.text=Debug Port", 
//    "PythonCustomizerPanel.localDebugInfoLabel.text=Debug Info", 
//    "PythonCustomizerPanel.syncCheckBox.text=Sync"
//})
    public PythonSourcePanel(ProjectCustomizer.Category category, Project project) {
        assert EventQueue.isDispatchThread();
        if (category == null) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }
        if (project == null) {
            throw new IllegalArgumentException("Invalid project: " + project);
        }

        this.category = category;
        this.project = (PythonProject)project;
        this.properties = this.project.getProperties();
        if (!this.properties.getSourceRoots().isEmpty()  ) {
            this.sourcePath = this.properties.getSourceRoots().get(0).first.getPath();
            this.testPath = this.properties.getSourceRoots().get(0).first.getPath(); // TODO: Get "Test Path" when implemented
        } else {
            this.sourcePath = FileSystems.getDefault().getPath("","").toString();
            this.testPath = FileSystems.getDefault().getPath("","").toString();
        }
        this.encoding = this.properties.getEncoding();

        this.pythonPathPanel = this;
        
        // TODO: Determine if use "preferences" or "project properties"
//    preferences  = PythonSupport.forProject(project).getPreferences();
//        pythonPathPanel = new PythonPathPanel();
//        debugPortModel = new SpinnerNumberModel(65534, 1, 65534, 1);

        initComponents();
        init();
    }

    private void init() {
        
        sourcePathField.setText(sourcePath);
        // TODO Handle test code when capability available for now use project folder
        testSourcePathField.setText(sourcePath);

        selectedEncoding.setSelectedItem(properties.getEncoding());
        
//        pythonSourcePathPanel.add(pythonPathPanel, BorderLayout.CENTER);
//        // init
//        pythonEnabled = preferences.isEnabled();
////        enabledCheckBox.setSelected(pythonEnabled);
//        python = preferences.getPython();
//        pythonPathPanel.setPython(python);
//        pythonPathPanel.setPythonSources(preferences.getPythonSources());
//        defaultPython = preferences.isDefaultPython();
//        if (defaultPython) {
//            defaultPythonRadioButton.setSelected(true);
//        } else {
//            customPythonRadioButton.setSelected(true);
//        }
//        debugPortSpinner.setModel(debugPortModel);
//        debugPort = preferences.getDebugPort();
//        debugPortModel.setValue(debugPort);
//        syncChanges = preferences.isSyncEnabled();
//        syncCheckBox.setSelected(syncChanges);
//        // ui
        enableAllFields();
        validateData();
        // listeners
        ItemListener defaultItemListener = new DefaultItemListener();
        category.setStoreListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveData();
            }
        });
//        enabledCheckBox.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//                pythonEnabled = e.getStateChange() == ItemEvent.SELECTED;
//                validateData();
//                enableAllFields();
//            }
//        });
//        defaultPythonRadioButton.addItemListener(defaultItemListener);
//        customPythonRadioButton.addItemListener(defaultItemListener);
//        pythonPathPanel.addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
////                python = pythonPathPanel.getPython();
//                validateData();
//            }
//        });
//        debugPortModel.addChangeListener(new DefaultChangeListener());
//        syncCheckBox.addItemListener(defaultItemListener);
    }

    void enableAllFields() {
        // TODO: Update with all applicable fields for panel
        
        sourcePathField.setEnabled(true);
        testSourcePathField.setEnabled(false);  // TODO enable when functionality in place
        selectedEncoding.setEnabled(true);
        
    }

    void validateData() {
        
        // TODO Validate Data for panel
//        sourcePathField.getText();
//        testSourcePathField.getText();  // TODO enable when functionality in place
//        selectedEncoding.getSelectedItem();
        
        
//        ValidationResult result = new PythonPreferencesValidator()
//                .validateCustomizer(pythonEnabled, defaultPython, python, pythonPathPanel.getPythonSources(), debugPort)
//                .getResult();
//        if (result.hasErrors()) {
//            category.setErrorMessage(result.getFirstErrorMessage());
//            category.setValid(false);
//            return;
//        }
//        if (result.hasWarnings()) {
//            category.setErrorMessage(result.getFirstWarningMessage());
//            category.setValid(true);
//            return;
//        }
        category.setErrorMessage(null);
        category.setValid(true);
    }

    void saveData() {
        
        // TODO: Set Project Properties Source Root 
//        properties.setSourceRoots(sourceRoots);
//            sourcePathField.setText(project.getProjectDirectory().asText());
//            // TODO Handle test code when capability available for now use project folder
//            testSourcePath.setText(project.getProjectDirectory().asText());
//        } catch (IOException ex) {
//            Exceptions.printStackTrace(ex);
//            return;
//        }
//
//        selectedEncoding.setSelectedItem(properties.getEncoding());
        
        // TODO: Determine if "Preferences" or "Project Properties needs to be used here
    }
    
    
    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("org.netbeans.modules.javascript.nodejs.ui.customizer.NodeJsCustomizerPanel"); // NOI18N
    }

 
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nodeBbuttonGroup = new javax.swing.ButtonGroup();
        pythonSourcePathPanel = new javax.swing.JPanel();
        labelSourcePath = new javax.swing.JLabel();
        sourcePathField = new javax.swing.JTextField();
        labelTestSourcePath = new javax.swing.JLabel();
        testSourcePathField = new javax.swing.JTextField();
        labelSourceEncoding = new javax.swing.JLabel();
        selectedEncoding = new javax.swing.JComboBox<>();

        pythonSourcePathPanel.setLayout(new java.awt.BorderLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/apache/netbeans/modules/python4nb/project/ui/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(labelSourcePath, bundle.getString("PythonSourcePanel.labelSourcePath.text")); // NOI18N

        sourcePathField.setText(bundle.getString("PythonSourcePanel.sourcePathField.text")); // NOI18N
        sourcePathField.setToolTipText(bundle.getString("PythonSourcePanel.sourcePathField.toolTipText")); // NOI18N
        sourcePathField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sourcePathFieldActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(labelTestSourcePath, bundle.getString("PythonSourcePanel.labelTestSourcePath.text")); // NOI18N

        testSourcePathField.setText(bundle.getString("PythonSourcePanel.testSourcePathField.text")); // NOI18N
        testSourcePathField.setEnabled(false);
        testSourcePathField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testSourcePathFieldActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(labelSourceEncoding, bundle.getString("PythonSourcePanel.labelSourceEncoding.text")); // NOI18N

        selectedEncoding.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        selectedEncoding.setSelectedItem(selectedEncoding);
        selectedEncoding.setName("encoding"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelSourcePath)
                    .addComponent(labelTestSourcePath)
                    .addComponent(labelSourceEncoding))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(sourcePathField, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                    .addComponent(testSourcePathField)
                    .addComponent(selectedEncoding, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 15, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(pythonSourcePathPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelSourcePath)
                    .addComponent(sourcePathField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTestSourcePath)
                    .addComponent(testSourcePathField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelSourceEncoding)
                    .addComponent(selectedEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(65, 65, 65)
                .addComponent(pythonSourcePathPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void testSourcePathFieldActionPerformed(ActionEvent evt) {//GEN-FIRST:event_testSourcePathFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_testSourcePathFieldActionPerformed

    private void sourcePathFieldActionPerformed(ActionEvent evt) {//GEN-FIRST:event_sourcePathFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sourcePathFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel labelSourceEncoding;
    private javax.swing.JLabel labelSourcePath;
    private javax.swing.JLabel labelTestSourcePath;
    private javax.swing.ButtonGroup nodeBbuttonGroup;
    private javax.swing.JPanel pythonSourcePathPanel;
    private javax.swing.JComboBox<String> selectedEncoding;
    private javax.swing.JTextField sourcePathField;
    private javax.swing.JTextField testSourcePathField;
    // End of variables declaration//GEN-END:variables

    //~ Inner classes

    private final class DefaultItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
//            defaultPython = defaultPythonRadioButton.isSelected();
//            syncChanges = syncCheckBox.isSelected();
            if (e.getStateChange() == ItemEvent.SELECTED) {
                enableAllFields();
                validateData();
            }
        }

    }

    private final class DefaultChangeListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            validateData();
        }

    }

}
