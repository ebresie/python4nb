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
package org.apache.netbeans.modules.python4nb.platform;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import org.apache.netbeans.modules.python4nb.editor.file.PythonPackage;
import org.netbeans.api.project.Project;
import org.apache.netbeans.modules.python4nb.exec.PythonExecutable;
//import org.apache.netbeans.modules.python4nb.file.PythonPackage;
import org.apache.netbeans.modules.python4nb.editor.options.PythonOptions;
import org.apache.netbeans.modules.python4nb.preferences.PythonPreferences;
import org.apache.netbeans.modules.python4nb.ui.Notifications;
import org.apache.netbeans.modules.python4nb.ui.PythonRunPanel;
//import org.apache.netbeans.modules.python4nb.ui.actions.NodeJsActionProvider;
//import org.apache.netbeans.modules.python4nb.ui.customizer.NodeJsRunPanel;
import org.apache.netbeans.modules.python4nb.util.PythonUtils;
import org.apache.netbeans.modules.python4nb.util.StringUtils;
//import org.netbeans.modules.web.common.api.Version;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.netbeans.spi.project.support.ant.PropertyUtils;
//import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Pair;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;

public final class PythonSupport {

    static final Logger LOGGER = Logger.getLogger(PythonSupport.class.getName());

    static final RequestProcessor RP = new RequestProcessor(PythonSupport.class);

    final Project project;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    final PreferenceChangeListener optionsListener = new OptionsListener();
    final PreferenceChangeListener preferencesListener = new PreferencesListener();
    private final PropertyChangeListener packageJsonListener = new PythonPackageListener();
    private final FileChangeListener pythonSourcesListener = new NodeSourcesListener();
    final PythonPreferences  preferences;
    private final ActionProvider actionProvider;
    final PythonSourceRoots sourceRoots;
    final PythonPackage pythonPackage;


    private PythonSupport(Project project) {
        assert project != null;
        this.project = project;
        actionProvider = null;  // new NodeJsActionProvider(project);
        sourceRoots = new PythonSourceRoots(project);
        preferences = new PythonPreferences(project);
        pythonPackage =  new PythonPackage(project.getProjectDirectory());
    }

    @ProjectServiceProvider(service = PythonSupport.class, projectType = "org-netbeans-modules-python4nb-project") // NOI18N
    public static PythonSupport create(Project project) {
        PythonSupport support = new PythonSupport(project);
        // listeners
        PythonOptions  pythonOptions = PythonOptions.getInstance();
        pythonOptions.addPreferenceChangeListener(WeakListeners.create(PreferenceChangeListener.class, support.optionsListener, pythonOptions));
        return support;
    }

    public static PythonSupport forProject(Project project) {
        // TODO: FIgure out why lookup not working right
        Lookup lookup = project.getLookup();
        PythonSupport support = lookup.lookup(PythonSupport.class);
//        PythonSupport support = project.getLookup().lookup(PythonSupport.class);
        if (support == null) {
            throw new IllegalArgumentException("Python Support not available or configured.");
        }
//        assert support != null : "NodeJsSupport should be found in project " + project.getClass().getName() + " (lookup: " + project.getLookup() + ")";
        return support;
    }

    public PythonPreferences getPreferences() {
        return preferences;
    }

    public ActionProvider getActionProvider() {
        return actionProvider;
    }

    public List<URL> getSourceRoots() {
        return sourceRoots.getSourceRoots();
    }

    public PythonPackage getPythonPackage() {
        return pythonPackage;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void firePropertyChanged(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(project, propertyName, oldValue, newValue));
    }

    public void fireSourceRootsChanged() {
        sourceRoots.resetSourceRoots();
        firePropertyChanged(PythonPlatformProvider.PROP_SOURCE_ROOTS, null, null);
    }

    void projectOpened() {
        FileUtil.addFileChangeListener(pythonSourcesListener, PythonUtils.getPythonSources());
        preferences.addPreferenceChangeListener(preferencesListener);
        pythonPackage.addPropertyChangeListener(packageJsonListener);
        // init node version
        PythonExecutable node = PythonExecutable.forProject(project, false);
        if (node != null) {
            node.getVersion();
        }
    }

