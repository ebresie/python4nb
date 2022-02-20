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

package org.apache.netbeans.modules.python4nb.exec;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.netbeans.modules.python4nb.editor.options.PythonOptionsValidator;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.base.input.InputProcessor;
import org.netbeans.api.extexecution.base.input.InputProcessors;
import org.netbeans.api.extexecution.base.input.LineProcessor;
import org.netbeans.api.extexecution.print.ConvertedLine;
import org.netbeans.api.extexecution.print.LineConvertor;
import org.netbeans.api.extexecution.startup.StartupExtender;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.api.project.Project;
import org.apache.netbeans.modules.python4nb.editor.options.PythonOptions;
import org.apache.netbeans.modules.python4nb.editor.file.PythonPackage;
import org.apache.netbeans.modules.python4nb.platform.PythonSupport;
import org.apache.netbeans.modules.python4nb.preferences.PythonPreferences;
import org.apache.netbeans.modules.python4nb.preferences.PythonPreferencesValidator;
import org.apache.netbeans.modules.python4nb.ui.PythonCustomizerProvider;
import org.apache.netbeans.modules.python4nb.ui.options.PythonOptionsPanelController;
import org.apache.netbeans.modules.python4nb.util.FileUtils;
import org.apache.netbeans.modules.python4nb.util.PythonUtils;
import org.apache.netbeans.modules.python4nb.util.StringUtils;
import org.apache.netbeans.modules.python4nb.util.ValidationUtils;
import org.apache.netbeans.modules.python4nb.util.ValidationResult;
// TODO: Determine how to handle these since these are based on org.netbeans.modules.web.common.ui classes
import org.apache.netbeans.modules.python4nb.util.Version;
import org.apache.netbeans.modules.python4nb.util.ExternalExecutable;


//import org.apache.netbeans.modules.python4nb.editor.options.PythonOptionsValidator;

//import org.netbeans.modules.javascript.nodejs.file.PythonPackage;
//import org.netbeans.modules.javascript.nodejs.options.NodeJsOptions;
//import org.netbeans.modules.javascript.nodejs.options.NodeJsOptionsValidator;
//import org.netbeans.modules.javascript.nodejs.platform.PythonSupport;
//import org.netbeans.modules.javascript.nodejs.preferences.PythonPreferences;
//import org.netbeans.modules.javascript.nodejs.preferences.PythonPreferencesValidator;
////import org.netbeans.modules.javascript.nodejs.ui.customizer.NodeJsCustomizerProvider;
//import org.netbeans.modules.javascript.nodejs.ui.options.PythonOptionsPanelController;
//import org.netbeans.modules.javascript.nodejs.util.FileUtils;
//import org.netbeans.modules.javascript.nodejs.util.PythonUtils;
//import org.netbeans.modules.javascript.nodejs.util.StringUtils;
//import org.netbeans.modules.javascript.nodejs.util.ValidationUtils;
// TODO: Determine how to handle debugging and remote connection
//// import org.netbeans.modules.javascript.v8debug.api.Connector;
//// import org.netbeans.modules.javascript.v8debug.api.DebuggerOptions;
//import org.netbeans.modules.web.common.api.ValidationResult;
//import org.netbeans.modules.web.common.api.Version;
//import org.netbeans.modules.web.common.ui.api.ExternalExecutable;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.Pair;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;

public class PythonExecutable {

    static final Logger LOGGER = Logger.getLogger(PythonExecutable.class.getName());

    @org.netbeans.api.annotations.common.SuppressWarnings(value = "MS_MUTABLE_ARRAY", justification = "Just internal usage")
    public static final String[] PYTHON_NAMES;
    //TODO: Need to determine if debug port for use with Python is needed
    public static final int DEFAULT_DEBUG_PORT = 9292;

    private static final String JYTHON_NAME;

    /* TODO: Need to determine if to perform debug with Python.  This may involve
    integration with pdb - see https://docs.python.org/3/library/pdb.html */
    private static final String DEBUG_BRK_COMMAND = "--debug-brk=%d"; // NOI18N
    private static final String DEBUG_COMMAND = "--debug=%d"; // NOI18N
    private static final String VERSION_PARAM = "--version"; // NOI18N

