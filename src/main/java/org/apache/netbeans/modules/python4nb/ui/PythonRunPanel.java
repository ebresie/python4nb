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

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.project.Project;
import org.apache.netbeans.modules.python4nb.platform.PythonSupport;
import org.apache.netbeans.modules.python4nb.preferences.PythonPreferences;
import org.apache.netbeans.modules.python4nb.preferences.PythonPreferencesValidator;
import org.apache.netbeans.modules.python4nb.util.PythonUtils;
import org.netbeans.modules.web.clientproject.spi.CustomizerPanelImplementation;
import org.apache.netbeans.modules.python4nb.util.ValidationResult;
import org.openide.awt.Mnemonics;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

public class PythonRunPanel extends JPanel implements CustomizerPanelImplementation {

    public static final String IDENTIFIER = "python"; // NOI18N

    private final Project project;
    private final PythonPreferences preferences;
    private final ChangeSupport changeSupport = new ChangeSupport(this);

    private volatile String startFile;
    private volatile String args;
    private volatile boolean restart;


    public PythonRunPanel(Project project) {
        assert project != null;

        this.project = project;
        preferences = PythonSupport.forProject(project).getPreferences();

        initComponents();
        init();
    }

    private void init() {
        startFile = preferences.getStartFile();
        startFileTextField.setText(startFile);
        args = preferences.getStartArgs();
        argsTextField.setText(args);
        restart = preferences.isRunRestart();
        restartCheckBox.setSelected(restart);
        // listeners
        DocumentListener defaultDocumentListener = new DefaultDocumentListener();
        startFileTextField.getDocument().addDocumentListener(defaultDocumentListener);
        argsTextField.getDocument().addDocumentListener(defaultDocumentListener);
        restartCheckBox.addItemListener(new DefaultItemListener());
        // ui
        if (!PythonUtils.isPythonLibrary(project)) {
            runOnPythonLabel.setVisible(false);
        }
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @NbBundle.Messages("PythonRunPanel.name=Python Application")
    @Override
    public String getDisplayName() {
        return Bundle.PythonRunPanel_name();
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public String getErrorMessage() {
        return validateData().getFirstErrorMessage();
    }

    @NbBundle.Messages("PythonRunPanel.sources.none=Source folder is needed to run project JavaScript files (set it in Sources category).")
    @Override
    public String getWarningMessage() {
        String warning = validateData().getFirstWarningMessage();
        if (warning != null) {
            return warning;
        }
        // #247853
        if (PythonUtils.getSourceRoots(project).isEmpty()) {
            return Bundle.PythonRunPanel_sources_none();
        }
        return null;
    }

    @Override
    public void save() {
        assert !EventQueue.isDispatchThread();
        preferences.setStartFile(startFile);
        preferences.setStartArgs(args);
        preferences.setRunRestart(restart);
    }

    void fireChange() {
        changeSupport.fireChange();
    }

    private ValidationResult validateData() {
        return new PythonPreferencesValidator()
                .validateRun(startFile, args)
                .getResult();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        runOnPythonLabel = new JLabel();
        startFileLabel = new JLabel();
        startFileTextField = new JTextField();
        startFileBrowseButton = new JButton();
        argsLabel = new JLabel();
        argsTextField = new JTextField();
        restartCheckBox = new JCheckBox();

        Mnemonics.setLocalizedText(runOnPythonLabel, NbBundle.getMessage(PythonRunPanel.class, "PythonRunPanel.runOnPythonLabel.text")); // NOI18N

        startFileLabel.setLabelFor(startFileTextField);
        Mnemonics.setLocalizedText(startFileLabel, NbBundle.getMessage(PythonRunPanel.class, "PythonRunPanel.startFileLabel.text")); // NOI18N

        Mnemonics.setLocalizedText(startFileBrowseButton, NbBundle.getMessage(PythonRunPanel.class, "PythonRunPanel.startFileBrowseButton.text")); // NOI18N
        startFileBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                startFileBrowseButtonActionPerformed(evt);
            }
        });

        argsLabel.setLabelFor(argsTextField);
        Mnemonics.setLocalizedText(argsLabel, NbBundle.getMessage(PythonRunPanel.class, "PythonRunPanel.argsLabel.text")); // NOI18N

        Mnemonics.setLocalizedText(restartCheckBox, NbBundle.getMessage(PythonRunPanel.class, "PythonRunPanel.restartCheckBox.text")); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(argsLabel)
                    .addComponent(startFileLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(startFileTextField)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(startFileBrowseButton))
                    .addComponent(argsTextField)))
            .addComponent(runOnPythonLabel)
            .addComponent(restartCheckBox)
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(runOnPythonLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(startFileTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(startFileBrowseButton)
                    .addComponent(startFileLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(argsLabel)
                    .addComponent(argsTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(restartCheckBox))
        );
    }// </editor-fold>//GEN-END:initComponents

    @NbBundle.Messages("PythonRunPanel.browse.title=Select start file")
    private void startFileBrowseButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_startFileBrowseButtonActionPerformed
        assert EventQueue.isDispatchThread();
        FileChooserBuilder fileChooserBuilder = new FileChooserBuilder(PythonRunPanel.class)
                .setFilesOnly(true)
                .setTitle(Bundle.PythonRunPanel_browse_title());
        File sourceRoot = PythonUtils.getSourceRoot(project);
        if (sourceRoot != null) {
            fileChooserBuilder
                .setDefaultWorkingDirectory(sourceRoot)
                .forceUseOfDefaultWorkingDirectory(true);
        }
        File file = fileChooserBuilder.showOpenDialog();
        if (file != null) {
            startFileTextField.setText(file.getAbsolutePath());
        }
    }//GEN-LAST:event_startFileBrowseButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel argsLabel;
    private JTextField argsTextField;
    private JCheckBox restartCheckBox;
    private JLabel runOnPythonLabel;
    private JButton startFileBrowseButton;
    private JLabel startFileLabel;
    private JTextField startFileTextField;
    // End of variables declaration//GEN-END:variables

    //~ Inner classes

    private final class DefaultDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            processChange();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            processChange();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            processChange();
        }

        private void processChange() {
            startFile = startFileTextField.getText();
            args = argsTextField.getText();
            fireChange();
        }

    }

    private final class DefaultItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            restart = restartCheckBox.isSelected();
            fireChange();
        }

    }

}
