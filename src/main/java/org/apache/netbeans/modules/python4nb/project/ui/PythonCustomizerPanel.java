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

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.api.project.Project;
import org.apache.netbeans.modules.python4nb.platform.PythonSupport;
import org.apache.netbeans.modules.python4nb.preferences.PythonPreferences;
import org.apache.netbeans.modules.python4nb.preferences.PythonPreferencesValidator;
import org.apache.netbeans.modules.python4nb.project.PythonProject;
import org.apache.netbeans.modules.python4nb.project.PythonProjectProperties;
import org.apache.netbeans.modules.python4nb.ui.PythonPathPanel;
import org.apache.netbeans.modules.python4nb.ui.PythonPathPanel;
import org.apache.netbeans.modules.python4nb.ui.options.PythonOptionsPanelController;
import org.apache.netbeans.modules.python4nb.util.ValidationResult;
import org.netbeans.spi.project.ui.CustomizerProvider2;


//import org.netbeans.modules.javascript.nodejs.platform.NodeJsSupport;
//import org.netbeans.modules.javascript.nodejs.preferences.PythonPreferences;
//import org.netbeans.modules.javascript.nodejs.preferences.PythonPreferencesValidator;
//import org.netbeans.modules.javascript.nodejs.ui.PythonPathPanel;
//import org.netbeans.modules.javascript.nodejs.ui.options.NodeJsOptionsPanelController;
//import org.netbeans.modules.web.common.api.ValidationResult;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.CompositeCategoryProvider;
import org.openide.awt.Mnemonics;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import static org.apache.netbeans.modules.python4nb.project.ui.PythonCustomizerProvider.PYTHON_CATEGORY_IDENT;

final class PythonCustomizerPanel extends JPanel implements HelpCtx.Provider {

    private final ProjectCustomizer.Category category;
    private final PythonProject project;
    private final PythonProjectProperties properties;
    private final PythonCustomizerPanel pythonPathPanel;
//    private final PythonPreferences preferences;
//    private final SpinnerNumberModel debugPortModel;
//
//    volatile boolean pythonEnabled;
//    volatile boolean defaultPython;
//    volatile String python;
//    volatile int debugPort;
//    volatile boolean syncChanges;
    private String projectName;
    private String projectDescription;

// TODO: Add @NbBundle.Messages({ }) annotation once fields are defined
    @NbBundle.Messages({ "PythonCustomizerPanel.enabledCheckBox.text=Enables(TBD)", 
        "PythonCustomizerPanel.configureNodeButton.text=NodeButton(TBD)", 
        "PythonCustomizerPanel.defaultPythonRadioButton.text=DefaultPython(TBD)", 
        "PythonCustomizerPanel.customPythonRadioButton.text=Customer(TBD)", 
        "PythonCustomizerPanel.debugPortLabel.text=Debug Port", 
        "PythonCustomizerPanel.localDebugInfoLabel.text=Debug Info", 
        "PythonCustomizerPanel.syncCheckBox.text=Sync"
    })
    public PythonCustomizerPanel(ProjectCustomizer.Category category, Project project) {
        assert EventQueue.isDispatchThread();
        if (category == null) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }
        if (project == null) {
            throw new IllegalArgumentException("Invalid project: " + project);
        }

        this.category = category;
        this.project = (PythonProject)project;
        this.properties = (PythonProjectProperties)this.project.getProperties();
        // panel specific attributes
        this.projectName = this.properties.getName();
        this.projectDescription  = this.properties.getDescription();
        
        pythonPathPanel = this; //new PythonPathPanel();
//    preferences  = PythonSupport.forProject(project).getPreferences();
//        debugPortModel = new SpinnerNumberModel(65534, 1, 65534, 1);

