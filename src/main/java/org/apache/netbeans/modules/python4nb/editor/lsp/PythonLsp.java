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
package org.apache.netbeans.modules.python4nb.editor.lsp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.editor.mimelookup.MimeRegistrations;
import org.apache.netbeans.modules.python4nb.editor.file.MIMETypes;
import org.apache.netbeans.modules.python4nb.editor.options.PythonOptions;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.Exceptions;
import org.openide.util.Pair;
import org.netbeans.api.project.Project;
import org.netbeans.modules.lsp.client.spi.LanguageServerProvider;
import org.openide.util.Lookup;

/**
 *
 * @author bresie
 */
@MimeRegistrations({
    @MimeRegistration(service = LanguageServerProvider.class,
            mimeType = MIMETypes.PY),
    @MimeRegistration(service = LanguageServerProvider.class,
            mimeType = MIMETypes.PYC),
    @MimeRegistration(service = LanguageServerProvider.class,
            mimeType = MIMETypes.PYO)
})
public class PythonLsp implements LanguageServerProvider {

    private static final Logger LOG = Logger.getLogger(PythonLsp.class.getName());

    private static final Map<Project, Pair<Process, LanguageServerDescription>> prj2Server = new HashMap<>();
    @StaticResource
    private static final String PYTHON_ICON = "org/apache/netbeans/modules/python4nb/editor/py.png"; // NOI18N

    @Override
    public LanguageServerDescription startServer(Lookup lookup) {
        LOG.log(Level.INFO, "Starting Python LSP Server");

        try {
            // get python location
            PythonOptions options = PythonOptions.getInstance();
            final String python = options.getPython();

            // TODO: Add handling when python not configured
            
            // TODO: Check if Python LSP available to be run

            // TODO: get python support based on project settings
            
            //   Project prj = lookup.lookup(Project.class);
            //   if (prj == null) {
            //       return null;
            //   }
            // PythonSupport support = PythonSupport.forProject(prj);
            // support = PythonSupport.forProject(prj);
            // PythonPreferences pyPreferences = support.getPreferences();
            LOG.log(Level.INFO, "Starting up Python LSP Server using {0}", python);

            LOG.log(Level.INFO, "Started up Python LSP Server");
            ProcessBuilder pythonServerBuilder = new ProcessBuilder(python, "-m", "pyls");
            // setup python-language-server with python.exe -m pip install python-language-server
            // https://pypi.org/project/python-language-server/
            
            Process pythonServerProcess = pythonServerBuilder.redirectError(ProcessBuilder.Redirect.INHERIT).start();
            // TODO: If unable to start pyls support need to error out and/or notify user for setup
            
            LOG.log(Level.INFO, "Python LSP establish input-output for server.");
            InputStream inputStream = pythonServerProcess.getInputStream();
            OutputStream outputStream = pythonServerProcess.getOutputStream();
            LanguageServerDescription lspDescription
                    = LanguageServerDescription.create(inputStream, outputStream, pythonServerProcess);
            return lspDescription;
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Failure on startup of Python LSP server.", ex);
            Exceptions.printStackTrace(ex);
            return null;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Failure on startup of Python LSP server.", ex);
            Exceptions.printStackTrace(ex);
            return null;
        }
    }
    