    // versions of python executables
    private static final ConcurrentMap<String, Version> VERSIONS = new ConcurrentHashMap<>();
    private static final Version UNKNOWN_VERSION = Version.fromDottedNotationWithFallback("0.0"); // NOI18N

    protected final Project project;
    protected final String pythonPath;


    static {
        // TODO: Anything else needed (iojs is a nodejs thing) and set for Windows?
        if (Utilities.isWindows()) {
            PYTHON_NAMES = new String[] {"python.exe"}; // NOI18N
            JYTHON_NAME = "jython.exe"; // NOI18N
        } else {
            PYTHON_NAMES = new String[] {"python"   /* , "nodejs" */ }; // NOI18N
            JYTHON_NAME = "jython"; // NOI18N
        }
    }


    PythonExecutable(String pythonPath, @NullAllowed Project project) {
       if (pythonPath == null  ) {
            throw new IllegalArgumentException("Invalid Path provided");
        }
        this.pythonPath = pythonPath;
        this.project = project;
    }

    @CheckForNull
    public static PythonExecutable getDefault(@NullAllowed Project project, boolean showOptions) {
        ValidationResult result = new PythonOptionsValidator()
                .validatePython(false)
                .getResult();
        if (validateResult(result) != null) {
            if (showOptions) {
                OptionsDisplayer.getDefault().open(PythonOptionsPanelController.OPTIONS_PATH);
            }
            return null;
        }
        return createExecutable(PythonOptions.getInstance().getPython(), project);
    }

    @CheckForNull
    public static PythonExecutable forProject(Project project, boolean showCustomizer) {
        if (project == null) {
            throw new IllegalArgumentException("Invalid project: " + project);
        }
        return forProjectInternal(project, showCustomizer);
    }

    @CheckForNull
    public static PythonExecutable forPath(String path) {
        ValidationResult result = new ValidationResult();
        ValidationUtils.validatePython(result, path);;
        if (validateResult(result) != null) {
            return null;
        }
        return createExecutable(path, null);
    }

    @CheckForNull
    private static PythonExecutable forProjectInternal(@NullAllowed Project project, boolean showCustomizer) {
        if (project == null) {
            return getDefault(null, showCustomizer);
        }
        PythonPreferences preferences = PythonSupport.forProject(project).getPreferences();
        if (preferences.isDefaultPython()) {
            return getDefault(project, showCustomizer);
        }
        String node = preferences.getPython();
        ValidationResult result = new PythonPreferencesValidator()
                .validateNode(node)
                .getResult();
        if (validateResult(result) != null) {
            if (showCustomizer) {
                PythonCustomizerProvider.openCustomizer(project, result);
            }
            return null;
        }
        assert node != null;
        return createExecutable(node, project);
    }

    private static PythonExecutable createExecutable(String node, Project project) {
        if (Utilities.isMac()) {
            return new MacPythonExecutable(node, project);
        }
        return new PythonExecutable(node, project);
    }

    public String getExecutable() {
        return new ExternalExecutable(pythonPath).getExecutable();
    }

    String getCommand() {
        return pythonPath;
    }

    // TODO: THis is nodejs alternative; maybe could use for JYthon
    public boolean isJython() {
        File python = new File(new ExternalExecutable(pythonPath).getExecutable());
        if (python.getName().equals(JYTHON_NAME)) {
            return true;
        }
        // #250534 - selected "python" file in io.js sources?
        File iojs = new File(python.getParentFile(), JYTHON_NAME);
        // do not check if iojs exists but simply immediately compare their sizes
        return python.length() == iojs.length();
    }

    public void resetVersion() {
        VERSIONS.remove(pythonPath);
    }

    public boolean versionDetected() {
        return VERSIONS.containsKey(pythonPath);
    }

    // #255878
    @CheckForNull
    public Version getRealVersion() {
        // TODO: Update to determine version based on python call
//        detectVersion();
        Version version = VERSIONS.get(pythonPath);
        if (version == UNKNOWN_VERSION) {
            return null;
        }
        return version;
    }

    @CheckForNull
    public Version getVersion() {
        return getRealVersion();
    }

