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
package org.apache.netbeans.modules.python4nb.project;

import org.apache.netbeans.modules.python4nb.project.ui.PythonCustomizerProvider;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.apache.netbeans.modules.python4nb.platform.PythonSupport;
import org.apache.netbeans.modules.python4nb.project.PythonSources;
import org.apache.netbeans.modules.python4nb.project.ui.MyPythonCustomizerProvider;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.netbeans.spi.project.ui.CustomizerProvider;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
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

// Portions of this code are based on nbPython Code.  

/**
 * This class is to reflect details and applicable attributes 
 * about a given Python Project for use in NetBeans IDE.
 */
public class PythonProject implements  Project {

    public static final String PYTHON_PROJECT_TYPE = "org-apache-netbeans-modules-python4nb-project";
    public static final String SOURCES_TYPE_PYTHON = "python"; //NOI18N
    
    private final FileObject projectDir;
    protected SourceRoots sourceRoots;
    protected SourceRoots testRoots;
    protected Lookup lkp;
    protected PythonProjectProperties properties;
    //TODO Determine if needed for future enhancements; remove if not needed
//    private final ProjectState state;  // XXX May need
//    protected AntProjectHelper helper; // XXX May need some form of Helper
//    protected UpdateHelper updateHelper;
//    protected LogicalViewProvider logicalView = new PythonLogicalView(this);
//    protected PropertyEvaluator evaluator;
//    protected ReferenceHelper refHelper;
//    protected AuxiliaryConfiguration aux;

    
    private static final Logger LOGGER = Logger.getLogger(PythonProject.class.getName());
    
    @StaticResource()
    public static final String PYTHON_PROJECT_ICON = "org/apache/netbeans/modules/python4nb/editor/py_module.png";
    PythonProject(FileObject dir, ProjectState state) {
        this.projectDir = dir;
        this.lkp = getLookup();
        this.sourceRoots = getSourceRoots();
        this.properties = getProperties();
//        this.state = state;
    }
    
     PythonProject(File  dir) {
        this.projectDir = FileUtil.toFileObject(dir);
        this.lkp = getLookup(); //createLookup();    
        this.sourceRoots = getSourceRoots();
//        this.state = ProjectState.state;
    }

    @Override
    public FileObject getProjectDirectory() {
        // TODO: Maybe change to  a debug or applicable log level
        LOGGER.info("PythonProject.projectDir=" + projectDir);
        return projectDir;
    }

    @Override
    public Lookup getLookup() {
        if (lkp == null) {
            lkp = Lookups.fixed(new Object[]{
                this,
                new PythonInfo(),
                new PythonProjectLogicalView(this),
                new PythonActionProvider(this),
                new PythonSources(this),
                new PythonSupport(this),
                new PythonCustomizerProvider(this) //Project custmoizer
//                new MyPythonCustomizerProvider(this)
//TODO Determine if needed for future enhancements; remove if not needed
//                , 
//                new CustomizerProvider() {
//                        @Override
//                        public void showCustomizer() {
//                            JOptionPane.showMessageDialog(
//                                    null,
//                                    "customizer for " +
//                                    getProjectDirectory().getName());
//                        }
//                }
//            new ClassPathProviderImpl(this),
//            new Info(), // Project information Implementation
//            logicalView, // Logical view if project implementation
//            new PythonOpenedHook(), //Called by project framework when project is opened (closed)
//            new PythonProjectXmlSavedHook(), //Called when project.xml changes
//            new PythonSources(this, helper, evaluator, sourceRoots, testRoots), //Python source grops - used by package view, factories, refactoring, ...
//            new PythonProjectOperations(this), //move, rename, copy of project
//            new RecommendedTemplatesImpl(this.updateHelper), // Recommended Templates
//            new PythonCustomizerProvider(this), //Project custmoizer
//            new PythonFileEncodingQuery(),
//            new PythonProjectTemplateAttributesProvider(getEvaluator()),
//            new PythonSharabilityQuery(helper, getEvaluator(), getSourceRoots(), getTestRoots()), //Sharabilit info - used by VCS
//            helper.createCacheDirectoryProvider(), //Cache provider
//            helper.createAuxiliaryProperties(), // AuxiliaryConfiguraion provider - used by bookmarks, project Preferences, etc
//            new PythonPlatformProvider(getEvaluator()),
//            new PythonCoverageProvider(this),
//            new PythonProjectSourceLevelQuery(evaluator, "")
            });
        }
        return lkp;
    }

