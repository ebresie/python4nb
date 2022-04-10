/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.apache.netbeans.modules.python4nb.project;

import org.apache.netbeans.modules.python4nb.platform.PythonPlatform;
import org.apache.netbeans.modules.python4nb.platform.PythonPlatformManager;
import org.apache.netbeans.modules.python4nb.project.PythonProjectProperties;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.support.ant.AntProjectHelper;

public class PythonProjectUtil {
    
    private PythonProjectUtil() {}
    
    public static PythonProject getProject (final Project project) {
        assert project != null;
        return project.getLookup().lookup(PythonProject.class);
    }
    
    public static AntProjectHelper getProjectHelper (final Project project) {
        final PythonProject pyProject = getProject(project);
        return pyProject == null ? null : pyProject.getHelper();
    }
    
    public static PythonPlatform getActivePlatform (final Project project) {
        final PythonProject pp = getProject(project);
        if (pp == null) {
            return null;    //No Python project
        }
        final PythonPlatformManager manager = PythonPlatformManager.getInstance();
        String platformId = pp.getEvaluator().getProperty(PythonProjectProperties.ACTIVE_PLATFORM);
        if (platformId == null) {
            platformId = manager.getDefaultPlatform();
        }
        if (platformId == null) {
            return null;    //No Python platform in the IDE
        }
        return manager.getPlatform(platformId);
    }
   
}
