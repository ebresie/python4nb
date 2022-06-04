/*
 * Copyright 2022 Eric Bresie and friends. All rights reserved.
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

// Portions of this code are based on nbPython Code.  

package org.apache.netbeans.modules.python4nb.ui.actions;

import javax.swing.JOptionPane;
import org.apache.netbeans.modules.python4nb.exec.PythonExecution;
import org.apache.netbeans.modules.python4nb.editor.file.MIMETypes; // MIMEResolver;
import org.apache.netbeans.modules.python4nb.editor.options.PythonOptions;
import org.apache.netbeans.modules.python4nb.platform.PythonPlatform;
import org.apache.netbeans.modules.python4nb.platform.PythonPlatformManager;

//import org.netbeans.modules.python.editor.codecoverage.PythonCoverageProvider;
//import org.netbeans.modules.python.project.GotoTest;
import org.apache.netbeans.modules.python4nb.project.PythonActionProvider;
import org.apache.netbeans.modules.python4nb.project.PythonProject;
//import org.netbeans.modules.python.project.spi.TestRunner;
import org.netbeans.spi.gototest.TestLocator.LocationResult;
import org.netbeans.spi.project.ActionProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;

import org.openide.util.Lookup;

// TODO: Implement additional test and coverage functionality

public class RunSingleCommand extends Command {
    protected boolean isTest;

    public RunSingleCommand(PythonProject project, boolean isTest) {
        super(project);
        this.isTest = isTest;
    }

    @Override
    public String getCommandId() {
        return isTest ? ActionProvider.COMMAND_TEST_SINGLE : ActionProvider.COMMAND_RUN_SINGLE;
    }

    @Override
    public void invokeAction(Lookup context) throws IllegalArgumentException {
        Node[] activatedNodes = getSelectedNodes();
        DataObject gdo = activatedNodes[0].getLookup().lookup(DataObject.class);
        FileObject file = gdo.getPrimaryFile();
        if (file.getMIMEType().equals(MIMETypes.PYTHON_MIME_TYPE) ){
            String path = FileUtil.toFile(file.getParent()).getAbsolutePath();
            // String workingdir = FileUtil.toFile(getProject().getSrcFolder()).getAbsolutePath();
            //int pos = path.lastIndexOf("/");
            //path = path.substring(0, pos);
            String script = FileUtil.toFile(file).getAbsolutePath();

            final PythonProject pyProject = getProject();

            //String target = FileUtil.getRelativePath(getRoot(project.getSourceRoots().getRoots(),file), file);
            if (isTest || file.getName().endsWith("_test")) { // NOI18N

                // See if this looks like a test file; if not, see if we can find its corresponding
                // test
                boolean isTestFile = (file.getName().endsWith("_test"));
                if (!isTestFile) {
                    for (FileObject testRoot : pyProject.getTestSourceRootFiles()) {
                        if (FileUtil.isParentOf(testRoot, file)) {
                            isTestFile = true;
                            break;
                        }
                    }
                }
                
                // TODO: Add additional test functionality
//                if (!isTestFile) {
//                    // Try to find the matching test
//                    LocationResult result = new GotoTest().findTest(file, -1);
//                    if (result != null && result.getFileObject() != null) {
//                        file = result.getFileObject();
//                    }
//                }

//                // Run test normally - don't pop up browser
//                TestRunner testRunner = PythonActionProvider.getTestRunner(TestRunner.TestType.PY_UNIT);
//                if (testRunner != null) {
//                    testRunner.getInstance().runTest(file, false);
//                    return;
//                }
            }

            PythonExecution pyexec = new PythonExecution();
            pyexec.setDisplayName(gdo.getName());
            pyexec.setWorkingDirectory(path);
//            if(PythonOptions.getInstance().getPromptForArgs()){
//               String args =  JOptionPane.showInputDialog("Enter the args for this script.", "");
//               pyexec.setScriptArgs(args);
//
//            }
            final PythonPlatform platform = checkProjectPythonPlatform(pyProject);
            if ( platform == null )
              return ; // invalid platform user has been warn in check so safe to return
            pyexec.setCommand(platform.getInterpreterCommand());
            pyexec.setScript(script);
            pyexec.setCommandArgs(platform.getInterpreterArgs());
            pyexec.setPath(PythonPlatform.buildPath(super.buildPythonPath(platform, pyProject)));
            pyexec.setJavaPath(PythonPlatform.buildPath(super.buildJavaPath(platform, pyProject)));
            pyexec.setShowControls(true);
            pyexec.setShowInput(true);
            pyexec.setShowWindow(true);
            pyexec.addStandardRecognizers();

//            PythonCoverageProvider coverageProvider = PythonCoverageProvider.get(pyProject);
//            if (coverageProvider != null && coverageProvider.isEnabled()) {
//                pyexec = coverageProvider.wrapWithCoverage(pyexec);
//            }

            pyexec.run();
        }
    }

    @Override
    public boolean isActionEnabled(Lookup context) throws IllegalArgumentException {
        boolean results = false; //super.enable(activatedNodes);
        Node[] activatedNodes = getSelectedNodes();
        if(activatedNodes != null && activatedNodes.length > 0){
            DataObject gdo = activatedNodes[0].getLookup().lookup(DataObject.class);
            if(gdo != null && gdo.getPrimaryFile() != null)
                results = gdo.getPrimaryFile().getMIMEType().equals(
                        MIMETypes.PYTHON_MIME_TYPE);
        }
        return results;
    }

}
