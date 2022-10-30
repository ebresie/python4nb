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
package org.apache.netbeans.modules.python4nb.ui.library;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import org.apache.netbeans.modules.python4nb.editor.file.PythonPackage;
//import org.apache.netbeans.modules.python4nb.ui.Bundle;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
//import org.netbeans.modules.javascript.nodejs.file.PythonPackage;
//import org.netbeans.modules.javascript.nodejs.ui.libraries.LibraryCustomizer;
import org.netbeans.spi.project.ui.CustomizerProvider2;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

public final class PythonLibraries {

    private PythonLibraries() {
    }

    @NodeFactory.Registration(projectType = "org-netbeans-modules-python4nb-project", position = 600)
    public static NodeFactory forPythonProject() {
        return new PipLibrariesNodeFactory();
    }

//    @NodeFactory.Registration(projectType = "org-netbeans-modules-php-project", position = 400)
//    public static NodeFactory forPhpProject() {
//        return new PipLibrariesNodeFactory();
//    }
//
//    @NodeFactory.Registration(projectType = "org-netbeans-modules-web-project", position = 310)
//    public static NodeFactory forWebProject() {
//        return new PipLibrariesNodeFactory();
//    }
//
//    @NodeFactory.Registration(projectType = "org-netbeans-modules-maven", position = 610)
//    public static NodeFactory forMavenProject() {
//        return new PipLibrariesNodeFactory();
//    }

    //~ Inner classes

    private static final class PipLibrariesNodeFactory implements NodeFactory {

        @Override
        public NodeList<?> createNodes(Project project) {
            if (project == null) {
                throw new IllegalArgumentException("createNodes: Invalid project: " + project);
            }
  
            return new PipLibrariesNodeList(project);
        }

    }

    private static final class PipLibrariesNodeList implements NodeList<Node>, PropertyChangeListener {

        private final Project project;
        // TODO: Determine if need something equivalent for python
//        private final PythonPackage pythonJson;
        private final PythonPackage pythonPackage;
        private final PipLibrariesChildren pipLibrariesChildren;
        private final ChangeSupport changeSupport = new ChangeSupport(this);

        // @GuardedBy("thread")
        private Node pipLibrariesNode;


        PipLibrariesNodeList(Project project) {
            assert project != null;
            this.project = project;
            pythonPackage = new PythonPackage(project.getProjectDirectory());
            pipLibrariesChildren = new PipLibrariesChildren(pythonPackage);
        }

        @Override
        public List<Node> keys() {
            if (!pipLibrariesChildren.hasDependencies()) {
                return Collections.<Node>emptyList();
            }
            if (pipLibrariesNode == null) {
                pipLibrariesNode = new PipLibrariesNode(project, pipLibrariesChildren);
            }
            return Collections.<Node>singletonList(pipLibrariesNode);
        }

        @Override
        public void addChangeListener(ChangeListener listener) {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener) {
            changeSupport.removeChangeListener(listener);
        }

        @Override
        public Node node(Node key) {
            return key;
        }

        @Override
        public void addNotify() {
//            pythonPackage.addPropertyChangeListener(WeakListeners.propertyChange(this, pythonPackage));
        }

        @Override
        public void removeNotify() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();
            if (PythonPackage.PROP_DEPENDENCIES.equals(propertyName)
                    || PythonPackage.PROP_DEV_DEPENDENCIES.equals(propertyName)
                    || PythonPackage.PROP_PEER_DEPENDENCIES.equals(propertyName)
                    || PythonPackage.PROP_OPTIONAL_DEPENDENCIES.equals(propertyName)) {
                fireChange();
            }
        }

