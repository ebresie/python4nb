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

import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.apache.netbeans.modules.python4nb.exec.PythonExecutable;
import org.apache.netbeans.modules.python4nb.util.FileUtils;
import org.apache.netbeans.modules.python4nb.util.PythonUtils;
import org.apache.netbeans.modules.python4nb.util.Version;
import org.apache.netbeans.modules.python4nb.util.StringUtils;
//import org.netbeans.modules.javascript.nodejs.util.FileUtils;
//import org.netbeans.modules.javascript.nodejs.util.PythonUtils;
import org.netbeans.modules.web.clientproject.api.network.NetworkException;
//import org.netbeans.modules.web.clientproject.api.util.StringUtilities;
//import org.netbeans.modules.web.common.api.Version;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.Mnemonics;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

public final class PythonPathPanel extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(PythonPathPanel.class.getName());

    private static final RequestProcessor RP = new RequestProcessor(PythonPathPanel.class);

    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private final RequestProcessor.Task versionTask;

    volatile File pythonSources = null;


    public PythonPathPanel() {
        initComponents();
        init();

        versionTask = RP.create(new Runnable() {
            @Override
            public void run() {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        setVersion();
                    }
                });
            }
        });
    }

    @NbBundle.Messages({
        "# {0} - python file name",
        "PythonPathPanel.node.hint1=Full path of node file (typically {0}).",
        "# {0} - python file name",
        "# {1} - python alternative file name",
        "PythonPathPanel.node.hint2=Full path of node file (typically {0} or {1}).",
    })
    private void init() {
        sourcesTextField.setText(" "); // NOI18N
        String[] nodes = PythonExecutable.PYTHON_NAMES;
        if (nodes.length > 1) {
            pythonHintLabel.setText(Bundle.PythonPathPanel_node_hint2(nodes[0], nodes[1]));
        } else {
            pythonHintLabel.setText(Bundle.PythonPathPanel_node_hint1(nodes[0]));
        }
        // listeners
        pythonTextField.getDocument().addDocumentListener(new NodeDocumentListener());
        sourcesTextField.getDocument().addDocumentListener(new DefaultDocumentListener());
    }

    public String getPython() {
        return pythonTextField.getText();
    }

    public void setPython(String python) {
        pythonTextField.setText(python);
    }

    @CheckForNull
    public String getPythonSources() {
        if (pythonSources != null) {
            return pythonSources.getAbsolutePath();
        }
        return null;
    }

    public void setPythonSources(String pythonSources) {
//        if (StringUtilities.hasText(pythonSources)) {
        if ( pythonSources != null ) {
            this.pythonSources = new File(pythonSources);
            setPythonSourcesDescription();
        }
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    public void enablePanel(boolean enabled) {
        assert EventQueue.isDispatchThread();
        pythonLabel.setEnabled(enabled);
        pythonTextField.setEnabled(enabled);
        pythonBrowseButton.setEnabled(enabled);
        pythonSearchButton.setEnabled(enabled);
        pythonHintLabel.setEnabled(enabled);
        pythonInstallLabel.setVisible(enabled);
        sourcesLabel.setEnabled(enabled);
        sourcesTextField.setEnabled(enabled);
        selectSourcesButton.setEnabled(enabled);
        downloadSourcesButton.setEnabled(false);
        if (enabled) {
            if (pythonSources != null) {
                setPythonSourcesDescription();
            }
            setVersion();
        }
    }

    void fireChange() {
        changeSupport.fireChange();
    }

    void detectVersion() {
        versionTask.schedule(100);
    }

    @NbBundle.Messages("PythonPathPanel.version.detecting=Detecting...")
    void setVersion() {
        assert EventQueue.isDispatchThread();
        downloadSourcesButton.setEnabled(false);
        if (pythonSources == null) {
            setPythonSourcesDescription(Bundle.PythonPathPanel_version_detecting());
        }
        final String pythonPath = getPython();
        RP.post(new Runnable() {
            @Override
            public void run() {
                final Version version;
                final Version realVersion;
                PythonExecutable python = PythonExecutable.forPath(pythonPath);
                if (python != null) {
                    version = python.getVersion();
                    realVersion = python.getRealVersion();
                } else {
                    version = null;
                    realVersion = null;
                }
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        assert EventQueue.isDispatchThread();
                        if (version != null) {
                            downloadSourcesButton.setEnabled(true);
                        }
                        if (pythonSources == null) {
                            setPythonSourcesDescription(version, realVersion);
                        }
                    }
                });
            }
        });
    }

    @NbBundle.Messages({
        "# {0} - version",
        "PythonPathPanel.sources.exists=Sources for version {0} already exist. Download again?",
        "PythonPathPanel.sources.downloading=Downloading...",
        "PythonPathPanel.download.success=Python sources downloaded successfully.",
        "# {0} - file URL",
        "PythonPathPanel.download.failure=File {0} cannot be downloaded.",
        "PythonPathPanel.download.error=Error occured during download (see IDE log).",
    })
    private void downloadSources() {
        assert EventQueue.isDispatchThread();
        downloadSourcesButton.setEnabled(false);
        String pythonPath = getPython();
        final PythonExecutable python = PythonExecutable.forPath(pythonPath);
        assert python != null : pythonPath;
        final Version version = python.getVersion();
        assert version != null : pythonPath;
        final Version realVersion = python.getRealVersion();
        assert realVersion != null : version;
        if (PythonUtils.hasPythonSources(version)) {
            pythonSources = null;
            setPythonSourcesDescription(version, realVersion);
            NotifyDescriptor.Confirmation confirmation = new NotifyDescriptor.Confirmation(
                    Bundle.PythonPathPanel_sources_exists(version.toString()),
                    NotifyDescriptor.YES_NO_OPTION);
            if (DialogDisplayer.getDefault().notify(confirmation) == NotifyDescriptor.NO_OPTION) {
                downloadSourcesButton.setEnabled(true);
                return;
            }
        }
        sourcesTextField.setText(Bundle.PythonPathPanel_sources_downloading());
        RP.post(new Runnable() {
            @Override
            public void run() {
                LOGGER.log(Level.WARNING, "Current implementation does not support download.");

                // TODO: For python account for downloading where applicable
//                try {0
//                    if (FileUtils.downloadNodeSources(version, python.isJython())) {
//                        StatusDisplayer.getDefault().setStatusText(Bundle.PythonPathPanel_download_success());
//                    }
//                    pythonSources = null;
//                } catch (NetworkException ex) {
//                    LOGGER.log(Level.INFO, null, ex);
//                    informUser(Bundle.PythonPathPanel_download_failure(ex.getFailedRequests().get(0)));
//                } catch (IOException ex) {
//                    LOGGER.log(Level.INFO, null, ex);
//                    informUser(Bundle.PythonPathPanel_download_error());
//                }
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        setPythonSourcesDescription(version, realVersion);
                        downloadSourcesButton.setEnabled(true);
                    }
                });
            }
        });
    }

    private void informUser(String message) {
        NotifyDescriptor descriptor = new NotifyDescriptor.Message(message, NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notifyLater(descriptor);
    }

    private void setPythonSourcesDescription() {
        assert EventQueue.isDispatchThread();
        File pythonSourcesRef = pythonSources;
        assert pythonSourcesRef != null;
        setPythonSourcesDescription(pythonSourcesRef.getAbsolutePath());
    }

    @NbBundle.Messages({
        "# {0} - python version",
        "PythonPathPanel.sources.downloaded=Downloaded (version {0})",
        "# {0} - real python version",
        "# {1} - python version",
        "PythonPathPanel.sources.es5.downloaded=Downloaded (version {0} -> {1})",
        "# {0} - python version",
        "PythonPathPanel.sources.not.downloaded=Not downloaded (version {0})",
        "# {0} - real python version",
        "# {1} - python version",
        "PythonPathPanel.sources.es5.not.downloaded=Not downloaded (version {0} -> {1})",
        "PythonPathPanel.sources.na=Not available",
    })
    private void setPythonSourcesDescription(@NullAllowed Version version, @NullAllowed Version realVersion) {
        assert EventQueue.isDispatchThread();
        String text;
        if (version == null) {
            text = Bundle.PythonPathPanel_sources_na();
        } else if (PythonUtils.hasPythonSources(version)) {
            if (Objects.equals(version, realVersion)) {
                text = Bundle.PythonPathPanel_sources_downloaded(version);
            } else {
                assert realVersion != null : version;
                text = Bundle.PythonPathPanel_sources_es5_downloaded(realVersion, version);
            }
        } else {
            if (Objects.equals(version, realVersion)) {
                text = Bundle.PythonPathPanel_sources_not_downloaded(version);
            } else {
                assert realVersion != null : version;
                text = Bundle.PythonPathPanel_sources_es5_not_downloaded(realVersion, version);
            }
        }
        setPythonSourcesDescription(text);
    }

    private void setPythonSourcesDescription(String text) {
        assert EventQueue.isDispatchThread();
        sourcesTextField.setText(text);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pythonLabel = new JLabel();
        pythonTextField = new JTextField();
        pythonBrowseButton = new JButton();
        pythonSearchButton = new JButton();
        pythonHintLabel = new JLabel();
        pythonInstallLabel = new JLabel();
        sourcesLabel = new JLabel();
        sourcesTextField = new JTextField();
        downloadSourcesButton = new JButton();
        selectSourcesButton = new JButton();

        Mnemonics.setLocalizedText(pythonLabel, NbBundle.getMessage(PythonPathPanel.class, "PythonPathPanel.pythonLabel.text")); // NOI18N

        Mnemonics.setLocalizedText(pythonBrowseButton, NbBundle.getMessage(PythonPathPanel.class, "PythonPathPanel.pythonBrowseButton.text")); // NOI18N
        pythonBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pythonBrowseButtonActionPerformed(evt);
            }
        });

        Mnemonics.setLocalizedText(pythonSearchButton, NbBundle.getMessage(PythonPathPanel.class, "PythonPathPanel.pythonSearchButton.text")); // NOI18N
        pythonSearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pythonSearchButtonActionPerformed(evt);
            }
        });

        Mnemonics.setLocalizedText(pythonHintLabel, "HINT"); // NOI18N

        Mnemonics.setLocalizedText(pythonInstallLabel, NbBundle.getMessage(PythonPathPanel.class, "PythonPathPanel.pythonInstallLabel.text")); // NOI18N
        pythonInstallLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                pythonInstallLabelMousePressed(evt);
            }
            public void mouseEntered(MouseEvent evt) {
                pythonInstallLabelMouseEntered(evt);
            }
        });

        Mnemonics.setLocalizedText(sourcesLabel, NbBundle.getMessage(PythonPathPanel.class, "PythonPathPanel.sourcesLabel.text")); // NOI18N

        sourcesTextField.setEditable(false);
        sourcesTextField.setColumns(30);

        Mnemonics.setLocalizedText(downloadSourcesButton, NbBundle.getMessage(PythonPathPanel.class, "PythonPathPanel.downloadSourcesButton.text")); // NOI18N
        downloadSourcesButton.setEnabled(false);
        downloadSourcesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                downloadSourcesButtonActionPerformed(evt);
            }
        });

        Mnemonics.setLocalizedText(selectSourcesButton, NbBundle.getMessage(PythonPathPanel.class, "PythonPathPanel.selectSourcesButton.text")); // NOI18N
        selectSourcesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                selectSourcesButtonActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(pythonLabel)
                    .addComponent(sourcesLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pythonTextField)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pythonBrowseButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pythonSearchButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pythonHintLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pythonInstallLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(sourcesTextField, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(downloadSourcesButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectSourcesButton))))
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(pythonLabel)
                    .addComponent(pythonTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(pythonBrowseButton)
                    .addComponent(pythonSearchButton))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(pythonHintLabel)
                    .addComponent(pythonInstallLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(sourcesLabel)
                    .addComponent(downloadSourcesButton)
                    .addComponent(selectSourcesButton)
                    .addComponent(sourcesTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    @NbBundle.Messages("PythonPathPanel.node.browse.title=Select node")
    private void pythonBrowseButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_pythonBrowseButtonActionPerformed
        assert EventQueue.isDispatchThread();
        File file = new FileChooserBuilder(PythonPathPanel.class)
                .setFilesOnly(true)
                .setTitle(Bundle.PythonPathPanel_node_browse_title())
                .showOpenDialog();
        if (file != null) {
            pythonTextField.setText(file.getAbsolutePath());
        }
    }//GEN-LAST:event_pythonBrowseButtonActionPerformed

    @NbBundle.Messages("PythonPathPanel.node.none=No node executable was found.")
    private void pythonSearchButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_pythonSearchButtonActionPerformed
        assert EventQueue.isDispatchThread();
        for (String node : FileUtils.findFileOnUsersPath(PythonExecutable.PYTHON_NAMES)) {
            pythonTextField.setText(new File(node).getAbsolutePath());
            return;
        }
        // no python found
        StatusDisplayer.getDefault().setStatusText(Bundle.PythonPathPanel_node_none());
    }//GEN-LAST:event_pythonSearchButtonActionPerformed

    private void downloadSourcesButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_downloadSourcesButtonActionPerformed
        downloadSources();
    }//GEN-LAST:event_downloadSourcesButtonActionPerformed

    private void pythonInstallLabelMouseEntered(MouseEvent evt) {//GEN-FIRST:event_pythonInstallLabelMouseEntered
        evt.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_pythonInstallLabelMouseEntered

    private void pythonInstallLabelMousePressed(MouseEvent evt) {//GEN-FIRST:event_pythonInstallLabelMousePressed
        try {
            HtmlBrowser.URLDisplayer.getDefault().showURL(new URL("https://python.org/")); // NOI18N
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
    }//GEN-LAST:event_pythonInstallLabelMousePressed

    @NbBundle.Messages("PythonPathPanel.sources.browse.title=Select python sources")
    private void selectSourcesButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_selectSourcesButtonActionPerformed
        assert EventQueue.isDispatchThread();
        File sources = new FileChooserBuilder(PythonPathPanel.class)
                .setDirectoriesOnly(true)
                .setTitle(Bundle.PythonPathPanel_sources_browse_title())
                .showOpenDialog();
        if (sources != null) {
            pythonSources = sources;
            setPythonSourcesDescription();
        }
    }//GEN-LAST:event_selectSourcesButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton downloadSourcesButton;
    private JButton pythonBrowseButton;
    private JLabel pythonHintLabel;
    private JLabel pythonInstallLabel;
    private JLabel pythonLabel;
    private JButton pythonSearchButton;
    private JTextField pythonTextField;
    private JButton selectSourcesButton;
    private JLabel sourcesLabel;
    private JTextField sourcesTextField;
    // End of variables declaration//GEN-END:variables

    //~ Inner classes

    private final class NodeDocumentListener implements DocumentListener {

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
            PythonExecutable node = PythonExecutable.forPath(getPython());
            if (node != null) {
                node.resetVersion();
            }
            detectVersion();
        }

    }

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

}
