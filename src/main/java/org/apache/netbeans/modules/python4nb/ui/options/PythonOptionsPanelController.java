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

import java.awt.EventQueue;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.netbeans.modules.python4nb.editor.options.PythonOptions;
import org.apache.netbeans.modules.python4nb.editor.options.PythonOptionsValidator;
import org.apache.netbeans.modules.python4nb.util.ValidationResult;
// TODO: Add more Python Debuging options (and imports as applicable)
//import org.netbeans.modules.javascript.v8debug.api.DebuggerOptions;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;


@NbBundle.Messages("PythonOptionsPanelController.name=Python")
@OptionsPanelController.SubRegistration(
    location = PythonOptionsPanelController.OPTIONS_CATEGORY,
    id = PythonOptionsPanelController.OPTIONS_SUBCATEGORY,
    displayName = "#PythonOptionsPanelController.name" // NOI18N
)
public final class PythonOptionsPanelController extends OptionsPanelController implements ChangeListener {

    public static final String OPTIONS_CATEGORY = "Python"; // NOI18N
    // TODO: May not need subcategory for Python
    public static final String OPTIONS_SUBCATEGORY = "Python"; // NOI18N
    public static final String OPTIONS_PATH = OPTIONS_CATEGORY + "/" + OPTIONS_SUBCATEGORY; // NOI18N

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    // @GuardedBy("EDT")
    private PythonOptionsPanel nodeJsOptionsPanel;
    private volatile boolean changed = false;
    private boolean firstOpening = true;


    @Override
    public void update() {
        assert EventQueue.isDispatchThread();
        if (firstOpening || !isChanged()) { // if panel is not modified by the user and he switches back to this panel, set to default
            firstOpening = false;
            getPanel().setPython(getPythonOptions().getPython());
            getPanel().setPythonSources(getPythonOptions().getPythonSources());
//            getPanel().setStopAtFirstLine(getDebuggerOptions().isBreakAtFirstLine());
//            getPanel().setLiveEdit(getDebuggerOptions().isLiveEdit());
            getPanel().setPip(getPythonOptions().getPip());
//            getPanel().setNpmIgnoreNodeModules(getPythonOptions().isNpmIgnoreNodeModules());
//            getPanel().setExpress(getPythonOptions().getExpress());
        }
        changed = false;
    }

    @Override
    public void applyChanges() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                getPythonOptions().setPython(getPanel().getPython());
                getPythonOptions().setPythonSources(getPanel().getPythonSources());
//                getDebuggerOptions().setBreakAtFirstLine(getPanel().isStopAtFirstLine());
//                getDebuggerOptions().setLiveEdit(getPanel().isLiveEdit());
                getPythonOptions().setPip(getPanel().getPip());
//                getPythonOptions().setPipIgnoreNodeModules(getPanel()..isNpmIgnoreNodeModules());
//                getPythonOptions().setExpress(getPanel().getExpress());
                changed = false;
            }
        });
    }

    @Override
    public void cancel() {
        if (isChanged()) { // if panel is modified by the user and options window closes, discard any changes
            getPanel().setPython(getPythonOptions().getPython());
            getPanel().setPythonSources(getPythonOptions().getPythonSources());
//            getPanel().setStopAtFirstLine(getDebuggerOptions().isBreakAtFirstLine());
//            getPanel().setLiveEdit(getDebuggerOptions().isLiveEdit());
            getPanel().setPip(getPythonOptions().getPip());
//            getPanel().setNpmIgnoreNodeModules(getPythonOptions().isNpmIgnoreNodeModules());
//            getPanel().setExpress(getPythonOptions().getExpress());
        }
    }

    @Override
    public boolean isValid() {
        assert EventQueue.isDispatchThread();
        PythonOptionsPanel panel = getPanel();
        ValidationResult result = new PythonOptionsValidator()
                .validatePython(panel.getPython(), panel.getPythonSources())
                .validatePip(panel.getPip())
//                .validateExpress(panel.getExpress())
                .getResult();
        // errors
        if (result.hasErrors()) {
            panel.setError(result.getFirstErrorMessage());
            return false;
        }
        // warnings
        if (result.hasWarnings()) {
            panel.setWarning(result.getFirstWarningMessage());
            return true;
        }
        // everything ok
        panel.setError(" "); // NOI18N
        return true;
    }

    @Override
    public boolean isChanged() {
        String saved = getPythonOptions().getPython();
        String current = getPanel().getPython().trim();
        if (saved == null ? !current.isEmpty() : !saved.equals(current)) {
            return true;
        }
        saved = getPythonOptions().getPythonSources();
        current = getPanel().getPythonSources();
        if (saved == null ? current != null : !saved.equals(current)) {
            return true;
        }
//        if (getDebuggerOptions().isBreakAtFirstLine() != getPanel().isStopAtFirstLine()) {
//            return true;
//        }
//        if (getDebuggerOptions().isLiveEdit() != getPanel().isLiveEdit()) {
//            return true;
//        }
        saved = getPythonOptions().getPip();
        current = getPanel().getPip().trim();
        if (saved == null ? !current.isEmpty() : !saved.equals(current)) {
            return true;
        }
//        if (getPythonOptions().isNpmIgnoreNodeModules() != getPanel().isNpmIgnoreNodeModules()) {
//            return true;
//        }
//        saved = getPythonOptions().getExpress();
//        current = getPanel().getExpress().trim();
        return saved == null ? !current.isEmpty() : !saved.equals(current);
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        assert EventQueue.isDispatchThread();
        return getPanel();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("org.netbeans.modules.javascript.nodejs.ui.options.PythonOptionsPanel"); // NOI18N
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (!changed) {
            changed = true;
            propertyChangeSupport.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        propertyChangeSupport.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

    private PythonOptionsPanel getPanel() {
        assert EventQueue.isDispatchThread();
        if (nodeJsOptionsPanel == null) {
            nodeJsOptionsPanel = PythonOptionsPanel.create();
            nodeJsOptionsPanel.addChangeListener(this);
        }
        return nodeJsOptionsPanel;
    }

    private PythonOptions getPythonOptions() {
        return PythonOptions.getInstance();
    }

//    private DebuggerOptions getDebuggerOptions() {
//        return DebuggerOptions.getInstance();
//    }

}
