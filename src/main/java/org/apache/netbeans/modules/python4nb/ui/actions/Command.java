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

import java.io.File;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.apache.netbeans.modules.python4nb.platform.PythonPlatform;
import org.apache.netbeans.modules.python4nb.platform.PythonPlatformManager;
import org.apache.netbeans.modules.python4nb.project.PythonProject;
import org.apache.netbeans.modules.python4nb.project.PythonProjectUtil;
import org.apache.netbeans.modules.python4nb.project.PythonProjectProperties;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

public abstract class Command { 
// TODO: Determine if need to extend from org.netbeans.api.lsp.Command{

    private final PythonProject project;
    private final PythonProjectProperties properties;
    public Command(PythonProject project) {
        this.project = project;
        assert project != null;
        properties = new PythonProjectProperties(this.project);
    }

    public abstract String getCommandId();

    public abstract void invokeAction(Lookup context) throws IllegalArgumentException;

    public abstract boolean isActionEnabled(Lookup context) throws IllegalArgumentException;

    public boolean asyncCallRequired() {
        return true;
    }

    public boolean saveRequired() {
        return true;
    }

    public final PythonProject getProject() {
        return project;
    }
    public Node[] getSelectedNodes(){
        return TopComponent.getRegistry().getCurrentNodes();
    }

    protected PythonProjectProperties getProperties() {
        return properties;
    }

    protected void showLaunchError( String message ){
      JOptionPane.showMessageDialog(null,message ,"Python Launch Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * used by children to handle severe launched errors
     * @param errMessage
     */
    protected PythonPlatform checkProjectPythonPlatform( PythonProject pyProject ){
       PythonPlatform platform = PythonProjectUtil.getActivePlatform(pyProject);
       if ( platform == null ) {
         // Better to inform the user than try to use a default unsuited
        String platformId = pyProject.getProperties().getActivePlatformId();
         showLaunchError( "The selected project specifies a missing or invalid Python platform : " + // NOI18N
                           platformId +
                           "\nPlease add the Python platform or choose an existing one in Project Properties. " // NOI18N
                         );
       }
       return platform ;
    }

    /**
     *
     * provide a reasonable common Build of PYTHONPATH for Run or Debug commands
     *
     * @param platform current platform
     * @param project current project
     * @return PythonPath FileList
     */
    protected ArrayList<String> buildPythonPath( PythonPlatform platform , PythonProject project ) {
      final ArrayList<String> pythonPath = new ArrayList<>() ;
      // start with platform
      pythonPath.addAll(platform.getPythonPath());
      for (FileObject fo : project.getSourceRoots().getRoots()) {
        File f = FileUtil.toFile(fo);
        pythonPath.add(f.getAbsolutePath());
      }
      if (getProperties().getPythonPath() != null ) {
        pythonPath.addAll(getProperties().getPythonPath());
      }
      return pythonPath ;
    }

    /**
     *
     * provide a reasonable common Build of JAVAPATH for Run or Debug Jython commands
     * @param platform current platform
     * @param project current project
     * @return JavaPath fileList for jython CLASSPATH command
     */
    protected ArrayList<String> buildJavaPath( PythonPlatform platform , PythonProject project ) {
      final ArrayList<String> javaPath = new ArrayList<>() ;
      // start with platform
      javaPath.addAll(platform.getJavaPath());
      javaPath.addAll(getProperties().getJavaPath());
      return javaPath ;
    }

}