    /* TODO: The below commented code is for reference, once more robust this 
    code can be removed.
    
    Possible helpful examples
    https://github.com/apache/netbeans/blob/master/webcommon/typescript.editor/src/org/netbeans/modules/typescript/editor/TypeScriptLSP.java
    Based on CCPLite implementations        
    */
//        ServerRestarter restarter = lookup.lookup(ServerRestarter.class);
//        Utils.settings().addPreferenceChangeListener(new PreferenceChangeListener() {
//            @Override
//            public void preferenceChange(PreferenceChangeEvent evt) {
//                if (evt.getKey() == null || Utils.KEY_CCLS_PATH.equals(evt.getKey()) || Utils.KEY_CLANGD_PATH.equals(evt.getKey())) {
//                    prj2Server.remove(prj);
//                    restarter.restart();
//                    Utils.settings().removePreferenceChangeListener(this);
//                }
//            }
//        });
//        String ccls = Utils.getCCLSPath();
//        String clangd = Utils.getCLANGDPath();
//        if (ccls != null || clangd != null) {
//            return prj2Server.compute(prj, (p, pair) -> {
//                if (pair != null && pair.first().isAlive()) {
//                    return pair;
//                }
//                try {
//                    List<String> command = new ArrayList<>();
//
//                    CProjectConfigurationProvider config = (prj);
//                    config.addChangeListener(new ChangeListener() {
//                        @Override
//                        public void stateChanged(ChangeEvent e) {
//                            prj2Server.remove(prj);
//                            restarter.restart();
//                            config.removeChangeListener(this);
//                        }
//                    });
//                    File compileCommandDirs = getCompileCommandsDir(config);
//
//                    if (compileCommandDirs != null) {
//                        if (ccls != null) {
//                            command.add(ccls);
//                            StringBuilder initOpt = new StringBuilder();
//                            initOpt.append("--init={\"compilationDatabaseDirectory\":\"");
//                            initOpt.append(compileCommandDirs.getAbsolutePath());
//                            initOpt.append("\"}");
//                            command.add(initOpt.toString());
//                        } else {
//                            command.add(clangd);
//                            command.add("--compile-commands-dir=" + compileCommandDirs.getAbsolutePath());
//                            command.add("--clang-tidy");
//                            command.add("--completion-style=detailed");
//                        }
//                        ProcessBuilder builder = new ProcessBuilder(command);
//                        if (LOG.isLoggable(Level.FINEST)) {
//                            builder.redirectError(Redirect.INHERIT);
//                        }
//                        Process process = builder.start();
//                        InputStream in = process.getInputStream();
//                        OutputStream out = process.getOutputStream();
//                        if (LOG.isLoggable(Level.FINEST)) {
//                            in = new CopyInput(in, System.err);
//                            out = new CopyOutput(out, System.err);
//                        }
//                        return Pair.of(process, LanguageServerDescription.create(in, out, process));
//                    }
//                    return null;
//                } catch (IOException ex) {
//                    LOG.log(Level.FINE, null, ex);
//                    return null;
//                }
//            }).second();
//        }
//        return null;
//    }
//    public static File getCompileCommandsDir(Project prj) {
//        return getCompileCommandsDir(getProjectSettings(prj));
//    }
//    private static CProjectConfigurationProvider getProjectSettings(Project prj) {
//        CProjectConfigurationProvider configProvider = prj.getLookup().lookup(CProjectConfigurationProvider.class);
//        if (configProvider == null) {
//            configProvider = new CProjectConfigurationProvider() {
//                @Override
//                public ProjectConfiguration getProjectConfiguration() {
//                    return new ProjectConfiguration(new File(FileUtil.toFile(prj.getProjectDirectory()), "compile_commands.json").getAbsolutePath());
//                }
//                @Override
//                public void addChangeListener(ChangeListener listener) {
//                }
//                @Override
//                public void removeChangeListener(ChangeListener listener) {
//                }
//            };
//        }
//        return configProvider;
//    }
//
//    private static int tempDirIndex = 0;
//
//    private static File getCompileCommandsDir(CProjectConfigurationProvider configProvider) {
//        ProjectConfiguration config = configProvider.getProjectConfiguration();
//
//        if (config == null) {
//            return null;
//        }
//
//        File commandsPath = config.commandJsonPath != null ? new File(config.commandJsonPath) : null;
//
//        if (config.commandJsonCommand != null || (commandsPath != null && commandsPath.canRead()) || config.commandJsonContent != null) {
//            File tempFile = Places.getCacheSubfile("cpplite/compile_commands/" + tempDirIndex++ + "/compile_commands.json");
//            if (config.commandJsonCommand != null) {
//                try {
//                    new ProcessBuilder(config.commandJsonCommand).redirectOutput(tempFile).redirectError(Redirect.INHERIT).start().waitFor();
//                } catch (IOException | InterruptedException ex) {
//                    LOG.log(Level.WARNING, null, ex);
//                    return null;
//                }
//            } else if (commandsPath != null && commandsPath.canRead()) {
//                try (InputStream in = new FileInputStream(commandsPath);
//                     OutputStream out = new FileOutputStream(tempFile)) {
//                    FileUtil.copy(in, out);
//                } catch (IOException ex) {
//                    LOG.log(Level.WARNING, null, ex);
//                    return null;
//                }
//            } else if (config.commandJsonContent != null) {
//                try (OutputStream out = new FileOutputStream(tempFile)) {
//                    out.write(config.commandJsonContent.getBytes());
//                } catch (IOException ex) {
//                    LOG.log(Level.WARNING, null, ex);
//                    return null;
//                }
//            } else {
//                return null;
//            }
//            return tempFile.getParentFile();
//        }
//        return null;
//    }
//
//    private static class CopyInput extends InputStream {
//
//        private final InputStream delegate;
//        private final OutputStream log;
//
//        public CopyInput(InputStream delegate, OutputStream log) {
//            this.delegate = delegate;
//            this.log = log;
//        }
//
//        @Override
//        public int read() throws IOException {
//            int read = delegate.read();
//            log.write(read);
//            return read;
//        }
//    }
//
//    private static class CopyOutput extends OutputStream {
//
//        private final OutputStream delegate;
//        private final OutputStream log;
//
//        public CopyOutput(OutputStream delegate, OutputStream log) {
//            this.delegate = delegate;
//            this.log = log;
//        }
//
//        @Override
//        public void write(int b) throws IOException {
//            delegate.write(b);
//            log.write(b);
//            log.flush();
//        }
//
//        @Override
//        public void flush() throws IOException {
//            delegate.flush();
//            log.flush();
//        }
//
//    }
}
