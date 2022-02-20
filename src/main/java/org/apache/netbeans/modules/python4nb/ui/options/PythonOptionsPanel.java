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
package org.apache.netbeans.modules.python4nb.ui.options;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
//import org.apache.netbeans.modules.python4nb.ui.options.Bundle;
//import org.apache.netbeans.modules.python4nb.editor.Bundle;
import org.netbeans.api.annotations.common.CheckForNull;
//import org.netbeans.modules.javascript.nodejs.exec.ExpressExecutable;
import org.apache.netbeans.modules.python4nb.exec.PythonExecutable;
import org.apache.netbeans.modules.python4nb.ui.PythonPathPanel;
import org.apache.netbeans.modules.python4nb.util.FileUtils;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.Mnemonics;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

@OptionsPanelController.Keywords(keywords = {"#KW.PythonOptionsPanel"}, location = "Python", tabTitle = "Python")
public final class PythonOptionsPanel extends JPanel implements ChangeListener {

    private static final Logger LOGGER = Logger.getLogger(PythonOptionsPanel.class.getName());

    final PythonPathPanel pythonPanel;
    private final ChangeSupport changeSupport = new ChangeSupport(this);


    private PythonOptionsPanel() {
        assert EventQueue.isDispatchThread();
        pythonPanel = new PythonPathPanel();
        initComponents();
        init();
    }

    public static PythonOptionsPanel create() {
        PythonOptionsPanel panel = new PythonOptionsPanel();
        panel.pythonPanel.addChangeListener(panel);
        return panel;
    }

