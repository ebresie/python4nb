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

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.api.project.Project;
import org.apache.netbeans.modules.python4nb.platform.PythonSupport;
import org.apache.netbeans.modules.python4nb.preferences.PythonPreferences;
import org.apache.netbeans.modules.python4nb.preferences.PythonPreferencesValidator;
import org.apache.netbeans.modules.python4nb.ui.PythonPathPanel;
import org.apache.netbeans.modules.python4nb.ui.options.PythonOptionsPanelController;
import org.apache.netbeans.modules.python4nb.util.ValidationResult;


//import org.netbeans.modules.javascript.nodejs.platform.NodeJsSupport;
//import org.netbeans.modules.javascript.nodejs.preferences.PythonPreferences;
//import org.netbeans.modules.javascript.nodejs.preferences.PythonPreferencesValidator;
//import org.netbeans.modules.javascript.nodejs.ui.PythonPathPanel;
//import org.netbeans.modules.javascript.nodejs.ui.options.NodeJsOptionsPanelController;
//import org.netbeans.modules.web.common.api.ValidationResult;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.awt.Mnemonics;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

final class PythonCustomizerPanel extends JPanel implements HelpCtx.Provider {

    private final ProjectCustomizer.Category category;
    private final PythonPreferences preferences;
    final PythonPathPanel pythonPathPanel;
    private final SpinnerNumberModel debugPortModel;

    volatile boolean pythonEnabled;
    volatile boolean defaultPython;
    volatile String python;
    volatile int debugPort;
    volatile boolean syncChanges;


    public PythonCustomizerPanel(ProjectCustomizer.Category category, Project project) {
        assert EventQueue.isDispatchThread();
        if (category == null) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }
        if (project == null) {
            throw new IllegalArgumentException("Invalid project: " + project);
        }

        this.category = category;
    preferences  = PythonSupport.forProject(project).getPreferences();
        pythonPathPanel = new PythonPathPanel();
        debugPortModel = new SpinnerNumberModel(65534, 1, 65534, 1);