        initComponents();
        init();
    }

    private void init() {
        // TODO: Updated with General Panel specific variables
        //updates the UI fields with project properties
        this.projectNameField.setText(this.project.getName());
        this.projectDescriptionField.setText(this.project.getDescription());
        
//        nodePathPanel.add(pythonPathPanel, BorderLayout.CENTER);
//        // init
//        pythonEnabled = preferences.isEnabled();
//        enabledCheckBox.setSelected(pythonEnabled);
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
        // ui
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
////                pythonEnabled = e.getStateChange() == ItemEvent.SELECTED;
//                validateData();
//                enableAllFields();
//            }
//        });
//        defaultPythonRadioButton.addItemListener(defaultItemListener);
//        customPythonRadioButton.addItemListener(defaultItemListener);
//        pythonPathPanel.addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
//                python = pythonPathPanel.getPython();
//                validateData();
//            }
//        });
//        debugPortModel.addChangeListener(new DefaultChangeListener());
//        syncCheckBox.addItemListener(defaultItemListener);
    }

    void enableAllFields() {
        // TODO: Enable all PythonGeneralPanel fields
        
//        // default
//        defaultPythonRadioButton.setEnabled(pythonEnabled);
//        configureNodeButton.setEnabled(pythonEnabled && defaultPython);
//        // custom
//        customPythonRadioButton.setEnabled(pythonEnabled);
//        pythonPathPanel.enablePanel(pythonEnabled && !defaultPython);
//        // debug port
//        debugPortLabel.setEnabled(pythonEnabled);
//        debugPortSpinner.setEnabled(pythonEnabled);
//        localDebugInfoLabel.setEnabled(pythonEnabled);
//        // sync
//        syncCheckBox.setEnabled(pythonEnabled);
    }

    void validateData() {
        
        // TODO: Validate Data on PythonGeneralPanel
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
        
//        PythonProjectProperties proj = this.project.getProperties();
        // TODO: Save data for PythonGeneralPanel
        
        // take values from UI fields and save in properties
        properties.setName(this.projectNameField.getText());
        properties.setDescription(this.projectDescriptionField.getText());
        properties.save();
//        proj.setName(this.projectNameField.getText());
//        proj.setDescription(this.projectDescriptionField.getText());
//        proj.save();
        
//        preferences.setEnabled(pythonEnabled);
//        preferences.setPython(python);
//        preferences.setNodeSources(pythonPathPanel.getPythonSources());
//        preferences.setDefaultNode(defaultPython);
//        preferences.setDebugPort(debugPort);
//        preferences.setSyncEnabled(syncChanges);
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

        projectNameLabel = new JLabel();
        projectNameField = new JTextField();
        projectDescriptionLabel = new JLabel();
        projectDescriptionScrollPane = new JScrollPane();
        projectDescriptionField = new JTextArea();

        ResourceBundle bundle = ResourceBundle.getBundle("org/apache/netbeans/modules/python4nb/project/ui/Bundle"); // NOI18N
        Mnemonics.setLocalizedText(projectNameLabel, bundle.getString("PythonCustomizerPanel.projectNameLabel.text")); // NOI18N

        projectNameField.setText(bundle.getString("PythonCustomizerPanel.projectNameField.text")); // NOI18N

        Mnemonics.setLocalizedText(projectDescriptionLabel, bundle.getString("PythonCustomizerPanel.projectDescriptionLabel.text")); // NOI18N

        projectDescriptionScrollPane.setName(""); // NOI18N

        projectDescriptionField.setColumns(20);
        projectDescriptionField.setRows(5);
        projectDescriptionScrollPane.setViewportView(projectDescriptionField);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(projectDescriptionLabel)
                    .addComponent(projectNameLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(projectDescriptionScrollPane, GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
                    .addComponent(projectNameField))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(projectNameLabel)
                    .addComponent(projectNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(projectDescriptionLabel)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(projectDescriptionScrollPane, GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                        .addGap(31, 31, 31))))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JTextArea projectDescriptionField;
    private JLabel projectDescriptionLabel;
    private JScrollPane projectDescriptionScrollPane;
    private JTextField projectNameField;
    private JLabel projectNameLabel;
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
//            debugPort = debugPortModel.getNumber().intValue();
            validateData();
        }

    }

}