    @NbBundle.Messages({
        "PythonExecutable.version.detecting=Detecting Python version..."
    })
    private void detectVersion() {
        if (VERSIONS.get(pythonPath) != null) {
            return;
        }
        assert !EventQueue.isDispatchThread();
        VersionOutputProcessorFactory versionOutputProcessorFactory = new VersionOutputProcessorFactory();
        try {
            getExecutable("python --version") // NOI18N
                    .additionalParameters(getVersionParams())
                    .runAndWait(getSilentDescriptor(), versionOutputProcessorFactory, Bundle.PythonExecutable_version_detecting());
            String detectedVersion = versionOutputProcessorFactory.getVersion();
            if (detectedVersion != null) {
                Version version = Version.fromDottedNotationWithFallback(detectedVersion);
                VERSIONS.put(pythonPath, version);
                return;
            }
            // no version detected, store UNKNOWN_VERSION
            VERSIONS.put(pythonPath, UNKNOWN_VERSION);
        } catch (CancellationException ex) {
            // cancelled, cannot happen
            assert false;
        } catch (ExecutionException ex) {
            LOGGER.log(Level.INFO, null, ex);
        }
    }

    @NbBundle.Messages({
        "# {0} - project name",
        "PythonExecutable.run=Python ({0})",
    })
    @CheckForNull
    public AtomicReference<Future<Integer>> run(File script, String args) {
        assert project != null;
        String projectName = PythonUtils.getProjectDisplayName(project);
        AtomicReference<Future<Integer>> taskRef = new AtomicReference<>();
        Future<Integer> task = getExecutable(Bundle.PythonExecutable_run(projectName))
                .additionalParameters(getRunParams(script, args))
                .run(getDescriptor(taskRef));
        assert task != null : pythonPath;
        taskRef.set(task);
        return taskRef;
    }

    @NbBundle.Messages({
        "# {0} - project name",
        "PythonExecutable.debug=Python ({0})",
    })
    @CheckForNull
    public AtomicReference<Future<Integer>> debug(int port, File script, String args) {
        assert project != null;
        String projectName = PythonUtils.getProjectDisplayName(project);
        AtomicReference<Future<Integer>> taskRef = new AtomicReference<>();
        boolean[] useV8Debug = { false };
        final Future<Integer> task = getExecutable(Bundle.PythonExecutable_run(projectName))
                .additionalParameters(getDebugParams(port, script, args, useV8Debug))
                .run(getDescriptor(taskRef, useV8Debug[0] ? new DebugInfo(project, taskRef, port) : null));
        assert task != null : pythonPath;
        taskRef.set(task);
        return taskRef;
    }

    private ExternalExecutable getExecutable(String title) {
        assert title != null;
        return new ExternalExecutable(getCommand())
                .workDir(getWorkDir())
                .displayName(title)
                .optionsPath(PythonOptionsPanelController.OPTIONS_PATH)
                .noOutput(false);
    }

    private ExecutionDescriptor getDescriptor(AtomicReference<Future<Integer>> taskRef) {
        return getDescriptor(taskRef, null);
    }

    private ExecutionDescriptor getDescriptor(final AtomicReference<Future<Integer>> taskRef, @NullAllowed final DebugInfo debugInfo) {
        assert project != null;
        assert taskRef != null;
        List<URL> sourceRoots = PythonSupport.forProject(project).getSourceRoots();
        final LineConvertorFactoryImpl lineConvertorFactory = new LineConvertorFactoryImpl(sourceRoots, debugInfo);
        return ExternalExecutable.DEFAULT_EXECUTION_DESCRIPTOR
                .frontWindowOnError(false)
                .showSuspended(true)
                .optionsPath(PythonOptionsPanelController.OPTIONS_PATH)
                .outLineBased(true)
                .errLineBased(true)
                .outConvertorFactory(lineConvertorFactory)
                .errConvertorFactory(lineConvertorFactory)
                .preExecution(lineConvertorFactory.getPreExecution())
                .postExecution(lineConvertorFactory.getPostExecution())
                .rerunCallback(new ExecutionDescriptor.RerunCallback() {
                    @Override
                    public void performed(Future<Integer> task) {
                        taskRef.set(task);
                    }
                });
    }

    private static ExecutionDescriptor getSilentDescriptor() {
        return new ExecutionDescriptor()
                .inputOutput(InputOutput.NULL)
                .inputVisible(false)
                .frontWindow(false)
                .showProgress(false)
                .charset(StandardCharsets.UTF_8);
    }