    void projectClosed() {
        FileUtil.removeFileChangeListener(pythonSourcesListener, PythonUtils.getPythonSources());
        preferences.removePreferenceChangeListener(preferencesListener);
        pythonPackage.removePropertyChangeListener(packageJsonListener);
        // cleanup
        pythonPackage.cleanup();
    }

    //~ Inner classes

    private final class OptionsListener implements PreferenceChangeListener {

        @Override
        public void preferenceChange(PreferenceChangeEvent evt) {
            String projectName = project.getProjectDirectory().getNameExt();
            if (!preferences.isEnabled()) {
                LOGGER.log(Level.FINE, "Change event in python options ignored, python not enabled in project {0}", projectName);
                return;
            }
            String key = evt.getKey();
            LOGGER.log(Level.FINE, "Processing change event {0} in python options in project {1}", new Object[] {key, projectName});
            if (preferences.isDefaultPython()
                    && (PythonOptions.PYTHON_PATH.equals(key) || PythonOptions.PYTHON_SOURCES_PATH.equals(key))) {
                fireSourceRootsChanged();
            }
        }

    }

    private final class PreferencesListener implements PreferenceChangeListener {

        // #248870 - 2 events fired in a row (one for 'file', second for 'args')
        private final RequestProcessor.Task startScriptSyncTask = RP.create(new Runnable() {
            @Override
            public void run() {
                startScriptChanged(preferences.getStartFile(), preferences.getStartArgs());
            }
        });


        @Override
        public void preferenceChange(PreferenceChangeEvent evt) {
            String projectName = project.getProjectDirectory().getNameExt();
            boolean enabled = preferences.isEnabled();
            String key = evt.getKey();
            LOGGER.log(Level.FINE, "Processing change event {0} in python preferences in project {1}", new Object[] {key, projectName});
            if (PythonPreferences.ENABLED.equals(key)) {
                firePropertyChanged(PythonPlatformProvider.PROP_ENABLED, !enabled, enabled);
                if (enabled) {
                    if (PythonUtils.isPythonLibrary(project)) {
                        // enable python run config
                        preferences.setRunEnabled(true);
                        firePropertyChanged(PythonPlatformProvider.PROP_RUN_CONFIGURATION, null, PythonRunPanel.IDENTIFIER);
                    } else if (preferences.isAskRunEnabled()) {
                        Notifications.notifyRunConfiguration(project);
                    }
                }
            } else if (!enabled) {
                LOGGER.log(Level.FINE, "Change event in python preferences ignored, python not enabled in project {0}", projectName);
            } else if (PythonPreferences.PYTHON_DEFAULT.equals(key)) {
                fireSourceRootsChanged();
            } else if (!preferences.isDefaultPython()
                    && (PythonPreferences.PYTHON_PATH.equals(key) || PythonPreferences.PYTHON_SOURCES_PATH.equals(key))) {
                fireSourceRootsChanged();
            } else if (PythonPreferences.START_FILE.equals(key)
                    || PythonPreferences.START_ARGS.equals(key)) {
                startScriptSyncTask.schedule(100);
            }
        }

