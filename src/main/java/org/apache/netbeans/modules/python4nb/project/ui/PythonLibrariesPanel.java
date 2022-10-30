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
import javax.swing.AbstractListModel;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
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

final class PythonLibrariesPanel extends JPanel implements HelpCtx.Provider {

    private final ProjectCustomizer.Category category;
    private final PythonProject project;
    
    // TODO: Update with applicable panel variables
    
//    private final PythonPreferences preferences;
//    final PythonPathPanel pythonPathPanel;
    final     PythonLibrariesPanel pythonPathPanel;

//    private final SpinnerNumberModel debugPortModel;
//
//    // TODO: Update with applicable panel variables
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
    public PythonLibrariesPanel(ProjectCustomizer.Category category, Project project) {
        assert EventQueue.isDispatchThread();
        if (category == null) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }
        if (project == null) {
            throw new IllegalArgumentException("Invalid project: " + project);
        }

        this.category = category;
        this.project = (PythonProject)project;
        this.pythonPathPanel = this;
//    preferences  = PythonSupport.forProject(project).getPreferences();
//        pythonPathPanel = new PythonPathPanel();
//        debugPortModel = new SpinnerNumberModel(65534, 1, 65534, 1);

        initComponents();
        init();
    }

    private void init() {
        
        // TODO determine details to set attributes for panel based on project properties
        
//        libraryDependeciesPanel.add(pythonPathPanel, BorderLayout.CENTER);
//        // init
//        pythonEnabled = preferences.isEnabled();
//        python = preferences.getPython();
//        pythonPathPanel.setPython(python);
//        pythonPathPanel.setPythonSources(preferences.getPythonSources());
//        defaultPython = preferences.isDefaultPython();
        // ui
        validateData();
        // listeners
        ItemListener defaultItemListener = new DefaultItemListener();
        category.setStoreListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveData();
            }
        });
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
        // TODO: ENable all applicable fields when defined
    }

    void validateData() {
        // TODO: Validate panel specific data
        
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
        // TODO Update to save library applicable project properties
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

        dependenceyButtonGroup = new ButtonGroup();
        libraryDependeciesPanel = new JPanel();
        debugPortSpinner = new JSpinner();
        labelLibraryDependencies = new JLabel();
        dependencyScrollPane = new JScrollPane();
        libraryDependencies = new JList<>();

        libraryDependeciesPanel.setLayout(new BorderLayout());

        debugPortSpinner.setEditor(new JSpinner.NumberEditor(debugPortSpinner, "#"));

        ResourceBundle bundle = ResourceBundle.getBundle("org/apache/netbeans/modules/python4nb/project/ui/Bundle"); // NOI18N
        Mnemonics.setLocalizedText(labelLibraryDependencies, bundle.getString("PythonLibrariesPanel.labelLibraryDependencies.text")); // NOI18N

        libraryDependencies.setModel(new AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        dependencyScrollPane.setViewportView(libraryDependencies);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(libraryDependeciesPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(dependencyScrollPane, GroupLayout.PREFERRED_SIZE, 471, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(debugPortSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(labelLibraryDependencies))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelLibraryDependencies)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addComponent(libraryDependeciesPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(debugPortSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dependencyScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JSpinner debugPortSpinner;
    private ButtonGroup dependenceyButtonGroup;
    private JScrollPane dependencyScrollPane;
    private JLabel labelLibraryDependencies;
    private JPanel libraryDependeciesPanel;
    private JList<String> libraryDependencies;
    // End of variables declaration//GEN-END:variables

    //~ Inner classes

    private final class DefaultItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
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