    private File getWorkDir() {
        if (project == null) {
            return FileUtils.TMP_DIR;
        }
        PythonPackage packageJson = PythonSupport.forProject(project).getPythonPackage();
        if (packageJson.exists()) {
            return new File(packageJson.getPath()).getParentFile();
        }
        File sourceRoot = PythonUtils.getSourceRoot(project);
        if (sourceRoot != null) {
            return sourceRoot;
        }
        File workDir = FileUtil.toFile(project.getProjectDirectory());
        assert workDir != null : project.getProjectDirectory();
        return workDir;
    }

    private List<String> getRunParams(File script, String args) {
        return getParams(getScriptArgsParams(script, args));
    }

    private List<String> getDebugParams(int port, File script, String args, boolean[] useV8Debug) {
        List<String> params = new ArrayList<>();
        List<StartupExtender> extenders = StartupExtender.getExtenders(project.getLookup(), StartupExtender.StartMode.DEBUG);
        for (StartupExtender e : extenders) {
            params.addAll(e.getArguments());
        }
        if (params.isEmpty()) {
            params.add(String.format(getDebugCommand(), port));
            useV8Debug[0] = true;
        }
        params.addAll(getScriptArgsParams(script, args));
        return getParams(params);
    }

    private String getDebugCommand() {
        // TODO: Determine how to handle debugging in Python
//        if (DebuggerOptions.getInstance().isBreakAtFirstLine()) {
//            return DEBUG_BRK_COMMAND;
//        }
        return DEBUG_COMMAND;
    }

    private List<String> getVersionParams() {
        return getParams(Collections.singletonList(VERSION_PARAM));
    }

    private List<String> getScriptArgsParams(File script, String args) {
        assert script != null;
        List<String> params = new ArrayList<>();
        params.add(script.getAbsolutePath());
        if (StringUtils.hasText(args)) {
            params.addAll(Arrays.asList(Utilities.parseParameters(args)));
        }
        return params;
    }

    List<String> getParams(List<String> params) {
        assert params != null;
        return params;
    }

    @CheckForNull
    private static String validateResult(ValidationResult result) {
        if (result.isFaultless()) {
            return null;
        }
        if (result.hasErrors()) {
            return result.getErrors().get(0).getMessage();
        }
        return result.getWarnings().get(0).getMessage();
    }

    //~ Inner classes

    private static final class MacPythonExecutable extends PythonExecutable {

        private static final String BASH_COMMAND = "/bin/bash -lc"; // NOI18N


        MacPythonExecutable(String nodePath, Project project) {
            super(nodePath, project);
        }

        @Override
        String getCommand() {
            return BASH_COMMAND;
        }

        @Override
        List<String> getParams(List<String> params) {
            StringBuilder sb = new StringBuilder(200);
            sb.append("\""); // NOI18N
            sb.append(pythonPath);
            sb.append("\" \""); // NOI18N
            sb.append(StringUtils.implode(super.getParams(params), "\" \"")); // NOI18N
            sb.append("\""); // NOI18N
            return Collections.singletonList(sb.toString());
        }

    }

    static class VersionOutputProcessorFactory implements ExecutionDescriptor.InputProcessorFactory2 {

        private static final Pattern VERSION_PATTERN = Pattern.compile("^v([\\d\\.]+)$"); // NOI18N

        volatile String version;


        @Override
        public InputProcessor newInputProcessor(InputProcessor defaultProcessor) {
            return InputProcessors.bridge(new LineProcessor() {

                @Override
                public void processLine(String line) {
                    assert version == null : version + " :: " + line;
                    version = parseVersion(line);
                }

                @Override
                public void reset() {
                }

                @Override
                public void close() {
                }

            });
        }

        @CheckForNull
        public String getVersion() {
            return version;
        }

        public String parseVersion(String line) {
            Matcher matcher = VERSION_PATTERN.matcher(line);
            if (matcher.matches()) {
                return matcher.group(1);
            }
            LOGGER.log(Level.INFO, "Unexpected python version line: {0}", line);
            return null;
        }

    }

    private static final class LineConvertorFactoryImpl implements ExecutionDescriptor.LineConvertorFactory {

