/*
 * Copyright 2022 ebres.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.netbeans.modules.python4nb.platform;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.apache.netbeans.modules.python4nb.api.PythonException;
import org.apache.netbeans.modules.python4nb.api.PythonPlatformListModel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author ebres
 */
@Messages({
         "CTL_PythonPlatformManager_Title=Python Platforms",
         "dialog.title=Python Platforms",
         "dialog.message=The file {0} was invalid.",
         "PythonPlatformPanel.closeButton.AccessibleContext.accessibleName=PythonPlatformPanelCloseAccessName",
         "CTL_Close=Close",
         "Executables=Executables"
})
public class PythonPlatformPanel extends javax.swing.JPanel {

    private final PythonPlatformManager manager;
    private PythonPlatform platform;
    boolean ignoreEvents;
    PythonPlatformListModel platformListModel = new PythonPlatformListModel();
//    private List<PythonPlatform> platformList;

    private static String getMessage(String key) {
        return NbBundle.getMessage(PythonPlatformPanel.class, key);
    }

    /**
     * Creates new form PythonPlatformPanel
     */
    public PythonPlatformPanel() {
        manager = PythonPlatformManager.getInstance();
        initComponents();
        try {
            ignoreEvents = true;
            selectDefaultPlatform();
        } finally {
            ignoreEvents = false;
        }

        platformListModel.refresh();
        manager.addVetoableChangeListener(new VetoableChangeListener() {
            @Override
            public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
                try {
                    ignoreEvents = true;
                    platformListModel.refresh();
                    selectDefaultPlatform();
                } finally {
                    ignoreEvents = false;
                }
            }
        });
    }

    
    static void showPlatformManager() {
        final PythonPlatformPanel platformPanel = new PythonPlatformPanel();
        JButton closeButton = new JButton();
        closeButton.addActionListener((ActionEvent arg0) -> {
            platformPanel.savePlatform();
        });
        // TODO Figure out how to use bundle/messages correctly
        closeButton.getAccessibleContext().setAccessibleDescription(
 getMessage("PythonPlatformPanel.closeButton.AccessibleContext.accessibleName"));
        Mnemonics.setLocalizedText(closeButton,
                NbBundle.getMessage(PythonPlatformPanel.class, "CTL_Close"));
        DialogDescriptor descriptor = new DialogDescriptor(
                platformPanel,
                getMessage("CTL_PythonPlatformManager_Title"), // NOI18N
                true,
                new Object[]{closeButton},
                closeButton,
                DialogDescriptor.DEFAULT_ALIGN,
                new HelpCtx("org.apache.netbeans.modules.python4nb.platform.PythonPlatformPanel"),
                null);
        Dialog dlg = DialogDisplayer.getDefault().createDialog(descriptor);
        dlg.setVisible(true);
        PythonPlatformManager.getInstance().save();
        dlg.dispose();   
    }
       private void updatePlatform() {
        if (ignoreEvents) {
            // We're probably changing the list contents as the result of an asynchronous
            // refresh -- don't use this to store results back into the platform!
            return;
        }

        platform.setInterpreterArgs(pyPlatformMainCommandArgumentsField.getText());
        platform.setName(pyPlatformMainNameField.getText());
        platform.setInterpreterCommand(pyPlatformMainCommandField.getText());
        platform.setInterpreterConsoleComand(pyPlatformMainConsoleCommandField.getText());
// TODO: Decide if need this or rely on other java path when integrating with jython
//        platform.setJavaPath(javaPathModel.getModel());  
// TODO: Need to figure out path details
//        platform.setPythonPath(pythonPathModel.getModel());

        // This shouldn't be necessary anymore -- the platform's -id- doesn't
        // change even though you may have edited properties in it, such as
        // its name
        //manager.addPlatform(platform);
    }

    // TODO: The following code is based on nbPython code
    public void savePlatform() {
        if (platform != null) {
            updatePlatform();
        }
    }

    class ExeFilter extends FileFilter {
        @Override
        public boolean accept(File file) {
            String ext = getExtention(file);
            if (ext == null) {
                return true;
            } else if (ext.equalsIgnoreCase("bat") || ext.equalsIgnoreCase("com") || ext.equalsIgnoreCase("exe")) { // NOI18N
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(PythonPlatformPanel.class, "Executables");
        }

        private String getExtention(File file) {
            String ext = null;
            if (file.isFile()) {
                int pos = file.getName().indexOf(".");
                if (pos > 0) {
                    ext = file.getName().substring(pos + 1);
                }
            }
            return ext;
        }
    }
    
    
    private void selectDefaultPlatform() {
        String defaultName = manager.getDefaultPlatform();
        
        // no default defined yet attempt to discover
        if (defaultName == null) {
            manager.autoDetect();
            // after discovery try to get default platform
            defaultName = manager.getDefaultPlatform();
        }
         
        if (defaultName != null) {
            PythonPlatform defaultPlatform = manager.getPlatform(defaultName);
            if (defaultPlatform != null) {
                platform = defaultPlatform;
                platformPanelList.setSelectedValue(defaultPlatform, true);
                loadPlatform();
            }
        } else {
            NotifyDescriptor message = new NotifyDescriptor.Message(
                "No Python found or installed.", NotifyDescriptor.ERROR_MESSAGE); 
                DialogDisplayer.getDefault().notify(message);
        }
    }
    
    /**
     * This loads the details of the currently in use platform into the 
     * PythonPlatformPanel GUI fields.
     */
      private void loadPlatform() {
        pyPlatformMainNameField.setText(platform.getName());
        pyPlatformMainCommandField.setText(platform.getInterpreterCommand());
        pyPlatformMainConsoleCommandField.setText(platform.getInterpreterConsoleComand());
        pyPlatformMainCommandArgumentsField.setText(platform.getInterpreterArgs());
        // Make copy so we don't muck with the master copy in the platform manager...
//        pythonPathModel.setModel(new ArrayList<>(platform.getPythonPath()));
//        javaPathModel.setModel(new ArrayList<>(platform.getJavaPath()));

    }

    private void clearPlatform() {
        pyPlatformMainNameField.setText("");
        pyPlatformMainCommandField.setText("");
        pyPlatformMainConsoleCommandField.setText("");
        pyPlatformMainCommandArgumentsField.setText("");
//        pythonPathModel.setModel(new ArrayList<String>());
//        javaPathModel.setModel(new ArrayList<String>());

    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pythonPlatformLabel = new javax.swing.JLabel();
        pythonPlatformListPane = new javax.swing.JScrollPane();
        platformPanelList = new javax.swing.JList<>();
        pythonPlatformAddButton = new javax.swing.JButton();
        pythonPlatformRemoveButton = new javax.swing.JButton();
        pythonPlatformAutoDetectButton = new javax.swing.JButton();
        pythonPlatformMakeDefaultButton = new javax.swing.JButton();
        pyPlatformMainPanel = new javax.swing.JTabbedPane();
        pyPlatformMainTabPanel = new javax.swing.JPanel();
        pyPlatformMainNameLabel = new javax.swing.JLabel();
        pyPlatformMainCommandLabel = new javax.swing.JLabel();
        pyPlatformMainConsoleCommandLabel = new javax.swing.JLabel();
        pyPlatformMainCommandArgumentLabel = new javax.swing.JLabel();
        pyPlatformMainNameField = new javax.swing.JTextField();
        pyPlatformMainCommandField = new javax.swing.JTextField();
        pyPlatformMainConsoleCommandField = new javax.swing.JTextField();
        pyPlatformMainCommandArgumentsField = new javax.swing.JTextField();
        pyPlatformModulePanel = new javax.swing.JPanel();
        pyPlatformModulesList = new javax.swing.JScrollPane();
        pyPlatformCloseButton = new javax.swing.JButton();
        pyPlatformHelpButton = new javax.swing.JButton();

        setName("pythonPlatformPanel"); // NOI18N

        pythonPlatformLabel.setText("Platforms");
        pythonPlatformLabel.setName("pythonPlatformLabel"); // NOI18N

        pythonPlatformListPane.setName("pythonPlatformListPane"); // NOI18N

        platformPanelList.setModel(platformListModel);
        platformPanelList.setCellRenderer(new PlatformListCellRenderer());
        platformPanelList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                platformPanelListValueChanged(evt);
            }
        });
        pythonPlatformListPane.setViewportView(platformPanelList);

        pythonPlatformAddButton.setText("Add");
        pythonPlatformAddButton.setName("pyPlatformAddButton"); // NOI18N
        pythonPlatformAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pythonPlatformAddButtonActionPerformed(evt);
            }
        });

        pythonPlatformRemoveButton.setText("Remove");
        pythonPlatformRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pythonPlatformRemoveButtonActionPerformed(evt);
            }
        });

        pythonPlatformAutoDetectButton.setText("Auto Detect");
        pythonPlatformAutoDetectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pythonPlatformAutoDetectButtonActionPerformed(evt);
            }
        });

        pythonPlatformMakeDefaultButton.setText("Make Default");
        pythonPlatformMakeDefaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pythonPlatformMakeDefaultButtonActionPerformed(evt);
            }
        });

        pyPlatformMainPanel.setName("Main"); // NOI18N

        pyPlatformMainTabPanel.setName("Main"); // NOI18N
        pyPlatformMainTabPanel.setPreferredSize(new java.awt.Dimension(601, 310));

        pyPlatformMainNameLabel.setText("Name");

        pyPlatformMainCommandLabel.setText("Command");

        pyPlatformMainConsoleCommandLabel.setText("Console Command");

        pyPlatformMainCommandArgumentLabel.setText("Command Arguments");

        pyPlatformMainNameField.setToolTipText("");
        pyPlatformMainNameField.setName("pythonPlatformName"); // NOI18N
        pyPlatformMainNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pyPlatformMainNameFieldActionPerformed(evt);
            }
        });

        pyPlatformMainCommandField.setName("pythonPlatformCommand"); // NOI18N
        pyPlatformMainCommandField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pyPlatformMainCommandFieldActionPerformed(evt);
            }
        });

        pyPlatformMainConsoleCommandField.setName("pythonPlatformConsoleCommand"); // NOI18N

        pyPlatformMainCommandArgumentsField.setName("pythonPlatformArguments"); // NOI18N
        pyPlatformMainCommandArgumentsField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pyPlatformMainCommandArgumentsFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pyPlatformMainTabPanelLayout = new javax.swing.GroupLayout(pyPlatformMainTabPanel);
        pyPlatformMainTabPanel.setLayout(pyPlatformMainTabPanelLayout);
        pyPlatformMainTabPanelLayout.setHorizontalGroup(
            pyPlatformMainTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pyPlatformMainTabPanelLayout.createSequentialGroup()
                .addGroup(pyPlatformMainTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pyPlatformMainNameLabel)
                    .addComponent(pyPlatformMainCommandLabel)
                    .addComponent(pyPlatformMainConsoleCommandLabel)
                    .addComponent(pyPlatformMainCommandArgumentLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pyPlatformMainTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pyPlatformMainTabPanelLayout.createSequentialGroup()
                        .addGroup(pyPlatformMainTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pyPlatformMainNameField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                            .addComponent(pyPlatformMainCommandField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                            .addComponent(pyPlatformMainCommandArgumentsField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE))
                        .addContainerGap())
                    .addComponent(pyPlatformMainConsoleCommandField, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)))
        );
        pyPlatformMainTabPanelLayout.setVerticalGroup(
            pyPlatformMainTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pyPlatformMainTabPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(pyPlatformMainTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pyPlatformMainNameLabel)
                    .addComponent(pyPlatformMainNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pyPlatformMainTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pyPlatformMainCommandLabel)
                    .addComponent(pyPlatformMainCommandField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pyPlatformMainTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pyPlatformMainConsoleCommandLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pyPlatformMainConsoleCommandField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pyPlatformMainTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pyPlatformMainCommandArgumentLabel)
                    .addComponent(pyPlatformMainCommandArgumentsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(202, Short.MAX_VALUE))
        );

        pyPlatformMainPanel.addTab("Main", pyPlatformMainTabPanel);

        pyPlatformModulesList.setName("pythonPlatformModules"); // NOI18N

        javax.swing.GroupLayout pyPlatformModulePanelLayout = new javax.swing.GroupLayout(pyPlatformModulePanel);
        pyPlatformModulePanel.setLayout(pyPlatformModulePanelLayout);
        pyPlatformModulePanelLayout.setHorizontalGroup(
            pyPlatformModulePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pyPlatformModulePanelLayout.createSequentialGroup()
                .addComponent(pyPlatformModulesList, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
                .addContainerGap())
        );
        pyPlatformModulePanelLayout.setVerticalGroup(
            pyPlatformModulePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pyPlatformModulePanelLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(pyPlatformModulesList, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                .addContainerGap())
        );

        pyPlatformMainPanel.addTab("Modules", pyPlatformModulePanel);

        pyPlatformCloseButton.setText("Close");
        pyPlatformCloseButton.setName("pyPlatformCloseButton"); // NOI18N
        pyPlatformCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pyPlatformCloseAction(evt);
            }
        });

        pyPlatformHelpButton.setEnabled(false);
        pyPlatformHelpButton.setLabel("Help");
        pyPlatformHelpButton.setName("pyPlatformHelpButton"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(pythonPlatformLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(pythonPlatformAddButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(pythonPlatformRemoveButton))
                            .addComponent(pythonPlatformListPane)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(pythonPlatformMakeDefaultButton, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                            .addComponent(pythonPlatformAutoDetectButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pyPlatformMainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(pyPlatformCloseButton)
                .addGap(18, 18, 18)
                .addComponent(pyPlatformHelpButton)
                .addGap(25, 25, 25))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pythonPlatformLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pythonPlatformListPane, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(pythonPlatformAddButton)
                            .addComponent(pythonPlatformRemoveButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pythonPlatformAutoDetectButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pythonPlatformMakeDefaultButton))
                    .addComponent(pyPlatformMainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pyPlatformCloseButton)
                    .addComponent(pyPlatformHelpButton)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pythonPlatformAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pythonPlatformAddButtonActionPerformed
       final JFileChooser fc = new JFileChooser();
        fc.setFileHidingEnabled(false);
        fc.addChoosableFileFilter(new ExeFilter());
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                String cmd = fc.getSelectedFile().getAbsolutePath();
                platform = manager.findPlatformProperties(cmd, null);
                loadPlatform();
                platformListModel.refresh();
            } catch (PythonException ex) {
                NotifyDescriptor message = new NotifyDescriptor.Message(
                        ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(message);
                //Exceptions.printStackTrace(ex);
            }catch(Exception ex){
                NotifyDescriptor message = new NotifyDescriptor.Message(
                        "Invalid Python Type", NotifyDescriptor.ERROR_MESSAGE); // was Invaid and platform_info.py is NOT where expected
                DialogDisplayer.getDefault().notify(message);
            }
        }    }//GEN-LAST:event_pythonPlatformAddButtonActionPerformed

    private void pyPlatformMainCommandFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pyPlatformMainCommandFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pyPlatformMainCommandFieldActionPerformed

    private void pyPlatformMainCommandArgumentsFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pyPlatformMainCommandArgumentsFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pyPlatformMainCommandArgumentsFieldActionPerformed

    private void pythonPlatformRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pythonPlatformRemoveButtonActionPerformed
        int selectedIndex = platformPanelList.getSelectedIndex();
        PythonPlatform selectedPlatform = (PythonPlatform)platformListModel.getElementAt(selectedIndex);
        String  name = selectedPlatform.getName();
        if (selectedIndex != -1) {
            manager.removePlatform( name);
            platformListModel.refresh();
            platform = null;
            clearPlatform();
        }
            }//GEN-LAST:event_pythonPlatformRemoveButtonActionPerformed

    private void pythonPlatformAutoDetectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pythonPlatformAutoDetectButtonActionPerformed
        manager.autoDetect();
        platformListModel.refresh();
    }