        @NbBundle.Messages({
            "# {0} - project name",
            "PreferencesListener.sync.title=Python ({0})",
            "PreferencesListener.sync.error=Cannot write changed start file/arguments to package.json.",
            "PreferencesListener.sync.done=Start file/arguments synced to package.json.",
        })
        void startScriptChanged(String newStartFile, final String newStartArgs) {
            final String projectDir = project.getProjectDirectory().getNameExt();
            if (!preferences.isEnabled()) {
                LOGGER.log(Level.FINE, "Start file/args change ignored in project {0}, python not enabled in project {0}", projectDir);
                return;
            }
            if (!preferences.isSyncEnabled()) {
                LOGGER.log(Level.FINE, "Start file/args change ignored in project {0}, sync not enabled", projectDir);
                return;
            }
            if (!StringUtils.hasText(newStartFile)
                    && !StringUtils.hasText(newStartArgs)) {
                LOGGER.log(Level.FINE, "Start file/args change ignored in project {0}, new file and args are empty", projectDir);
                return;
            }
            String relNewStartFile = newStartFile;
            String relPath = PropertyUtils.relativizeFile(FileUtil.toFile(project.getProjectDirectory()), new File(newStartFile));
            if (relPath != null) {
                relNewStartFile = relPath;
            }
            if (!pythonPackage.exists()) {
                LOGGER.log(Level.FINE, "Start file/args change ignored in project {0}, package.json not exist", projectDir);
                return;
            }
            LOGGER.log(Level.FINE, "Processing Start file/args change in project {0}", projectDir);
            Map<String, Object> content = pythonPackage.getContent();
            if (content == null) {
                LOGGER.log(Level.FINE, "Start file/args change ignored in project {0}, package.json has no or invalid content", projectDir);
                return;
            }
            String startFile = null;
            String startArgs = null;
            String startScript = pythonPackage.getContentValue(String.class, PythonPackage.FIELD_SCRIPTS, PythonPackage.FIELD_START);
            if (startScript != null) {
                Pair<String, String> startInfo = PythonUtils.parseStartFile(startScript);
                startFile = startInfo.first();
                startArgs = startInfo.second();
            }
            if (Objects.equals(startFile, relNewStartFile)
                    && Objects.equals(startArgs, newStartArgs)) {
                LOGGER.log(Level.FINE, "Start file/args change ignored in project {0}, file and args same as in package.json", projectDir);
                return;
            }
            final String projectName = PythonUtils.getProjectDisplayName(project);
            if (preferences.isAskSyncEnabled()) {
                final String relNewStartFileRef = relNewStartFile;
                Notifications.askSyncChanges(project, new Runnable() {
                    @Override
                    public void run() {
                        RP.post(new Runnable() {
                            @Override
                            public void run() {
                                changeStartScript(relNewStartFileRef, newStartArgs, projectName, projectDir);
                            }
                        });
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        preferences.setSyncEnabled(false);
                        LOGGER.log(Level.FINE, "Start file/args change ignored in project {0}, cancelled by user", projectDir);
                    }
                });
            } else {
                changeStartScript(relNewStartFile, newStartArgs, projectName, projectDir);
            }
        }

        void changeStartScript(String relNewStartFile, String newStartArgs, String projectName, String projectDir) {
            StringBuilder sb = new StringBuilder();
            sb.append(PythonUtils.START_FILE_PYTHON_PREFIX);
            sb.append(relNewStartFile);
            if (StringUtils.hasText(newStartArgs)) {
                sb.append(" "); // NOI18N
                sb.append(newStartArgs);
            }
            try {
                pythonPackage.setContent(Arrays.asList(PythonPackage.FIELD_SCRIPTS, PythonPackage.FIELD_START), sb.toString());
            } catch (IOException ex) {
                LOGGER.log(Level.INFO, null, ex);
                Notifications.informUser(Bundle.PreferencesListener_sync_error());
                return;
            }
            Notifications.notifyUser(Bundle.PreferencesListener_sync_title(projectName), Bundle.PreferencesListener_sync_done());
            LOGGER.log(Level.FINE, "Start file/args change synced to package.json in project {0}", projectDir);
        }

    }

