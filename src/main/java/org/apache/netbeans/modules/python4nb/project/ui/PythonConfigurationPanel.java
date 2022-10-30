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
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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

final class PythonConfigurationPanel extends JPanel implements HelpCtx.Provider {

    private final ProjectCustomizer.Category category;
    private final PythonProject project;

//    private final PythonPreferences preferences;
//    final PythonPathPanel pythonPathPanel;
//    private final SpinnerNumberModel debugPortModel;
//
//    volatile boolean pythonEnabled;
//    volatile boolean defaultPython;
//    volatile String python;
//    volatile int debugPort;
//    volatile boolean syncChanges;

    // TODO: Setup panel specific properties with @NbBundle.Messages({}) annotation where applixable
    
//@NbBundle.Messages({ "PythonCustomizerPanel.enabledCheckBox.text=Enables(TBD)", 
//    "PythonCustomizerPanel.configureNodeButton.text=NodeButton(TBD)", 
//    "PythonCustomizerPanel.defaultPythonRadioButton.text=DefaultPython(TBD)", 
//    "PythonCustomizerPanel.customPythonRadioButton.text=Customer(TBD)", 
//    "PythonCustomizerPanel.debugPortLabel.text=Debug Port", 
//    "PythonCustomizerPanel.localDebugInfoLabel.text=Debug Info", 
//    "PythonCustomizerPanel.syncCheckBox.text=Sync"
//})
    public PythonConfigurationPanel(ProjectCustomizer.Category category, Project project) {
        assert EventQueue.isDispatchThread();
        if (category == null) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }
        if (project == null) {
            throw new IllegalArgumentException("Invalid project: " + project);
        }

        this.category = category;
        this.project = (PythonProject)project;
//    preferences  = PythonSupport.forProject(project).getPreferences();
//        pythonPathPanel = new PythonPathPanel();
//        debugPortModel = new SpinnerNumberModel(65534, 1, 65534, 1);

        initComponents();
        init();
    }

    private void init() {
//        configurationPanel.add(pythonPathPanel, BorderLayout.CENTER);
        // init
//        pythonEnabled = preferences.isEnabled();
////        enabledCheckBox.setSelected(pythonEnabled);
//        python = preferences.getPython();
//        pythonPathPanel.setPython(python);
//        pythonPathPanel.setPythonSources(preferences.getPythonSources());
//        defaultPython = preferences.isDefaultPython();
//        // ui
        // ui

        validateData();
        // listeners
        category.setStoreListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveData();
            }
        });
//        pythonPathPanel.addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
//                python = pythonPathPanel.getPython();
//                validateData();
//            }
//        });
    }


    void validateData() {
        // TODO: Setup validation data for PythonCOnfigurationPanel
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
        // TODO: Setup saveData for PythonCOnfigurationPanel

        // 
        PythonProjectProperties properties = project.getProperties();
        final String projectPlatform = projectSelectedPlatform.getSelectedItem().toString();
        properties.setActivePlatformId(projectPlatform);

        // TODO: Account for virtual environment attributes

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
        configurationPanel = new JPanel();
        labelPythonPlatform = new JLabel();
        projectSelectedPlatform = new JComboBox<>();
        projectSelectedEnvironment = new JComboBox<>();
        labelUseVirtualEnvironment = new JCheckBox();

        configurationPanel.setLayout(new BorderLayout());

        ResourceBundle bundle = ResourceBundle.getBundle("org/apache/netbeans/modules/python4nb/project/ui/Bundle"); // NOI18N
        Mnemonics.setLocalizedText(labelPythonPlatform, bundle.getString("PythonConfigurationPanel.labelPythonPlatform.text")); // NOI18N

        projectSelectedPlatform.setModel(new DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        projectSelectedPlatform.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                projectSelectedPlatformActionPerformed(evt);
            }
        });

        projectSelectedEnvironment.setModel(new DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        projectSelectedEnvironment.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                projectSelectedEnvironmentActionPerformed(evt);
            }
        });

        Mnemonics.setLocalizedText(labelUseVirtualEnvironment, bundle.getString("PythonConfigurationPanel.labelUseVirtualEnvironment.text")); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelUseVirtualEnvironment)
                .addGap(18, 18, 18)
                .addComponent(projectSelectedEnvironment, GroupLayout.PREFERRED_SIZE, 292, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(configurationPanel, GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelPythonPlatform)
                .addGap(76, 76, 76)
                .addComponent(projectSelectedPlatform, GroupLayout.PREFERRED_SIZE, 292, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(labelPythonPlatform)
                    .addComponent(projectSelectedPlatform, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(configurationPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(labelUseVirtualEnvironment)
                            .addComponent(projectSelectedEnvironment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                .addGap(130, 130, 130))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void projectSelectedPlatformActionPerformed(ActionEvent evt) {//GEN-FIRST:event_projectSelectedPlatformActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_projectSelectedPlatformActionPerformed

    private void projectSelectedEnvironmentActionPerformed(ActionEvent evt) {//GEN-FIRST:event_projectSelectedEnvironmentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_projectSelectedEnvironmentActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel configurationPanel;
    private JLabel labelPythonPlatform;
    private JCheckBox labelUseVirtualEnvironment;
    private ButtonGroup nodeBbuttonGroup;
    private JComboBox<String> projectSelectedEnvironment;
    private JComboBox<String> projectSelectedPlatform;
    // End of variables declaration//GEN-END:variables

    //~ Inner classes

    private final class DefaultItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
//                enableAllFields();
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
