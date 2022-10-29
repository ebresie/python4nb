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
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.netbeans.modules.python4nb.api.Util;
import org.apache.netbeans.modules.python4nb.editor.file.PythonPackage;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
//import org.netbeans.modules.javascript.nodejs.file.PythonPackage;
import org.apache.netbeans.modules.python4nb.preferences.PythonPreferences;
import org.apache.netbeans.modules.python4nb.ui.Notifications;
//import org.netbeans.modules.javascript.nodejs.ui.customizer.PythonRunPanel;
//import org.netbeans.modules.javascript.nodejs.util.GraalVmUtils;
import org.apache.netbeans.modules.python4nb.util.PythonUtils;
import org.apache.netbeans.modules.python4nb.util.StringUtils;
import org.apache.netbeans.modules.python4nb.ui.PythonRunPanel;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.web.clientproject.api.BadgeIcon;
import org.netbeans.modules.web.clientproject.api.platform.PlatformProviders;
import org.netbeans.modules.web.clientproject.spi.CustomizerPanelImplementation;
import org.netbeans.modules.web.clientproject.spi.platform.PlatformProviderImplementation;
import org.netbeans.modules.web.clientproject.spi.platform.PlatformProviderImplementationListener;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.ServiceProvider;


@ServiceProvider(service = PlatformProviderImplementation.class, path = PlatformProviders.PLATFORM_PATH, position = 100)
public final class PythonPlatformProvider implements PlatformProviderImplementation, PropertyChangeListener {

    private static final Logger LOGGER = Logger.getLogger(PythonPlatformProvider.class.getName());

//    private final PropertyEvaluator evaluator;
    
    public static final String IDENT = "python"; // NOI18N

    static final RequestProcessor RP = new RequestProcessor(PythonPlatformProvider.class);

    @StaticResource
    private static final String ICON_PATH = "org/apache/netbeans/modules/python4nb/editor/py.png"; // NOI18N

    private final BadgeIcon badgeIcon;
    private final PlatformProviderImplementationListener.Support listenerSupport = new PlatformProviderImplementationListener.Support();

    public PythonPlatformProvider(final PropertyEvaluator evaluator) {
//        this.evaluator = evaluator;
        badgeIcon = new BadgeIcon(
                ImageUtilities.loadImage(ICON_PATH),
                PythonPlatformProvider.class.getResource("/" + ICON_PATH)); // NOI18N
    }

    public PythonPlatformProvider() {
        badgeIcon = new BadgeIcon(
                ImageUtilities.loadImage(ICON_PATH),
                PythonPlatformProvider.class.getResource("/" + ICON_PATH)); // NOI18N
    }

    @Override
    public String getIdentifier() {
        return IDENT;
    }

    @NbBundle.Messages({"PythonPlatformProvider.name=Python"})
    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(PythonPlatformProvider.class, "PythonPlatformProvider.name");
        // return Bundle.PythonPlatformProvider_name();
    }

    @Override
    public BadgeIcon getBadgeIcon() {
        return badgeIcon;
    }

    @Override
    public boolean isEnabled(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Invalid project: " + project);
        }
        return PythonSupport.forProject(project).getPreferences().isEnabled();
    }

    @Override
    public List<URL> getSourceRoots(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Invalid project: " + project);
        }
        assert isEnabled(project) : "Node.je support must be enabled in this project: " + project.getProjectDirectory().getNameExt();
        return PythonSupport.forProject(project).getSourceRoots();
    }

    @Override
    public ActionProvider getActionProvider(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Invalid project: " + project);
        }
        return PythonSupport.forProject(project).getActionProvider();
    }

    @Override
    public List<CustomizerPanelImplementation> getRunCustomizerPanels(Project project) {
        return Collections.<CustomizerPanelImplementation>singletonList(new PythonRunPanel(project));
    }

    @Override
    public void projectOpened(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Invalid project: " + project);
        }
        PythonSupport pythonSupport = PythonSupport.forProject(project);
        pythonSupport.addPropertyChangeListener(this);
        pythonSupport.projectOpened();
        detectPython(project);