    private final class PythonPackageListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String projectName = project.getProjectDirectory().getNameExt();
            if (!preferences.isEnabled()) {
                LOGGER.log(Level.FINE, "Property change event in package.json ignored, python not enabled in project {0}", projectName);
                return;
            }
            if (!preferences.isSyncEnabled()) {
                LOGGER.log(Level.FINE, "Property change event in package.json ignored, python sync not enabled in project {0}", projectName);
                return;
            }
            String propertyName = evt.getPropertyName();
            LOGGER.log(Level.FINE, "Processing property change event {0} in package.json in project {1}", new Object[] {propertyName, projectName});
            if (PythonPackage.PROP_NAME.equals(propertyName)) {
                projectNameChanged(evt.getOldValue(), evt.getNewValue());
            } else if (PythonPackage.PROP_SCRIPTS_START.equals(propertyName)) {
                startScriptChanged((String) evt.getNewValue());
            }
        }

        private void projectNameChanged(final Object oldName, final Object newName) {
            if (!(newName instanceof String)) {
                LOGGER.log(Level.FINE, "Project name change ignored, not a string: {0}", newName);
                // ignore
                return;
            }
            if (preferences.isAskSyncEnabled()) {
                Notifications.askSyncChanges(project, new Runnable() {
                    @Override
                    public void run() {
                        RP.post(new Runnable() {
                            @Override
                            public void run() {
                                firePropertyChanged(PythonPlatformProvider.PROP_PROJECT_NAME, oldName, newName);
                            }
                        });
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        preferences.setSyncEnabled(false);
                        LOGGER.log(Level.FINE, "Project name change ignored in project {0}, cancelled by user", project.getProjectDirectory().getNameExt());
                    }
                });
            } else {
                firePropertyChanged(PythonPlatformProvider.PROP_PROJECT_NAME, oldName, newName);
            }
        }

        @NbBundle.Messages({
            "# {0} - project name",
            "PythonPackageListener.sync.title=Python ({0})",
            "PythonPackageListener.sync.done=Start file/arguments synced to Project Properties.",
        })
        private void startScriptChanged(String newStartScript) {
            final String projectDir = project.getProjectDirectory().getNameExt();
            if (!StringUtils.hasText(newStartScript)) {
                LOGGER.log(Level.FINE, "Start script change ignored in project {0}, it has no text", projectDir);
                return;
            }
            Pair<String, String> newStartInfo = PythonUtils.parseStartFile(newStartScript);
            if (newStartInfo.first() == null) {
                LOGGER.log(Level.FINE, "Start script change ignored in project {0}, no 'file' found", projectDir);
                return;

            }
            final String newStartFile = new File(FileUtil.toFile(project.getProjectDirectory()), newStartInfo.first()).getAbsolutePath();
            String startFile = preferences.getStartFile();
            final boolean syncFile = !Objects.equals(startFile, newStartFile);
            String startArgs = preferences.getStartArgs();
            final String newStartArgs = newStartInfo.second();
            final boolean syncArgs = !Objects.equals(startArgs, newStartArgs);
            if (!syncFile
                    && !syncArgs) {
                LOGGER.log(Level.FINE, "Start script change ignored in project {0}, same values already set", projectDir);
                return;
            }
            final String projectName = PythonUtils.getProjectDisplayName(project);
            if (preferences.isAskSyncEnabled()) {
                Notifications.askSyncChanges(project, new Runnable() {
                    @Override
                    public void run() {
                        RP.post(new Runnable() {
                            @Override
                            public void run() {
                                changeStartScript(syncFile, newStartFile, syncArgs, newStartArgs, projectName, projectDir);
                            }
                        });
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        preferences.setSyncEnabled(false);
                        LOGGER.log(Level.FINE, "Start script change ignored in project {0}, cancelled by user", projectDir);
                    }
                });
            } else {
                changeStartScript(syncFile, newStartFile, syncArgs, newStartArgs, projectName, projectDir);
            }
        }

        void changeStartScript(boolean syncFile, String newStartFile, boolean syncArgs, String newStartArgs, String projectName, String projectDir) {
            if (syncFile) {
                preferences.setStartFile(newStartFile);
            }
            if (syncArgs) {
                preferences.setStartArgs(newStartArgs);
            }
            Notifications.notifyUser(Bundle.PythonPackageListener_sync_title(projectName), Bundle.PythonPackageListener_sync_done());
            LOGGER.log(Level.FINE, "Start file/args change synced to project.properties in project {0}", projectDir);
        }

    }

    private final class NodeSourcesListener extends FileChangeAdapter {

        @Override
        public void fileFolderCreated(FileEvent fe) {
            String projectName = project.getProjectDirectory().getNameExt();
            if (!preferences.isEnabled()) {
                LOGGER.log(Level.FINE, "File change event in node sources ignored, python not enabled in project {0}", projectName);
                return;
            }
            PythonExecutable node = PythonExecutable.forProject(project, false);
            if (node == null) {
                return;
            }
//            Version version = node.getVersion();
//            if (version == null) {
//                return;
//            }
//            if (fe.getFile().getNameExt().equals(version.toString())) {
//                LOGGER.log(Level.FINE, "Processing file change event in node sources in project {0}", projectName);
//                fireSourceRootsChanged();
//            }
        }

    }

}