        private final List<File> files;
        private final Runnable preExecution;
        private final Runnable postExecution;
        private LineConvertorImpl executionLineConvertor;


        public LineConvertorFactoryImpl(List<URL> sourceRoots, @NullAllowed DebugInfo debugInfo) {
            assert sourceRoots != null;
            files = new CopyOnWriteArrayList<>(toFiles(sourceRoots));
            this.preExecution = () -> {
                executionLineConvertor = new LineConvertorImpl(new FileLineParser(files), debugInfo);
            };
            this.postExecution = () -> {
                executionLineConvertor = null;
            };
        }

        Runnable getPreExecution() {
            return preExecution;
        }

        Runnable getPostExecution() {
            return postExecution;
        }

        @Override
        public LineConvertor newLineConvertor() {
            return executionLineConvertor;
        }

        private List<File> toFiles(List<URL> sourceRoots) {
            List<File> result = new ArrayList<>(sourceRoots.size());
            for (URL sourceRoot : sourceRoots) {
                try {
                    result.add(Utilities.toFile(sourceRoot.toURI()));
                } catch (URISyntaxException ex) {
                    LOGGER.log(Level.INFO, null, ex);
                }
            }
            return result;
        }

    }

    private static final class LineConvertorImpl implements LineConvertor {
// TODO: Maybe change to pdb here instead f "python"
        private static final RequestProcessor RP = new RequestProcessor("python debugger starter/connector"); // NOI18N

        private final FileLineParser fileLineParser;
        @NullAllowed
        private final DebugInfo debugInfo;
        @NullAllowed
        final CountDownLatch debuggerCountDownLatch;


        volatile boolean debugging = false;


        public LineConvertorImpl(FileLineParser fileLineParser, @NullAllowed DebugInfo debugInfo) {
            if (fileLineParser == null) {
                throw new IllegalArgumentException("Invalid fileLineParser: " + fileLineParser);
            }
            this.fileLineParser = fileLineParser;
            this.debugInfo = debugInfo;
            if (debugInfo == null) {
                debuggerCountDownLatch = null;
            } else {
                debuggerCountDownLatch = new CountDownLatch(1);
                RP.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            assert debuggerCountDownLatch != null;
                            boolean expected = debuggerCountDownLatch.await(5, TimeUnit.SECONDS);
                            // #252451
                            if (!expected) {
                                // TODO: Update to be compatible with Python Debugger (pdb)
                                LOGGER.log(Level.INFO, "Connect python debugger timeout elapsed");
                            }
//                            connectDebugger();
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });
            }
        }

        @Override
        public List<ConvertedLine> convert(String line) {
            // debugger?
            if (debugInfo != null
                    && !debugging) {
                if (line.toLowerCase(Locale.US).startsWith("debugger listening on ")) { // NOI18N
                    assert debuggerCountDownLatch != null;
                    debuggerCountDownLatch.countDown();
                }
            }
            // process output
            OutputListener outputListener = null;
            Pair<File, Integer> fileLine = fileLineParser.getOutputFileLine(line);
            if (fileLine != null) {
                outputListener = new FileOutputListener(fileLine.first(), fileLine.second());
            }
            return Collections.singletonList(ConvertedLine.forText(line, outputListener));
        }

//        void connectDebugger() {
//            assert debugInfo != null;
//            Connector.Properties props = createConnectorProperties("localhost", debugInfo.port, debugInfo.project); // NOI18N
//            try {
//                Connector.connect(props, new Runnable() {
//                    @Override
//                    public void run() {
//                        debugging = false;
//                        assert debugInfo != null;
//                        assert debugInfo.project != null;
//                        assert debugInfo.taskRef != null;
//                        Future<Integer> task = debugInfo.taskRef.get();
//                        assert task != null : debugInfo.project.getProjectDirectory();
//                        task.cancel(true);
//                    }
//                });
//                debugging = true;
//            } catch (IOException ex) {
//                LOGGER.log(Level.INFO, "cannot run python.js debugger", ex);
//                warnCannotDebug(ex);
//            }
//        }

        /** TODO: Determine how to establish connections and ports in the context of
         * python projects
        private static Connector.Properties createConnectorProperties(String host, int port, Project project) {
            List<File> sourceRoots = PythonUtils.getSourceRoots(project);
            List<File> siteRoots = PythonUtils.getSiteRoots(project);
            List<String> localPaths = new ArrayList<>(sourceRoots.size());
            List<String> localPathsExclusionFilter = Collections.emptyList();
            for (File src : sourceRoots) {
                localPaths.add(src.getAbsolutePath());
                for (File site : siteRoots) {
                    if (FileUtils.isSubdirectoryOf(src, site) && !src.equals(site)) {
                        if (localPathsExclusionFilter.isEmpty()) {
                            localPathsExclusionFilter = new ArrayList<>();
                        }
                        localPathsExclusionFilter.add(site.getAbsolutePath());
                    }
                }
            }
            return new Connector.Properties(host, port, localPaths, Collections.<String>emptyList(), localPathsExclusionFilter);
        } */