//        platformListModel.refresh();    }//GEN-LAST:event_pythonPlatformAutoDetectButtonActionPerformed

    private void pythonPlatformMakeDefaultButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pythonPlatformMakeDefaultButtonActionPerformed
        int selectedIndex = platformPanelList.getSelectedIndex();
        if (selectedIndex != -1) {
            manager.setDefaultPlatform(
                    ((PythonPlatform)platformListModel.getElementAt(
                    selectedIndex)).getId());
            platformListModel.refresh();
        }
    }//GEN-LAST:event_pythonPlatformMakeDefaultButtonActionPerformed

    private void pyPlatformCloseAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pyPlatformCloseAction
        // TODO add your handling code here:
        this.savePlatform();
        
    }//GEN-LAST:event_pyPlatformCloseAction

    private void pyPlatformMainNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pyPlatformMainNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pyPlatformMainNameFieldActionPerformed

    private void platformPanelListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_platformPanelListValueChanged
              if (platform != null) {
            updatePlatform();
        }
        int selectedIndex = platformPanelList.getSelectedIndex();
        if (selectedIndex != -1) {
            platform = (PythonPlatform)platformListModel.getElementAt(selectedIndex);
            loadPlatform();
        }
    }//GEN-LAST:event_platformPanelListValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> platformPanelList;
    private javax.swing.JButton pyPlatformCloseButton;
    private javax.swing.JButton pyPlatformHelpButton;
    private javax.swing.JLabel pyPlatformMainCommandArgumentLabel;
    private javax.swing.JTextField pyPlatformMainCommandArgumentsField;
    private javax.swing.JTextField pyPlatformMainCommandField;
    private javax.swing.JLabel pyPlatformMainCommandLabel;
    private javax.swing.JTextField pyPlatformMainConsoleCommandField;
    private javax.swing.JLabel pyPlatformMainConsoleCommandLabel;
    private javax.swing.JTextField pyPlatformMainNameField;
    private javax.swing.JLabel pyPlatformMainNameLabel;
    private javax.swing.JTabbedPane pyPlatformMainPanel;
    private javax.swing.JPanel pyPlatformMainTabPanel;
    private javax.swing.JPanel pyPlatformModulePanel;
    private javax.swing.JScrollPane pyPlatformModulesList;
    private javax.swing.JButton pythonPlatformAddButton;
    private javax.swing.JButton pythonPlatformAutoDetectButton;
    private javax.swing.JLabel pythonPlatformLabel;
    private javax.swing.JScrollPane pythonPlatformListPane;
    private javax.swing.JButton pythonPlatformMakeDefaultButton;
    private javax.swing.JButton pythonPlatformRemoveButton;
    // End of variables declaration//GEN-END:variables

 
}
