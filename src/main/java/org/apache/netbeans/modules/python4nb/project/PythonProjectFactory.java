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

import java.io.IOException;
import org.apache.netbeans.modules.python4nb.editor.file.MIMETypes;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;
/**
 *
 * @author ebres
 */
/* TODO: Investigate if ProjectFactory2 usage may be preferred which may be better
for multiple project and performance reasons.. 

See https://netbeans.apache.org/tutorials/nbm-projecttype.html */

@ServiceProvider(service=ProjectFactory.class)
public class PythonProjectFactory implements  ProjectFactory {

    /** Checks if project folder is a python project (i.e. if ".py" files are 
     * present in folder) */
    @Override
    public boolean isProject(FileObject projectDirectory) {
        // search if there are any 
        FileObject[] childen = projectDirectory.getChildren();
        boolean isPythonProject = false;
        for (FileObject file: childen) {
            if (file.existsExt(MIMETypes.PY_EXT) || 
                    file.existsExt(MIMETypes.PYC_EXT) || 
                    file.existsExt(MIMETypes.PYO_EXT) 
                    ) {
                isPythonProject = true;
            }
        }
        return isPythonProject;
    }

    //Specifies when the project will be opened, i.e., if the project exists:*
    @Override
    public Project loadProject(FileObject dir,  ProjectState state) throws IOException {
        return isProject(dir) ? new PythonProject(dir, state) : null;
    }

    @Override
    public void saveProject(final Project project) throws IOException, ClassCastException {
        PythonProject pyProject = (PythonProject)project;
        PythonProjectProperties properties = pyProject.getProperties();
        properties.save();
    }

}