    @NbBundle.Messages({
        "# {0} - pip file name",
        "PythonOptionsPanel.pip.hint=Full path of pip file (typically {0}).",
        "# {0} - express file name",
        "PythonOptionsPanel.express.hint=Full path of express file (typically {0}).",
    })
    private void init() {
//        errorLabel.setText(" "); // NOI18N
//        pythonHintLabel.setText(Bundle.PythonOptionsPanel_pip_hint(NpmExecutable.NPM_NAME));
//        expressHintLabel.setText(Bundle.PythonOptionsPanel_express_hint(ExpressExecutable.EXPRESS_NAME));
        pythonPanelHolder.add(pythonPanel, BorderLayout.CENTER);
        DefaultItemListener defaultItemListener = new DefaultItemListener();
        stopAtFirstLineCheckBox.addItemListener(defaultItemListener);
        liveEditCheckBox.addItemListener(defaultItemListener);
//        pipIgnorePythonModulesCheckBox.addItemListener(defaultItemListener);
        DocumentListener defaultDocumentListener = new DefaultDocumentListener();
        pythonTextField.getDocument().addDocumentListener(defaultDocumentListener);
//        expressTextField.getDocument().addDocumentListener(defaultDocumentListener);
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    public void setError(String message) {
        errorLabel.setText(" "); // NOI18N
        errorLabel.setForeground(UIManager.getColor("nb.errorForeground")); // NOI18N
        errorLabel.setText(message);
    }

    public void setWarning(String message) {
        errorLabel.setText(" "); // NOI18N
        errorLabel.setForeground(UIManager.getColor("nb.warningForeground")); // NOI18N
        errorLabel.setText(message);
    }

    public String getPython() {
        return pythonPanel.getPython();
    }

    public void setPython(String node) {
        pythonPanel.setPython(node);
    }

    @CheckForNull
    public String getPythonSources() {
        return pythonPanel.getPythonSources();
    }

    public void setPythonSources(String nodeSources) {
        pythonPanel.setPythonSources(nodeSources);
    }

    public boolean isStopAtFirstLine() {
        return stopAtFirstLineCheckBox.isSelected();
    }

    public void setStopAtFirstLine(boolean stopAtFirstLine) {
        stopAtFirstLineCheckBox.setSelected(stopAtFirstLine);
    }

    public boolean isLiveEdit() {
        return liveEditCheckBox.isSelected();
    }

    public void setLiveEdit(boolean liveEdit) {
        liveEditCheckBox.setSelected(liveEdit);
    }

    public String getPip() {
        return pythonTextField.getText();
    }

    public void setPip(String pip) {
        pythonTextField.setText(pip);
    }

//    public boolean isPipIgnorePythonModules() {
//        return pipIgnorePythonModulesCheckBox.isSelected();
//    }
//
//    public void setNpmIgnoreNodeModules(boolean pipIgnoreNodeModules) {
//        pipIgnorePythonModulesCheckBox.setSelected(pipIgnoreNodeModules);
//    }

//    public String getExpress() {
//        return expressTextField.getText();
//    }
//
//    public void setExpress(String express) {
//        expressTextField.setText(express);
//    }

    void fireChange() {
        changeSupport.fireChange();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        fireChange();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pythonPlatformFrame = new JInternalFrame();
        pythonPanelHolder = new JPanel();
        pythonHeaderLabel = new JLabel();
        pythonSeparator = new JSeparator();
        pythonLabel = new JLabel();
        pythonTextField = new JTextField();
        pythonBrowseButton = new JButton();
        pythonSearchButton = new JButton();
        pythonDebugFrame = new JInternalFrame();
        debuggingLabel = new JLabel();
        debuggingSeparator = new JSeparator();
        stopAtFirstLineCheckBox = new JCheckBox();
        liveEditCheckBox = new JCheckBox();
        liveEditInfo1Label = new JLabel();
        liveEditInfo2Label = new JLabel();
        errorLabel = new JLabel();

        pythonPlatformFrame.setVisible(true);

        pythonPanelHolder.setLayout(new BorderLayout());

        Mnemonics.setLocalizedText(pythonHeaderLabel, NbBundle.getMessage(PythonOptionsPanel.class, "PythonOptionsPanel.pythonHeaderLabel.text")); // NOI18N

        Mnemonics.setLocalizedText(pythonLabel, NbBundle.getMessage(PythonOptionsPanel.class, "PythonOptionsPanel.pythonLabel.text")); // NOI18N

        pythonTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pythonTextFieldActionPerformed(evt);
            }
        });

        Mnemonics.setLocalizedText(pythonBrowseButton, NbBundle.getMessage(PythonOptionsPanel.class, "PythonOptionsPanel.pythonBrowseButton.text")); // NOI18N
        pythonBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pythonBrowseButtonActionPerformed(evt);
            }
        });

        Mnemonics.setLocalizedText(pythonSearchButton, NbBundle.getMessage(PythonOptionsPanel.class, "PythonOptionsPanel.pythonSearchButton.text")); // NOI18N
        pythonSearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pythonSearchButtonActionPerformed(evt);
            }
        });

        GroupLayout pythonPlatformFrameLayout = new GroupLayout(pythonPlatformFrame.getContentPane());
        pythonPlatformFrame.getContentPane().setLayout(pythonPlatformFrameLayout);
        pythonPlatformFrameLayout.setHorizontalGroup(pythonPlatformFrameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pythonPlatformFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pythonPlatformFrameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(pythonPlatformFrameLayout.createSequentialGroup()
                        .addComponent(pythonLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pythonTextField, GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pythonBrowseButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pythonSearchButton))
                    .addGroup(pythonPlatformFrameLayout.createSequentialGroup()
                        .addComponent(pythonHeaderLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pythonSeparator))
                    .addComponent(pythonPanelHolder, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pythonPlatformFrameLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {pythonBrowseButton, pythonSearchButton});

        pythonPlatformFrameLayout.setVerticalGroup(pythonPlatformFrameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pythonPlatformFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pythonPanelHolder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pythonPlatformFrameLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(pythonHeaderLabel)
                    .addComponent(pythonSeparator, GroupLayout.PREFERRED_SIZE, 8, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pythonPlatformFrameLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(pythonLabel)
                    .addComponent(pythonTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(pythonSearchButton)
                    .addComponent(pythonBrowseButton))
                .addContainerGap())
        );

        pythonDebugFrame.setVisible(true);

        Mnemonics.setLocalizedText(debuggingLabel, NbBundle.getMessage(PythonOptionsPanel.class, "PythonOptionsPanel.debuggingLabel.text")); // NOI18N

        Mnemonics.setLocalizedText(stopAtFirstLineCheckBox, NbBundle.getMessage(PythonOptionsPanel.class, "PythonOptionsPanel.stopAtFirstLineCheckBox.text")); // NOI18N

        Mnemonics.setLocalizedText(liveEditCheckBox, NbBundle.getMessage(PythonOptionsPanel.class, "PythonOptionsPanel.liveEditCheckBox.text")); // NOI18N

        Mnemonics.setLocalizedText(liveEditInfo1Label, NbBundle.getMessage(PythonOptionsPanel.class, "PythonOptionsPanel.liveEditInfo1Label.text")); // NOI18N

        Mnemonics.setLocalizedText(liveEditInfo2Label, NbBundle.getMessage(PythonOptionsPanel.class, "PythonOptionsPanel.liveEditInfo2Label.text")); // NOI18N

        GroupLayout pythonDebugFrameLayout = new GroupLayout(pythonDebugFrame.getContentPane());
        pythonDebugFrame.getContentPane().setLayout(pythonDebugFrameLayout);
        pythonDebugFrameLayout.setHorizontalGroup(pythonDebugFrameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pythonDebugFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pythonDebugFrameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(pythonDebugFrameLayout.createSequentialGroup()
                        .addComponent(debuggingLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(debuggingSeparator))
                    .addGroup(pythonDebugFrameLayout.createSequentialGroup()
                        .addGroup(pythonDebugFrameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(liveEditCheckBox)
                            .addGroup(pythonDebugFrameLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addGroup(pythonDebugFrameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(liveEditInfo2Label)
                                    .addComponent(liveEditInfo1Label)))
                            .addComponent(stopAtFirstLineCheckBox))
                        .addGap(0, 115, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pythonDebugFrameLayout.setVerticalGroup(pythonDebugFrameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pythonDebugFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pythonDebugFrameLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(debuggingLabel)
                    .addComponent(debuggingSeparator, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stopAtFirstLineCheckBox)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(liveEditCheckBox)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(liveEditInfo1Label)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(liveEditInfo2Label)
                .addContainerGap())
        );

        Mnemonics.setLocalizedText(errorLabel, "ERROR"); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(pythonDebugFrame)
            .addComponent(pythonPlatformFrame)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(errorLabel)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pythonPlatformFrame, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pythonDebugFrame, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(errorLabel)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    @NbBundle.Messages("PythonOptionsPanel.pip.browse.title=Select pip")
    private void pythonBrowseButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_pythonBrowseButtonActionPerformed
        assert EventQueue.isDispatchThread();
        File file = new FileChooserBuilder(PythonOptionsPanel.class)
                .setFilesOnly(true)
                .setTitle(Bundle.PythonOptionsPanel_pip_browse_title())
                .showOpenDialog();
        if (file != null) {
            pythonTextField.setText(file.getAbsolutePath());
        }
    }//GEN-LAST:event_pythonBrowseButtonActionPerformed

    @NbBundle.Messages("PythonOptionsPanel.pip.none=No pip executable was found.")
    private void pythonSearchButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_pythonSearchButtonActionPerformed
        assert EventQueue.isDispatchThread();
        for (String pip : FileUtils.findFileOnUsersPath(PythonExecutable.PYTHON_NAMES)) {
            pythonTextField.setText(new File(pip).getAbsolutePath());
            return;
        }
        // no pip found
        StatusDisplayer.getDefault().setStatusText(Bundle.PythonOptionsPanel_pip_none());
    }//GEN-LAST:event_pythonSearchButtonActionPerformed

    private void pythonTextFieldActionPerformed(ActionEvent evt) {//GEN-FIRST:event_pythonTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pythonTextFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel debuggingLabel;
    private JSeparator debuggingSeparator;
    private JLabel errorLabel;
    private JCheckBox liveEditCheckBox;
    private JLabel liveEditInfo1Label;
    private JLabel liveEditInfo2Label;
    private JButton pythonBrowseButton;
    private JInternalFrame pythonDebugFrame;
    private JLabel pythonHeaderLabel;
    private JLabel pythonLabel;
    private JPanel pythonPanelHolder;
    private JInternalFrame pythonPlatformFrame;
    private JButton pythonSearchButton;
    private JSeparator pythonSeparator;
    private JTextField pythonTextField;
    private JCheckBox stopAtFirstLineCheckBox;
    // End of variables declaration//GEN-END:variables

    //~ Inner classes

    private final class DefaultDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            processUpdate();
        }

        private void processUpdate() {
            fireChange();
        }

    }

    private final class DefaultItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            fireChange();
        }

    }

}
