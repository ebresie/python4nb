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
package org.apache.netbeans.modules.python4nb.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.netbeans.modules.python4nb.editor.options.PythonOptions;
import org.apache.netbeans.modules.python4nb.project.PythonProjectConstants;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.apache.netbeans.modules.python4nb.exec.PythonExecutable;
import org.apache.netbeans.modules.python4nb.platform.PythonSupport;
import org.apache.netbeans.modules.python4nb.preferences.PythonPreferences;
import org.netbeans.modules.web.common.api.UsageLogger;
//import org.netbeans.modules.javascript.nodejs.exec.PythonExecutable;
//import org.netbeans.modules.javascript.nodejs.exec.NpmExecutable;
//import org.netbeans.modules.javascript.nodejs.file.PackageJson;
//import org.netbeans.modules.javascript.nodejs.options.NodeJsOptions;
//import org.netbeans.modules.javascript.nodejs.platform.NodeJsSupport;
//import org.netbeans.modules.javascript.nodejs.preferences.NodeJsPreferences;
//import org.netbeans.modules.web.clientproject.api.WebClientProjectConstants;
//import org.netbeans.modules.web.common.api.UsageLogger;
//import org.netbeans.modules.web.common.api.Version;
import org.apache.netbeans.modules.python4nb.util.Version;
import org.netbeans.spi.project.ui.support.ProjectConvertors;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.modules.Places;
import org.openide.util.Lookup;
import org.openide.util.Pair;
import org.openide.util.Utilities;

public final class PythonUtils {

    public static final String START_FILE_PYTHON_PREFIX = "python "; // NOI18N

    private static final String PYTHON_DIR_NAME = "python"; // NOI18N
    private static final String USAGE_LOGGER_NAME = "org.netbeans.ui.metrics.javascript.nodejs"; // NOI18N
//    private static final UsageLogger PIP_INSTALL_USAGE_LOGGER = new UsageLogger.Builder(USAGE_LOGGER_NAME)
//            .message(PythonUtils.class, "USG_PIP_INSTALL") // NOI18N
//            .create();
//    private static final UsageLogger PIP_RUN_SCRIPT_USAGE_LOGGER = new UsageLogger.Builder(USAGE_LOGGER_NAME)
//            .message(PythonUtils.class, "USG_PIP_RUN_SCRIPT") // NOI18N
//            .firstMessageOnly(false)
//            .create();
//    private static final UsageLogger PIP_LIBRARY_USAGE_LOGGER = new UsageLogger.Builder(USAGE_LOGGER_NAME)
//            .message(PythonUtils.class, "USG_PIP_LIBRARY") // NOI18N
//            .firstMessageOnly(false)
//            .create();


    private PythonUtils() {
    }

    public static void logUsagePipInstall() {
//        PIP_INSTALL_USAGE_LOGGER.log();
    }

    public static void logUsagePipRunScript(String script) {
//        PIP_RUN_SCRIPT_USAGE_LOGGER.log(script);
    }

    public static void logUsagePipLibrary(String type, String name, String version) {
//        PIP_LIBRARY_USAGE_LOGGER.log(type, name, version);
    }

    public static String getProjectDisplayName(Project project) {
        return ProjectUtils.getInformation(project).getDisplayName();
    }

    @CheckForNull
    public static String getPython() {
//        if (GraalVmUtils.isRunningOn()) {
//            return GraalVmUtils.getPython();
//        }
        List<String> files = FileUtils.findFileOnUsersPath(PythonExecutable.PYTHON_NAMES);
        if (!files.isEmpty()) {
            return files.get(0);
        }
        return null;
    }

    @CheckForNull
    public static String getPip() {
//        if (GraalVmUtils.isRunningOn()) {
//            return GraalVmUtils.getPip(true);
//        }
        List<String> files = FileUtils.findFileOnUsersPath(PythonExecutable.PYTHON_NAMES);
        if (!files.isEmpty()) {
            return files.get(0);
        }
        return null;
    }
    // TODO: Determine if the below are needed

    public static boolean isPythonLibrary(Project project) {
        return getSiteRoots(project).isEmpty();
    }

    @CheckForNull
    public static File getSourceRoot(Project project) {
        for (File root : getSourceRoots(project)) {
            return root;
        }
        return null;
    }

    @CheckForNull
    public static File getSiteRoot(Project project) {
        for (File root : getSiteRoots(project)) {
            return root;
        }
        return null;
    }

    @CheckForNull
    public static File getTestRoot(Project project) {
        for (File root : getTestRoots(project)) {
            return root;
        }
        return null;
    }

    public static List<File> getSourceRoots(Project project) {
        return getRoots(project, PythonProjectConstants.SOURCES_TYPE_PYTHON);
    }

    public static List<File> getSiteRoots(Project project) {
        return getRoots(project, PythonProjectConstants.SOURCES_TYPE_PYTHON_SITE_ROOT);
    }

    public static List<File> getTestRoots(Project project) {
        return getRoots(project, PythonProjectConstants.SOURCES_TYPE_PYTHON_TEST);
    }