//        GraalVmUtils.detectOptions();
    }

    @Override
    public void projectClosed(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Invalid project: " + project);
        }
        PythonSupport pythonSupport = PythonSupport.forProject(project);
        pythonSupport.projectClosed();
        pythonSupport.removePropertyChangeListener(this);
    }

    @Override
    public void notifyPropertyChanged(final Project project, final PropertyChangeEvent event) {
        String propertyName = event.getPropertyName();
        if (PROP_ENABLED.equals(propertyName)) {
            PythonSupport.forProject(project).getPreferences().setEnabled((boolean) event.getNewValue());
        } else if (PROP_PROJECT_NAME.equals(propertyName)) {
            projectNameChanged(project, (String) event.getNewValue());
        } else if (PROP_RUN_CONFIGURATION.equals(propertyName)) {
            runConfigurationChanged(project, event.getNewValue());
        }
    }

    @Override
    public void addPlatformProviderImplementationListener(PlatformProviderImplementationListener listener) {
        listenerSupport.addPlatformProviderImplementationsListener(listener);
    }

    @Override
    public void removePlatformProviderImplementationListener(PlatformProviderImplementationListener listener) {
        listenerSupport.removePlatformProviderImplementationsListener(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        listenerSupport.firePropertyChanged((Project) evt.getSource(), this,
                new PropertyChangeEvent(this, evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
    }

    private void detectPython(Project project) {
        PythonSupport pythonSupport = PythonSupport.forProject(project);
        PythonPreferences preferences = pythonSupport.getPreferences();
        if (preferences.isEnabled()) {
            // already enabled => noop
            return;
        }
        PythonPackage pythonPackage = pythonSupport.getPythonPackage();
        if (!pythonPackage.exists()) {
            return;
        }
//        Map<String, Object> content = pythonPackage.getContent();
//        if (content == null) {
//            // some error
//            return;
//        }
//        Object engines = content.get(PythonPackage.FIELD_ENGINES);
//        if (engines instanceof Map) {
//            @SuppressWarnings("unchecked")
//            Map<String, Object> engines2 = (Map<String, Object>) engines;
//            if (engines2.containsKey(PythonPackage.FIELD_NODE)) {
//                Notifications.notifyPythonDetected(project);
//            }
//        }
    }

    void projectNameChanged(Project project, final String newName) {
        final String projectDir = project.getProjectDirectory().getNameExt();
        PythonSupport pythonSupport = PythonSupport.forProject(project);
        final PythonPreferences preferences = pythonSupport.getPreferences();
        if (!preferences.isEnabled()) {
            LOGGER.log(Level.FINE, "Project name change ignored in project {0}, python not enabled", projectDir);
            return;
        }
        if (!preferences.isSyncEnabled()) {
            LOGGER.log(Level.FINE, "Project name change ignored in project {0}, sync not enabled", projectDir);
            return;
        }
        final PythonPackage packageJson = pythonSupport.getPythonPackage();
        if (!packageJson.exists()) {
            LOGGER.log(Level.FINE, "Project name change ignored in project {0}, package.json not exist", projectDir);
            return;
        }
        LOGGER.log(Level.FINE, "Processing project name change in project {0}", projectDir);
//        Map<String, Object> content = packageJson.getContent();
//        if (content == null) {
//            LOGGER.log(Level.FINE, "Project name change ignored in project {0}, package.json has no or invalid content", projectDir);
//            return;
//        }
        if (!StringUtils.hasText(newName)) {
            LOGGER.log(Level.FINE, "Project name change ignored in project {0}, new name is empty", projectDir);
            return;
        }
//        String name = (String) content.get(PythonPackage.FIELD_NAME);
//        if (Objects.equals(name, newName)) {
//            LOGGER.log(Level.FINE, "Project name change ignored in project {0}, new name same as current name in package.json", projectDir);
//            return;
//        }
        final String projectName = PythonUtils.getProjectDisplayName(project);
        if (preferences.isAskSyncEnabled()) {
            Notifications.askSyncChanges(project, new Runnable() {
                @Override
                public void run() {
                    RP.post(new Runnable() {
                        @Override
                        public void run() {
                            changeProjectName(packageJson, newName, projectName, projectDir);
                        }
                    });
                }
            }, new Runnable() {
                @Override
                public void run() {
                    preferences.setSyncEnabled(false);
                    LOGGER.log(Level.FINE, "Project name change ignored in project {0}, cancelled by user", projectDir);
                }
            });
        } else {
            changeProjectName(packageJson, newName, projectName, projectDir);
        }
    }

    @NbBundle.Messages({
        "# {0} - project name",
        "PythonPlatformProvider.sync.title=Python ({0})",
        "PythonPlatformProvider.sync.error=Cannot write changed project name to package.json.",
        "# {0} - project name",
        "PythonPlatformProvider.sync.done=Project name {0} synced to package.json.",
    })
    void changeProjectName(PythonPackage pythonPackage, String newName, String projectName, String projectDir) {
//        try {
//            pythonPackage.setContent(Collections.singletonList(PythonPackage.FIELD_NAME), newName);
//        } catch (IOException ex) {
//            LOGGER.log(Level.INFO, null, ex);
//            Notifications.informUser(Bundle.PythonPlatformProvider_sync_error());
//            return;
//        }
//        Notifications.notifyUser(Bundle.PythonPlatformProvider_sync_title(projectName), Bundle.PythonPlatformProvider_sync_done(projectName));
        LOGGER.log(Level.FINE, "Project name change synced to package.json in project {0}", projectDir);
    }

    private void runConfigurationChanged(Project project, Object activeRunConfig) {
        boolean runEnabled = false;
        for (CustomizerPanelImplementation panel : getRunCustomizerPanels(project)) {
            if (panel.getIdentifier().equals(activeRunConfig)) {
                runEnabled = true;
                break;
            }
        }
        PythonSupport.forProject(project).getPreferences().setRunEnabled(runEnabled);
    }

    // TODO: Based on nbPython
    public PythonPlatform getPlatform() {
        ensurePlatformsReady();
        // TODO: Determine if "Property Evalutor" logic is needed
//        String id = evaluator.getProperty("platform.active"); // NOI18N
//        String id = evaluator.getProperty("platform.active"); // NOI18N
        PythonPlatformManager manager = PythonPlatformManager.getInstance();
//        if (id == null) {
//            id = manager.getDefaultPlatform();
//        }

        String id = manager.getDefaultPlatform();
        
        PythonPlatform platform = manager.getPlatform(id);
        if (platform == null) {
            LOGGER.info("Platform with id '" + id + "' does not exist. Using default platform.");
            platform = manager.getPlatform(manager.getDefaultPlatform());
        }
        return platform;
        
    }
    
       private void ensurePlatformsReady() {
        if (!Util.isFirstPlatformTouch()) {
            return;
        }
        String handleMessage = NbBundle.getMessage(PythonPlatformProvider.class, "PythonPlatformProvider.PythonPlatformAutoDetection");
        ProgressHandle ph = ProgressHandleFactory.createHandle(handleMessage);
        ph.start();
        try {
            Thread autoDetection = new Thread(new Runnable() {
                @Override
                public void run() {
                    PythonPlatformManager.getInstance().autoDetect();
                }
            }, "Python Platform AutoDetection"); // NOI18N
            autoDetection.start();
            autoDetection.join();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
                // Restore interrupted state...
                Thread.currentThread().interrupt();
        }
        ph.finish();
    }
}
