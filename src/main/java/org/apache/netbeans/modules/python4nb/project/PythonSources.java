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

// Portions of this code are based on nbPython Code. 
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.support.GenericSources;
import org.openide.filesystems.FileObject;

public class PythonSources implements Sources {

    private final PythonProject project;
    private SourceGroup[] roots;

    public PythonSources(PythonProject project) {
        this.project = project;
        this.roots = getSourceGroups(PythonProject.SOURCES_TYPE_PYTHON);
    }

    @Override
    public SourceGroup[] getSourceGroups(String type) {
        synchronized (this) {
            if (roots == null) {
                FileObject fo = project.getProjectDirectory();
                roots = new SourceGroup[]{GenericSources.group(project, fo, fo.getPath(), "Source Packages", null, null)};
            }
        }
        return roots;
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
    }
}
