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

import org.apache.netbeans.modules.python4nb.exec.PythonExecution;
import org.apache.netbeans.modules.python4nb.platform.PythonPlatform;
import org.apache.netbeans.modules.python4nb.project.PythonProject;
import org.apache.netbeans.modules.python4nb.project.PythonProjectProperties;
import org.apache.netbeans.modules.python4nb.ui.Utils;

import org.netbeans.api.project.ProjectUtils;
//import org.netbeans.modules.python.editor.codecoverage.PythonCoverageProvider;
//import org.netbeans.modules.python.project.PythonActionProvider;
//import org.netbeans.modules.python.project.spi.TestRunner;
//import org.netbeans.modules.python.project.ui.Utils;
import org.netbeans.spi.project.ActionProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

public class RunCommand extends Command {
    protected final boolean isTest;

    public RunCommand(PythonProject project, boolean isTest) {
        super(project);
        this.isTest = isTest;
    }

    @Override
    public String getCommandId() {
        return isTest ? ActionProvider.COMMAND_TEST : ActionProvider.COMMAND_RUN;
    }

    @Override
    public void invokeAction(Lookup context) throws IllegalArgumentException {
// TODO: Add additional test functionality during execution
//        if (isTest) {
//            TestRunner testRunner = PythonActionProvider.getTestRunner(TestRunner.TestType.PY_UNIT);
//            //boolean testTaskExist = RakeSupport.getRakeTask(project, TEST_TASK_NAME) != null;
//            //if (testTaskExist) {
//            //    File pwd = FileUtil.toFile(project.getProjectDirectory());
//            //    RakeRunner runner = new RakeRunner(project);
//            //    runner.setPWD(pwd);
//            //    runner.setFileLocator(new RubyFileLocator(context, project));
//            //    runner.showWarnings(true);
//            //    runner.setDebug(COMMAND_DEBUG_SINGLE.equals(command));
//            //    runner.run(TEST_TASK_NAME);
//            /*} else */if (testRunner != null) { // don't invoke null.getInstance()...
//                    testRunner.getInstance().runAllTests(getProject(), false);
//                }
//        }

        final PythonProject pyProject = getProject();
        final PythonPlatform platform = checkProjectPythonPlatform(pyProject);
        if ( platform == null )
          return ; // invalid platform user has been warn in check so safe to return
         
//        PythonProjectProperties projectProperties = getProperties();
//        
//        // projects main module not selected yet for the project
//        if (projectProperties.getMainModule() == null ||
//                projectProperties.getMainModule().equals("")){
//            String main = Utils.chooseMainModule(getProject());
//            projectProperties.setMainModule(main);
//            projectProperties.save();
//        }

        // get the file object for the main file of the project
        FileObject script = findMainFile(pyProject);       
        assert script != null;
        
//        // if main file not found then trigger user to select file
//        if (script == null ){
//            String main = Utils.chooseMainModule(getProject());
//            projectProperties.setMainModule(main);
//            projectProperties.save();
//            script = findMainFile(pyProject);
//        }
        FileObject parent = script.getParent();
        PythonExecution pyexec = new PythonExecution();
        pyexec.setDisplayName (ProjectUtils.getInformation(pyProject).getDisplayName());                
        //Set work dir - probably we need a property to store work dir
        String path = FileUtil.toFile(parent).getAbsolutePath();
        pyexec.setWorkingDirectory(path);        
        pyexec.setCommand(platform.getInterpreterCommand());
        //Set python script
        path = FileUtil.toFile(script).getAbsolutePath();
        pyexec.setScript(path);
        pyexec.setCommandArgs(platform.getInterpreterArgs());
        pyexec.setScriptArgs(pyProject.getProperties().getApplicationArgs());
        //build path & set 
        //build path & set
        pyexec.setPath(PythonPlatform.buildPath(super.buildPythonPath(platform,pyProject)));
        pyexec.setJavaPath(PythonPlatform.buildPath(super.buildJavaPath(platform,pyProject)));
        pyexec.setShowControls(true);
        pyexec.setShowInput(true);
        pyexec.setShowWindow(true);
        pyexec.addStandardRecognizers();

//        PythonCoverageProvider coverageProvider = PythonCoverageProvider.get(pyProject);
//        if (coverageProvider != null && coverageProvider.isEnabled()) {
//            pyexec = coverageProvider.wrapWithCoverage(pyexec);
//        }

        pyexec.run();
    }

    @Override
    public boolean isActionEnabled(Lookup context) throws IllegalArgumentException {
//        final PythonProject pyProject = getProject();
//        PythonPlatform platform = PythonProjectUtil.getActivePlatform(pyProject);
//        if (platform == null) {
//            return false;
//        }
//        else{
//            return true;
//        }
//        final FileObject fo = findMainFile (pyProject);
//        if (fo == null) {
//            return false;
//        }
//        return PythonMIMEResolver.PYTHON_MIME_TYPE.equals(fo.getMIMEType());
        return true;
    }
    
    protected static FileObject findMainFile (final PythonProject pyProject) {
        PythonProjectProperties projectProperties = pyProject.getProperties();
        
        // projects main module not selected yet for the project
        if (projectProperties.getMainModule() == null ||
                projectProperties.getMainModule().equals("")){
//            String main = Utils.chooseMainModule(getProject());
            String main = Utils.chooseMainModule(pyProject);
            projectProperties.setMainModule(main);
            projectProperties.save();
        }
        
        final FileObject[] roots = pyProject.getSourceRoots().getRoots();
        final String mainFile = pyProject.getProperties().getMainModule();
        if (mainFile == null) {
            return null;
        }
        FileObject fo = null;
        for (FileObject root : roots) {
            fo = root.getFileObject(mainFile);
            if (fo != null) {
                break;
            }
        }
        return fo;
    }

}
