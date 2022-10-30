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

final class PythonLicensePanel extends JPanel implements HelpCtx.Provider {

    private final ProjectCustomizer.Category category;
//    private final PythonPreferences preferences;
//    final PythonPathPanel pythonPathPanel;
//    private final SpinnerNumberModel debugPortModel;

    volatile boolean pythonEnabled;
    volatile boolean defaultPython;
    volatile String python;
    volatile int debugPort;
    volatile boolean syncChanges;
    private Project project;

// TODO: Add @NbBundle.Messages({ }) annotation once fields are defined
//@NbBundle.Messages({ "PythonCustomizerPanel.enabledCheckBox.text=Enables(TBD)", 
//    "PythonCustomizerPanel.configureNodeButton.text=NodeButton(TBD)", 
//    "PythonCustomizerPanel.defaultPythonRadioButton.text=DefaultPython(TBD)", 
//    "PythonCustomizerPanel.customPythonRadioButton.text=Customer(TBD)", 
//    "PythonCustomizerPanel.debugPortLabel.text=Debug Port", 
//    "PythonCustomizerPanel.localDebugInfoLabel.text=Debug Info", 
//    "PythonCustomizerPanel.syncCheckBox.text=Sync"
//})
    public PythonLicensePanel(ProjectCustomizer.Category category, Project project) {
        assert EventQueue.isDispatchThread();
        if (category == null) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }
        if (project == null) {
            throw new IllegalArgumentException("Invalid project: " + project);
        }

        this.category = category;
        this.project = project;
//    preferences  = PythonSupport.forProject(project).getPreferences();
//        pythonPathPanel = new PythonPathPanel();
//        debugPortModel = new SpinnerNumberModel(65534, 1, 65534, 1);

        initComponents();
        init();
    }

    private void init() {
        // TODO: Update with applicable panel variable / attributes when ready
//        licensePanel.add(pythonPathPanel, BorderLayout.CENTER);
        // init
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
//                python = pythonPathPanel.getPython();
//                validateData();
//            }
//        });
//        debugPortModel.addChangeListener(new DefaultChangeListener());
//        syncCheckBox.addItemListener(defaultItemListener);
    }

    void enableAllFields() {
        
        // TODO: Enable fields for panel when fully fleshed out
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
        // TODO: Validate applicable data on license panel
        
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
        // TODO: Update with project properties license details
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

        nodeBbuttonGroup = new ButtonGroup();
        labelLicense = new JLabel();
        licensePanel = new JPanel();
        jScrollPane1 = new JScrollPane();
        jTextArea1 = new JTextArea();

        ResourceBundle bundle = ResourceBundle.getBundle("org/apache/netbeans/modules/python4nb/project/ui/Bundle"); // NOI18N
        Mnemonics.setLocalizedText(labelLicense, bundle.getString("PythonLicensePanel.labelLicense.text")); // NOI18N

        licensePanel.setLayout(new BorderLayout());

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setText(bundle.getString("PythonLicensePanel.jTextArea1.text")); // NOI18N
        jScrollPane1.setViewportView(jTextArea1);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelLicense)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 502, GroupLayout.PREFERRED_SIZE)
                .addGap(76, 76, 76)
                .addComponent(licensePanel, GroupLayout.DEFAULT_SIZE, 9, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(92, 92, 92)
                        .addComponent(licensePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(labelLicense)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(19, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jScrollPane1;
    private JTextArea jTextArea1;
    private JLabel labelLicense;
    private JPanel licensePanel;
    private ButtonGroup nodeBbuttonGroup;
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
