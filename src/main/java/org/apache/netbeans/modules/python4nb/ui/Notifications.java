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
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.project.Project;
import org.apache.netbeans.modules.python4nb.editor.options.PythonOptions;
import org.apache.netbeans.modules.python4nb.platform.PythonPlatformProvider;
import org.apache.netbeans.modules.python4nb.platform.PythonSupport;
import org.apache.netbeans.modules.python4nb.preferences.PythonPreferences;
//import org.netbeans.modules.javascript.nodejs.ui.customizer.NodeJsRunPanel;
import org.apache.netbeans.modules.python4nb.ui.PythonRunPanel;
//import org.netbeans.modules.javascript.nodejs.util.GraalVmUtils;
//import org.netbeans.modules.javascript.nodejs.util.PythonUtils;
import org.apache.netbeans.modules.python4nb.util.PythonUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;

public final class Notifications {

    private Notifications() {
    }

    @NbBundle.Messages({
        "Notifications.detection.title=Python detected",
        "# {0} - project name",
        "Notifications.detection.description=Enable Python support in project {0}?",
        "# {0} - project name",
        "Notifications.detection.done=Python support enabled in project {0}.",
        "# {0} - project name",
        "Notifications.detection.noop=Python support already enabled in project {0}.",
    })
    public static void notifyPythonDetected(final Project project) {
        final String projectName = PythonUtils.getProjectDisplayName(project);
        final PythonPreferences preferences = PythonSupport.forProject(project).getPreferences();
        assert !preferences.isEnabled() : "Python support should not be enabled in project " + projectName;
        NotificationDisplayer.getDefault().notify(
                Bundle.Notifications_detection_title(),
                NotificationDisplayer.Priority.LOW.getIcon(),
                Bundle.Notifications_detection_description(projectName),
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String text;
                        if (preferences.isEnabled()) {
                            // already done
                            text = Bundle.Notifications_detection_noop(projectName);
                        } else {
                            preferences.setEnabled(true);
                            text = Bundle.Notifications_detection_done(projectName);
                        }
                        StatusDisplayer.getDefault().setStatusText(text);
                    }
                },
                NotificationDisplayer.Priority.LOW);
    }

//    @NbBundle.Messages({
//        "Notifications.graalvm.detection.title=GraalVM detected",
//        "Notifications.graalvm.detection.description=Set proper node and npm paths?",
//        "Notifications.graalvm.detection.done=Proper node and npm paths set.",
//        "Notifications.graalvm.detection.noop=Proper node and npm paths already set.",
//    })
//    public static void notifyGraalVmDetected() {
//        NotificationDisplayer.getDefault().notify(
//                Bundle.Notifications_graalvm_detection_title(),
//                NotificationDisplayer.Priority.LOW.getIcon(),
//                Bundle.Notifications_graalvm_detection_description(),
//                new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        String text;
//                        if (GraalVmUtils.properPathsSet()) {
//                            // already done
//                            text = Bundle.Notifications_graalvm_detection_noop();
//                        } else {
//                            PythonOptions.getInstance().setPython(GraalVmUtils.getPython());
//                            PythonOptions.getInstance().setNpm(GraalVmUtils.getNpm(true));
//                            text = Bundle.Notifications_graalvm_detection_done();
//                        }
//                        StatusDisplayer.getDefault().setStatusText(text);
//                    }
//                },
//                NotificationDisplayer.Priority.LOW);
//    }

    @NbBundle.Messages({
        "Notifications.enabled.title=Python support enabled",
        "# {0} - project name",
        "Notifications.enabled.description=Enable running project {0} as Python application?",
        "# {0} - project name",
        "Notifications.enabled.done=Project {0} will be run as Python application.",
        "# {0} - project name",
        "Notifications.enabled.noop=Project {0} already runs as Python application.",
        "# {0} - project name",
        "Notifications.enabled.invalid=Python support not enabled in project {0}.",
    })
    public static void notifyRunConfiguration(Project project) {
        final String projectName = PythonUtils.getProjectDisplayName(project);
        final PythonSupport pythonSupport = PythonSupport.forProject(project);
        final PythonPreferences preferences = pythonSupport.getPreferences();
        assert !preferences.isRunEnabled() : "Python run should not be enabled in " + projectName;
        NotificationDisplayer.getDefault().notify(Bundle.Notifications_enabled_title(),
                NotificationDisplayer.Priority.LOW.getIcon(),
                Bundle.Notifications_enabled_description(projectName),
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String text;
                        if (!preferences.isEnabled()) {
                            // not enabled at all (happens if one clicks in notifications window later)
                            text = Bundle.Notifications_enabled_invalid(projectName);
                        } else if (preferences.isRunEnabled()) {
                            // already done
                            text = Bundle.Notifications_enabled_noop(projectName);
                        } else {
                            pythonSupport.firePropertyChanged(PythonPlatformProvider.PROP_RUN_CONFIGURATION, null, PythonRunPanel.IDENTIFIER);
                            text = Bundle.Notifications_enabled_done(projectName);
                        }
                        StatusDisplayer.getDefault().setStatusText(text);
                    }
                },
                NotificationDisplayer.Priority.LOW);
    }

    public static void notifyUser(String title, String details) {
        NotificationDisplayer.getDefault().notify(
                title,
                NotificationDisplayer.Priority.LOW.getIcon(),
                details,
                null,
                NotificationDisplayer.Priority.LOW);
    }

    public static void informUser(String message) {
        DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message(message));
    }

    public static void ask(final String title, final String question, @NullAllowed final Runnable yesTask, @NullAllowed final Runnable noTask) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                NotifyDescriptor confirmation = new NotifyDescriptor.Confirmation(question, title, NotifyDescriptor.YES_NO_OPTION);
                if (DialogDisplayer.getDefault().notify(confirmation) == NotifyDescriptor.YES_OPTION) {
                    if (yesTask != null) {
                        yesTask.run();
                    }
                } else if (noTask != null) {
                    noTask.run();
                }
            }
        });
    }

    @NbBundle.Messages("Notifications.ask.sync=Sync changes between project and package.json?")
    public static void askSyncChanges(Project project, @NullAllowed Runnable yesTask, @NullAllowed Runnable noTask) {
        ask(PythonUtils.getProjectDisplayName(project), Bundle.Notifications_ask_sync(), yesTask, noTask);
    }

}