    public SourceRoots getSourceRoots() {
        // if source root has not been established yet, try to set based on project directory
        if (this.sourceRoots == null) {
            this.sourceRoots = SourceRoots.create(this);
        }
        
        LOGGER.info("PythonProject.SourceRoots=" + this.sourceRoots);

        return this.sourceRoots;
    }

    public SourceRoots getTestRoots() {
        return this.testRoots;
    }

    public FileObject[] getSourceRootFiles() {
        return getSourceRoots().getRoots();
    }

    public FileObject[] getTestSourceRootFiles() {
        return getTestRoots().getRoots();
    }

    public String getName() {
        if (this.properties == null) {
            this.properties = getProperties();
        }
        
        return this.properties.getName();
        
//        PythonInfo pi = lkp.lookup(PythonInfo.class);
//        return pi.getName();
   }
    public String getDescription() {
        if (this.properties == null) {
            this.properties = getProperties();
        }
        return this.properties.getDescription();
        
//        PythonInfo pi = lkp.lookup(PythonInfo.class);
//        return pi.getDisplayName();
   }

    public PythonProjectProperties getProperties() {
        // mcheck if python project properties exist if not then create one
        if (this.properties == null) {
            this.properties = new PythonProjectProperties(this);
            // none set yet, try to find existing properties
//            this.properties = PythonProjectProperties.findProjectPropertiesFile(this);
            
//                // no project properties found, create new one in memory
//                this.properties = new PythonProjectProperties(this);
//                // now save properties for future use
//                this.properties.save();
//            try {
//                this.properties.loadProperties();
//            } catch (IOException ex) {
//                Exceptions.printStackTrace(ex);
//                LOGGER.severe("Error loading PythonProject Properties.");
//
//            }
        }
        return properties;
     }
    
   private final class PythonInfo implements  ProjectInformation {

    @Override
    public Icon getIcon() {
        return new ImageIcon(ImageUtilities.loadImage(PYTHON_PROJECT_ICON));
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
//                    NodeFactorySupport.createCompositeChildren((Project)project, 
//                            PYTHON_PROJECT_NODES),
//                            PROP_NAME),
                    new FilterNode.Children(node),
                    new ProxyLookup(
                    new Lookup[]{
                        Lookups.singleton(project),
                        node.getLookup()
                    }));
            this.project = project;
        }
            public static final String PYTHON_PROJECT_NODES = "Projects/org-apache-netbeans-modules-python4nb-project-node/Nodes";

        @Override
        public Action[] getActions(boolean arg0) {
            return new Action[]{
                        CommonProjectActions.newFileAction(),
                        CommonProjectActions.newProjectAction(),
                        CommonProjectActions.renameProjectAction(),
                        CommonProjectActions.copyProjectAction(),
                        CommonProjectActions.moveProjectAction(),
                        CommonProjectActions.deleteProjectAction(),
                        CommonProjectActions.closeProjectAction(),
                        CommonProjectActions.openSubprojectsAction(),
                        CommonProjectActions.setAsMainProjectAction(),
                        CommonProjectActions.customizeProjectAction(),
                        CommonProjectActions.setProjectConfigurationAction()
                        // TODO Establish applicable Python Project Actions
                        // , CommonProjectActions.TODO (see auto completion)
                    };
        }

        @Override
        public Image getIcon(int type) {
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