    public static List<File> getRoots(Project project, String type) {
        SourceGroup[] sourceGroups = ProjectUtils.getSources(project).getSourceGroups(type);
        List<File> roots = new ArrayList<>(sourceGroups.length);
        for (SourceGroup sourceGroup : sourceGroups) {
            FileObject rootFolder = sourceGroup.getRootFolder();
            File root = FileUtil.toFile(rootFolder);
            assert root != null : rootFolder;
            roots.add(root);
        }
        return roots;
    }

    /* TODO: Determine if something similar needed for python support.  
    There may need to account for some python startup files (i.e. user
    preferences or some such equivalent).  If needed then uncomment and
    convert to applicable pyton equivalent.*/
    public static Pair<String, String> parseStartFile(String line) {
        if (line == null) {
            throw new IllegalArgumentException("parseStartFile: Invalid line: " + line);
        }
        String data = line.trim();
        if (data.startsWith(START_FILE_PYTHON_PREFIX)) {
            data = data.substring(START_FILE_PYTHON_PREFIX.length());
        }
        String[] params = Utilities.parseParameters(data);
        String startFile = null;
        StringBuilder startArgsBuilder = new StringBuilder();
        for (String param : params) {
            if (startFile == null) {
                if (param.startsWith("-")) { // NOI18N
                    // python param
                    continue;
                }
                startFile = param;
            } else {
                // args
                if (startArgsBuilder.length() > 0) {
                    startArgsBuilder.append(" "); // NOI18N
                }
                startArgsBuilder.append(param);
            }
        }
        return Pair.of(startFile, startArgsBuilder.toString());
    }
//
//    @CheckForNull
//    public static PackageJson getPackageJson(Lookup context) {
//        return getProjectAndPackageJson(context).second();
//    }
//
//    @CheckForNull
//    public static Project getPackageJsonProject(Lookup context) {
//        return getProjectAndPackageJson(context).first();
//    }
//
//    public static Pair<Project, PackageJson> getProjectAndPackageJson(Lookup context) {
//        Project project = context.lookup(Project.class);
//        PackageJson packageJson = null;
//        if (project != null) {
//            // project action
//            packageJson = new PackageJson(project.getProjectDirectory());
//        } else {
//            // package.json directly
//            FileObject file = context.lookup(FileObject.class);
//            if (file == null) {
//                DataObject dataObject = context.lookup(DataObject.class);
//                if (dataObject != null) {
//                    file = dataObject.getPrimaryFile();
//                }
//            }
//            if (file != null) {
//                packageJson = new PackageJson(file.getParent());
//                project = FileOwnerQuery.getOwner(file);
//            }
//        }
//        if (project == null) {
//            return Pair.of(null, null);
//        }
//        if (packageJson == null) {
//            return Pair.of(null, null);
//        }
//        if (!packageJson.exists()) {
//            return Pair.of(null, null);
//        }
//        assert project != null;
//        assert packageJson != null;
//        return Pair.of(project, packageJson);
//    }

    @CheckForNull
    public static File getPythonSources(Project project) {
        PythonPreferences preferences = PythonSupport.forProject(project).getPreferences();
        if (preferences.isDefaultPython()) {
            // default python
            String pythonSources = PythonOptions.getInstance().getPythonSources();
            if (pythonSources != null) {
                return new File(pythonSources);
            }
            PythonExecutable python = PythonExecutable.getDefault(project, false);
            if (python == null) {
                return null;
            }
            Version version = python.getVersion();
            if (version == null) {
                return null;
            }
            return getPythonSources(version);
        }
        // custom python
        String pythonSources = preferences.getPythonSources();
        if (pythonSources != null) {
            return new File(pythonSources);
        }
        PythonExecutable python = PythonExecutable.forProject(project, false);
        if (python == null) {
            return null;
        }
        Version version = python.getVersion();
        if (version == null) {
            return null;
        }
        return PythonUtils.getPythonSources(project);
    }

    public static File getPythonSources() {
        return Places.getCacheSubdirectory(PYTHON_DIR_NAME);
    }

    public static boolean hasPythonSources(Version version) {
          if (version == null) {
              throw new IllegalArgumentException("hasPythonSources: Invalid Version: " + version);
          }
        return PythonUtils.getPythonSources(version).isDirectory();
    }

    public static boolean hasPythonSources(String version) {
          if (version == null) {
              throw new IllegalArgumentException("hasPythonSources: Invalid Version: " + version);
          }
        return getPythonSources(version).isDirectory();
    }

    static File getPythonSources(Version version) {
        assert version != null;
        return getPythonSources(version.toString());
    }

    private static File getPythonSources(String version) {
        assert version != null;
        return new File(PythonUtils.getPythonSources(), version);
    }

    /**
     * Checks whether the given folder is already a project.
     * <p>
     * This method ignores ProjectConvertor projects.
     * @param folder folder to be checked
     * @return {@code true} if the given folder is already a project, {@code false} otherwise
     */
    public static boolean isProject(File folder) {
        Project prj = null;
        boolean foundButBroken = false;
        try {
            prj = ProjectManager.getDefault().findProject(FileUtil.toFileObject(FileUtil.normalizeFile(folder)));
        } catch (IOException ex) {
            foundButBroken = true;
        } catch (IllegalArgumentException ex) {
            // noop
        }
        if (prj != null
                && !ProjectConvertors.isConvertorProject(prj)) {
            return true;
        }
        return foundButBroken;
    }

}