        private void fireChange() {
            pipLibrariesChildren.refreshDependencies();
            changeSupport.fireChange();
        }

    }

    private static final class PipLibrariesNode extends AbstractNode {

        @StaticResource
        private static final String LIBRARIES_BADGE = "org/apache/netbeans/modules/python4nb/editor/py_module.png"; // NOI18N

        private final Project project;
        private final Node iconDelegate;


        PipLibrariesNode(Project project, PipLibrariesChildren pipLibrariesChildren) {
            super(pipLibrariesChildren);
            assert project != null;
            this.project = project;
            iconDelegate = DataFolder.findFolder(FileUtil.getConfigRoot()).getNodeDelegate();
        }

        @NbBundle.Messages("PipLibrariesNode.name=pip Libraries")
        @Override
        public String getDisplayName() {
            return Bundle.PipLibrariesNode_name();
        }

        @Override
        public Image getIcon(int type) {
            return ImageUtilities.mergeImages(iconDelegate.getIcon(type), ImageUtilities.loadImage(LIBRARIES_BADGE), 7, 7);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[] {
                new CustomizeLibrariesAction(project),
            };
        }

    }

    private static final class PipLibrariesChildren extends Children.Keys<PipLibraryInfo> {

        @StaticResource
        private static final String LIBRARIES_ICON = "org/apache/netbeans/modules/python4nb/editor/py_module.png"; // libraries.gif // NOI18N
        @StaticResource
        private static final String DEV_BADGE = "org/apache/netbeans/modules/python4nb/editor/py_module.png"; // libraries-dev-badge.gif"; // NOI18N
        @StaticResource
        private static final String PEER_BADGE = "org/apache/netbeans/modules/python4nb/editor/py_module.png"; // libraries-peer-badge.png"; // NOI18N
        @StaticResource
        private static final String OPTIONAL_BADGE = "org/apache/netbeans/modules/python4nb/editor/py_module.png"; // libraries-optional-badge.png"; // NOI18N

//        @StaticResource
//        private static final String LIBRARIES_ICON = "org/netbeans/modules/javascript/nodejs/ui/resources/libraries.gif"; // NOI18N
//        @StaticResource
//        private static final String DEV_BADGE = "org/netbeans/modules/javascript/nodejs/ui/resources/libraries-dev-badge.gif"; // NOI18N
//        @StaticResource
//        private static final String PEER_BADGE = "org/netbeans/modules/javascript/nodejs/ui/resources/libraries-peer-badge.png"; // NOI18N
//        @StaticResource
//        private static final String OPTIONAL_BADGE = "org/netbeans/modules/javascript/nodejs/ui/resources/libraries-optional-badge.png"; // NOI18N

        private final PythonPackage pythonPackage;
        private final java.util.Map<String, Image> icons = new HashMap<>();


        public PipLibrariesChildren(PythonPackage pythonPackage) {
            super(true);
            if (pythonPackage == null) {
                throw new IllegalArgumentException(
                        "PipLibrariesChildren: Invalid pythonPackage: " 
                        + pythonPackage);
            };
            this.pythonPackage = pythonPackage;
        }

        public boolean hasDependencies() {
//            return !pythonPackage.getDependencies().isEmpty();
            return !pythonPackage.exists(); //.getDependencies().isEmpty();
        }

        public void refreshDependencies() {
            setKeys();
        }

        @Override
        protected Node[] createNodes(PipLibraryInfo key) {
            return new Node[] {new PipLibraryNode(key)};
        }

        @NbBundle.Messages({
            "PipLibrariesChildren.library.dev=dev",
            "PipLibrariesChildren.library.optional=optional",
            "PipLibrariesChildren.library.peer=peer",
        })
        @Override
        protected void addNotify() {
            setKeys();
        }

        @Override
        protected void removeNotify() {
            setKeys(Collections.<PipLibraryInfo>emptyList());
        }


        private void setKeys() {
//            PythonPackage.PipDependencies dependencies = pythonPackage.getDependencies();
//            if (dependencies.isEmpty()) {
//                setKeys(Collections.<PipLibraryInfo>emptyList());
//                return;
//            }
//            List<PipLibraryInfo> keys = new ArrayList<>(dependencies.getCount());
//            keys.addAll(getKeys(dependencies.dependencies, null, null));
//            keys.addAll(getKeys(dependencies.devDependencies, DEV_BADGE, Bundle.PipLibrariesChildren_library_dev()));
//            keys.addAll(getKeys(dependencies.optionalDependencies, OPTIONAL_BADGE, Bundle.PipLibrariesChildren_library_optional()));
//            keys.addAll(getKeys(dependencies.peerDependencies, PEER_BADGE, Bundle.PipLibrariesChildren_library_peer()));
//            setKeys(keys);
        }

        @NbBundle.Messages({
            "# {0} - library name",
            "# {1} - library version",
            "PipLibrariesChildren.description.short={0}: {1}",
            "# {0} - library name",
            "# {1} - library version",
            "# {2} - library type",
            "PipLibrariesChildren.description.long={0}: {1} ({2})",
        })
        private List<PipLibraryInfo> getKeys(java.util.Map<String, String> dependencies, String badge, String libraryType) {
            if (dependencies.isEmpty()) {
                return Collections.emptyList();
            }
            List<PipLibraryInfo> keys = new ArrayList<>(dependencies.size());
            for (java.util.Map.Entry<String, String> entry : dependencies.entrySet()) {
                String description;
                if (libraryType != null) {
                    description = Bundle.PipLibrariesChildren_description_long(entry.getKey(), entry.getValue(), libraryType);
                } else {
                    description = Bundle.PipLibrariesChildren_description_short(entry.getKey(), entry.getValue());
                }
                keys.add(new PipLibraryInfo(geIcon(badge), entry.getKey(), description));
            }
            Collections.sort(keys);
            return keys;
        }

        private Image geIcon(String badge) {
            Image icon = icons.get(badge);
            if (icon == null) {
                icon = ImageUtilities.loadImage(LIBRARIES_ICON);
                if (badge != null) {
                    icon = ImageUtilities.mergeImages(icon, ImageUtilities.loadImage(badge), 8, 8);
                }
                icons.put(badge, icon);
            }
            return icon;
        }

    }

    private static final class PipLibraryNode extends AbstractNode {

        private final PipLibraryInfo libraryInfo;


        PipLibraryNode(PipLibraryInfo libraryInfo) {
            super(Children.LEAF);
            this.libraryInfo = libraryInfo;
        }

        @Override
        public String getName() {
            return libraryInfo.name;
        }

        @Override
        public String getShortDescription() {
            return libraryInfo.description;
        }

        @Override
        public Image getIcon(int type) {
            return libraryInfo.icon;
        }

        @Override
        public Image getOpenedIcon(int type) {
            return libraryInfo.icon;
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[0];
        }

    }

    private static final class PipLibraryInfo implements Comparable<PipLibraryInfo> {

        final Image icon;
        final String name;
        final String description;


        PipLibraryInfo(Image icon, String name, String descrition) {
            assert icon != null;
            assert name != null;
            assert descrition != null;
            this.icon = icon;
            this.name = name;
            this.description = descrition;
        }

        @Override
        public int compareTo(PipLibraryInfo other) {
            return name.compareToIgnoreCase(other.name);
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 13 * hash + Objects.hashCode(this.name);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final PipLibraryInfo other = (PipLibraryInfo) obj;
            return name.equalsIgnoreCase(other.name);
        }

    }

    private static final class CustomizeLibrariesAction extends AbstractAction {

        private final Project project;


        @NbBundle.Messages("CustomizeLibrariesAction.name=Properties")
        CustomizeLibrariesAction(Project project) {
            assert project != null;

            this.project = project;

            String name = Bundle.CustomizeLibrariesAction_name();
            putValue(NAME, name);
            putValue(SHORT_DESCRIPTION, name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            project.getLookup().lookup(CustomizerProvider2.class).showCustomizer("PythonModuleLibrary", null);
        }

    }

}