        initComponents();
        init();
    }

    private void init() {
        nodePathPanel.add(pythonPathPanel, BorderLayout.CENTER);
        // init
        pythonEnabled = preferences.isEnabled();
        enabledCheckBox.setSelected(pythonEnabled);
        python = preferences.getPython();
        pythonPathPanel.setPython(python);
        pythonPathPanel.setPythonSources(preferences.getPythonSources());
        defaultPython = preferences.isDefaultPython();
        if (defaultPython) {
            defaultPythonRadioButton.setSelected(true);
        } else {
            customPythonRadioButton.setSelected(true);
        }
        debugPortSpinner.setModel(debugPortModel);
        debugPort = preferences.getDebugPort();
        debugPortModel.setValue(debugPort);
        syncChanges = preferences.isSyncEnabled();
        syncCheckBox.setSelected(syncChanges);
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
        enabledCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                pythonEnabled = e.getStateChange() == ItemEvent.SELECTED;
                validateData();
                enableAllFields();
            }
        });
        defaultPythonRadioButton.addItemListener(defaultItemListener);
        customPythonRadioButton.addItemListener(defaultItemListener);
        pythonPathPanel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                python = pythonPathPanel.getPython();
                validateData();
            }
        });
        debugPortModel.addChangeListener(new DefaultChangeListener());
        syncCheckBox.addItemListener(defaultItemListener);
    }

    void enableAllFields() {
        // default
        defaultPythonRadioButton.setEnabled(pythonEnabled);
        configureNodeButton.setEnabled(pythonEnabled && defaultPython);
        // custom
        customPythonRadioButton.setEnabled(pythonEnabled);
        pythonPathPanel.enablePanel(pythonEnabled && !defaultPython);
        // debug port
        debugPortLabel.setEnabled(pythonEnabled);
        debugPortSpinner.setEnabled(pythonEnabled);
        localDebugInfoLabel.setEnabled(pythonEnabled);
        // sync
        syncCheckBox.setEnabled(pythonEnabled);
    }

    void validateData() {
        ValidationResult result = new PythonPreferencesValidator()
                .validateCustomizer(pythonEnabled, defaultPython, python, pythonPathPanel.getPythonSources(), debugPort)
                .getResult();
        if (result.hasErrors()) {
            category.setErrorMessage(result.getFirstErrorMessage());
            category.setValid(false);
            return;
        }
        if (result.hasWarnings()) {
            category.setErrorMessage(result.getFirstWarningMessage());
            category.setValid(true);
            return;
        }
        category.setErrorMessage(null);
        category.setValid(true);
    }

    void saveData() {
        preferences.setEnabled(pythonEnabled);
        preferences.setPython(python);
        preferences.setNodeSources(pythonPathPanel.getPythonSources());
        preferences.setDefaultNode(defaultPython);
        preferences.setDebugPort(debugPort);
        preferences.setSyncEnabled(syncChanges);
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
        enabledCheckBox = new JCheckBox();
        configureNodeButton = new JButton();
        defaultPythonRadioButton = new JRadioButton();
        customPythonRadioButton = new JRadioButton();
        nodePathPanel = new JPanel();
        debugPortLabel = new JLabel();
        debugPortSpinner = new JSpinner();
        localDebugInfoLabel = new JLabel();
        syncCheckBox = new JCheckBox();

        Mnemonics.setLocalizedText(enabledCheckBox, NbBundle.getMessage(PythonCustomizerPanel.class, "PythonCustomizerPanel.enabledCheckBox.text")); // NOI18N

        Mnemonics.setLocalizedText(configureNodeButton, NbBundle.getMessage(PythonCustomizerPanel.class, "PythonCustomizerPanel.configureNodeButton.text")); // NOI18N
        configureNodeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                configureNodeButtonActionPerformed(evt);
            }
        });

        nodeBbuttonGroup.add(defaultPythonRadioButton);
        Mnemonics.setLocalizedText(defaultPythonRadioButton, NbBundle.getMessage(PythonCustomizerPanel.class, "PythonCustomizerPanel.defaultPythonRadioButton.text")); // NOI18N

        nodeBbuttonGroup.add(customPythonRadioButton);
        Mnemonics.setLocalizedText(customPythonRadioButton, NbBundle.getMessage(PythonCustomizerPanel.class, "PythonCustomizerPanel.customPythonRadioButton.text")); // NOI18N

        nodePathPanel.setLayout(new BorderLayout());

        debugPortLabel.setLabelFor(debugPortSpinner);
        Mnemonics.setLocalizedText(debugPortLabel, NbBundle.getMessage(PythonCustomizerPanel.class, "PythonCustomizerPanel.debugPortLabel.text")); // NOI18N

        debugPortSpinner.setEditor(new JSpinner.NumberEditor(debugPortSpinner, "#"));

        Mnemonics.setLocalizedText(localDebugInfoLabel, NbBundle.getMessage(PythonCustomizerPanel.class, "PythonCustomizerPanel.localDebugInfoLabel.text")); // NOI18N

        Mnemonics.setLocalizedText(syncCheckBox, NbBundle.getMessage(PythonCustomizerPanel.class, "PythonCustomizerPanel.syncCheckBox.text")); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(nodePathPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(defaultPythonRadioButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(configureNodeButton))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(localDebugInfoLabel)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(enabledCheckBox)
                    .addComponent(customPythonRadioButton)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(debugPortLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(debugPortSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(syncCheckBox))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(enabledCheckBox)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(defaultPythonRadioButton)
                    .addComponent(configureNodeButton))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customPythonRadioButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nodePathPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(debugPortLabel)
                    .addComponent(debugPortSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(localDebugInfoLabel)
                .addGap(18, 18, 18)
                .addComponent(syncCheckBox))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void configureNodeButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_configureNodeButtonActionPerformed
        assert EventQueue.isDispatchThread();
        OptionsDisplayer.getDefault().open(PythonOptionsPanelController.OPTIONS_PATH);
    }//GEN-LAST:event_configureNodeButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton configureNodeButton;
    private JRadioButton customPythonRadioButton;
    private JLabel debugPortLabel;
    private JSpinner debugPortSpinner;
    private JRadioButton defaultPythonRadioButton;
    private JCheckBox enabledCheckBox;
    private JLabel localDebugInfoLabel;
    private ButtonGroup nodeBbuttonGroup;
    private JPanel nodePathPanel;
    private JCheckBox syncCheckBox;
    // End of variables declaration//GEN-END:variables

    //~ Inner classes

    private final class DefaultItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            defaultPython = defaultPythonRadioButton.isSelected();
            syncChanges = syncCheckBox.isSelected();
            if (e.getStateChange() == ItemEvent.SELECTED) {
                enableAllFields();
                validateData();
            }
        }

    }

    private final class DefaultChangeListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            debugPort = debugPortModel.getNumber().intValue();
            validateData();
        }

    }

}
