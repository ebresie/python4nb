/*
 * Copyright 2022 ebres.
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
package org.apache.netbeans.modules.python4nb.project;

/**
 *
 * @author ebres
 */
import java.awt.Image;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

public class PythonProject implements  Project {

    private final FileObject projectDir;
    private final ProjectState state;
    private Lookup lkp;
    

    @StaticResource()
    public static final String PYTHON_PROJECT_ICON = "org/apache/netbeans/modules/python4nb/editor/py_module.png";
    PythonProject(FileObject dir, ProjectState state) {
        this.projectDir = dir;
        this.state = state;
    }

    @Override
    public FileObject getProjectDirectory() {
        return projectDir;
    }

    @Override
    public Lookup getLookup() {
        if (lkp == null) {
            lkp = Lookups.fixed(new Object[]{
                new PythonInfo(),
                new PythonProjectLogicalView(this)
//                , new CustomizerProvider() {
//                        @Override
//                        public void showCustomizer() {
//                            JOptionPane.showMessageDialog(
//                                    null,
//                                    "customizer for " +
//                                    getProjectDirectory().getName());
//                        }
//                }
            });
        }
        return lkp;
    }
    
 

   private final class PythonInfo implements  ProjectInformation {

//@StaticResource()
//    public static final String PYTHON_ICON = "org/apache/netbeans/modules/python4nb/editor/py.png";

    @Override
    public Icon getIcon() {
        return new ImageIcon(ImageUtilities.loadImage(PYTHON_PROJECT_ICON));
//        return new ImageIcon(ImageUtilities.loadImage(PYTHON_ICON));
    }

    @Override
    public String getName() {
        return getProjectDirectory().getName();
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        //do nothing, won't change
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        //do nothing, won't change
    }

    @Override
    public Project getProject() {
        return PythonProject.this;
    }
    }
   
   class PythonProjectLogicalView implements  LogicalViewProvider {

//    @StaticResource()
//    public static final String PYTHON_ICON = "org/apache/netbeans/modules/python4nb/editor/py.png";
//
//        @StaticResource()
//    public static final String PYTHON_PROJECT_ICON = "org/apache/netbeans/modules/python4nb/editor/py_module.png";

    private final PythonProject project;

    public PythonProjectLogicalView(PythonProject project) {
        this.project = project;
    }

    @Override
    public Node createLogicalView() {
        try {
            //Obtain the project directory's node:
            FileObject projectDirectory = project.getProjectDirectory();
            DataFolder projectFolder = DataFolder.findFolder(projectDirectory);
            Node nodeOfProjectFolder = projectFolder.getNodeDelegate();
            //Decorate the project directory's node:
            return new ProjectNode(nodeOfProjectFolder, project);
        } catch (DataObjectNotFoundException donfe) {
            Exceptions.printStackTrace(donfe);
            //Fallback-the directory couldn't be created -
            //read-only filesystem or something evil happened
            return new AbstractNode(Children.LEAF);
        }
    }

    private final class ProjectNode extends FilterNode {

        final PythonProject project;

        public ProjectNode(Node node, PythonProject project)
            throws DataObjectNotFoundException {
            super(node,
                    new FilterNode.Children(node),
                    new ProxyLookup(
                    new Lookup[]{
                        Lookups.singleton(project),
                        node.getLookup()
                    }));
            this.project = project;
        }

        @Override
        public Action[] getActions(boolean arg0) {
            return new Action[]{
                        CommonProjectActions.newFileAction(),
                        CommonProjectActions.copyProjectAction(),
                        CommonProjectActions.deleteProjectAction(),
                        CommonProjectActions.customizeProjectAction(),
                        CommonProjectActions.closeProjectAction()
                        // TODO Establish applicable Python Project Actions
                        // , CommonProjectActions.TODO (see auto completion)
                    };
        }

        @Override
        public Image getIcon(int type) {
//            return ImageUtilities.loadImage(PYTHON_ICON);
            return ImageUtilities.loadImage(PYTHON_PROJECT_ICON);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public String getDisplayName() {
            return project.getProjectDirectory().getName();
        }
    }

    @Override
    public Node findPath(Node root, Object target) {
        //leave unimplemented for now
        return null;
    }

}
}
