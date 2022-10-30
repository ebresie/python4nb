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
package org.apache.netbeans.modules.python4nb.project.node;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.apache.netbeans.modules.python4nb.project.PythonProject;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author ebres
 */




@NodeFactory.Registration(
        projectType = "org-apache-netbeans-modules-python4nb-project", 
        position = 10)
public class PythonNodeFactory implements  NodeFactory {

    @Override
    public NodeList createNodes(Project project) {
        PythonProject p = project.getLookup().lookup(PythonProject.class);
        assert p != null;
        return new PythonNodeList(p);
    }

    private class PythonNodeList implements NodeList {

        PythonProject project;

        public PythonNodeList(PythonProject project) {
            this.project = project;
        }

        @Override
        public List keys() {
            FileObject textsFolder =
                project.getProjectDirectory(); //.getFileObject("texts");
            List result = new ArrayList();
            if (textsFolder != null) {
                for (FileObject textsFolderFile : textsFolder.getChildren()) {
                    try {
                        result.add(DataObject.find(textsFolderFile).getNodeDelegate());
                    } catch (DataObjectNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
            return result;
        }

//        @Override
//        public Node node(Node node) {
//            return new FilterNode(node);
//        }

//        @Override
//        public Node node(Object k) {
//            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//        }
        
        @Override
        public void addNotify() {
        }

        @Override
        public void removeNotify() {
        }

        @Override
        public void addChangeListener(ChangeListener cl) {
        }

        @Override
        public void removeChangeListener(ChangeListener cl) {
        }

        @Override
        public Node node(Object key) {
            return new FilterNode((Node)key);

        }


    }

}