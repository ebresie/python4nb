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

import org.apache.netbeans.modules.python4nb.project.PythonProject;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.support.DefaultProjectOperations;

public class CopyCommand extends GlobalCommand {
    public CopyCommand(PythonProject project) {
        super(project);
    }

    @Override
    public String getCommandId() {
        return ActionProvider.COMMAND_COPY;
    }

    @Override
    public void invokeAction()  {
        DefaultProjectOperations.performDefaultCopyOperation(getProject());
    }
}