//        @NbBundle.Messages({
//            "# {0} - reason",
//            "LineConvertorImpl.warn.debug=Cannot run debugger. Reason:\n\n{0}",
//        })
//        protected void warnCannotDebug(IOException ex) {
//            NotifyDescriptor.Message descriptor = new NotifyDescriptor.Message(Bundle.LineConvertorImpl_warn_debug(ex), NotifyDescriptor.ERROR_MESSAGE);
//            DialogDisplayer.getDefault().notifyLater(descriptor);
//        }

    }

    private static final class FileOutputListener implements OutputListener {

        final File file;
        final int line;


        public FileOutputListener(File file, int line) {
            if (file == null) {
                throw new IllegalArgumentException("Invalid file: " + file);
            }
            this.file = file;
            this.line = line;
        }

        @Override
        public void outputLineSelected(OutputEvent ev) {
            // noop
        }

        @Override
        public void outputLineAction(OutputEvent ev) {
            RequestProcessor.getDefault().post(new Runnable() {
                @Override
                public void run() {
                    FileUtils.openFile(file, line);
                }
            });
        }

        @Override
        public void outputLineCleared(OutputEvent ev) {
            // noop
        }

    }

    static final class FileLineParser {

        // at python.js:906:3
        // (/home/gapon/NetBeansProjects/JsLibrary6/src/main.js:9:1)
        // ^/home/gapon/NetBeansProjects/JsLibrary6/src/main.js:9$
        static final Pattern OUTPUT_FILE_LINE_PATTERN = Pattern.compile("(?:at |\\(|^)(?<FILE>[^:(]+?):(?<LINE>\\d+)(?::\\d+)?(?:\\)|$)"); // NOI18N

        private final List<File> sourceRoots;


        public FileLineParser(List<File> sourceRoots) {
            assert sourceRoots != null;
            this.sourceRoots = sourceRoots;
        }

        @CheckForNull
        Pair<File, Integer> getOutputFileLine(String line) {
            Pair<String, Integer> fileNameLine = getOutputFileNameLine(line);
            if (fileNameLine == null) {
                return null;
            }
            String filepath = fileNameLine.first();
            Integer lineNumber = fileNameLine.second();
            // complete path?
            File file = new File(filepath);
            if (file.isFile()) {
                return Pair.of(file, lineNumber);
            }
            // incomplete path?
            for (File sourceRoot : sourceRoots) {
                file = new File(sourceRoot, filepath);
                if (file.isFile()) {
                    return Pair.of(file, lineNumber);
                }
            }
            return null;
        }

        @CheckForNull
        static Pair<String, Integer> getOutputFileNameLine(String line) {
            Matcher matcher = OUTPUT_FILE_LINE_PATTERN.matcher(line.trim());
            if (!matcher.find()) {
                return null;
            }
            return Pair.of(matcher.group("FILE"), Integer.valueOf(matcher.group("LINE"))); // NOI18N
        }

    }

    // TODO: Update to be compatible with Python Debugger (pdb)
    private static final class DebugInfo {

        @NonNull
        public final Project project;
        @NonNull
        public final AtomicReference<Future<Integer>> taskRef;
        public final int port;


        public DebugInfo(Project project, AtomicReference<Future<Integer>> taskRef, int port) {
            assert project != null;
            assert taskRef != null;
            this.project = project;
            this.taskRef = taskRef;
            this.port = port;
        }

    }
}
