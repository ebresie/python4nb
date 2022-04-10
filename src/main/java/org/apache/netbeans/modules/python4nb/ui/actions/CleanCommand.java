
package org.apache.netbeans.modules.python4nb.ui.actions;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.netbeans.modules.python4nb.exec.PythonExecution;
import org.apache.netbeans.modules.python4nb.platform.PythonPlatform;
import org.apache.netbeans.modules.python4nb.project.PythonProject;
import org.apache.netbeans.modules.python4nb.project.PythonProjectProperties;
import org.netbeans.api.project.ProjectUtils;
//import org.netbeans.modules.python.editor.codecoverage.PythonCoverageProvider;
import org.apache.netbeans.modules.python4nb.project.PythonActionProvider;
//import org.netbeans.modules.python.project.spi.TestRunner;
import org.apache.netbeans.modules.python4nb.api.Util; //Utils;
import org.netbeans.spi.project.ActionProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

public class CleanCommand extends Command {
    

    public CleanCommand(PythonProject project) {
        super(project);
        
    }

    @Override
    public String getCommandId() {
        return ActionProvider.COMMAND_CLEAN;
    }

    @Override
    public void invokeAction(Lookup context) throws IllegalArgumentException {
        final FileObject[] roots = getProject().getSourceRoots().getRoots();
        //FileObject fo = null;
        for (FileObject root : roots) {
            traverse(FileUtil.toFile(root));
        }
    }

    @Override
    public boolean isActionEnabled(Lookup context) throws IllegalArgumentException {
        return true;
    }
    
    private Logger LOGGER = Logger.getLogger(CleanCommand.class.getName());


    private boolean searchNestedDirectoies = true;
    private void processAction(File dir) {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Inspecting: " + dir.getAbsolutePath());
        }
        if(dir.isFile()){
            int pos = dir.getName().indexOf(".");
            String name = null;
            String ext = null;
            if (pos > -1 ){
                 name = dir.getName().substring(0, pos);
                 ext = dir.getName().substring(pos+1);
            }else{
                name = dir.getName();
                ext = "";
            }
            if(ext.toLowerCase().equals("pyc") || ext.toLowerCase().equals("pyo"))
                dir.delete();
        }
        if(dir.isDirectory()){
            String[] children = dir.list();
            if(children != null){
                for (String child : children) {
                    traverse(new File(dir, child));
                }
            }

        }

    }

    public void traverse(File dir) {

        processAction(dir);

        if (dir.isDirectory()) {
            String[] children = dir.list();
            if(children != null){
                for (String child : children) {
                    traverse(new File(dir, child));
                }

            }
        }

    }

}
