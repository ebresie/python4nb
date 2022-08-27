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
package org.apache.netbeans.modules.python4nb.platform;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;

/**
 *
 * @author ebres
 */
@ActionID(id = "org.apache.netbeans.modules.python4nb.platform.PythonManagerAction", category = "Python")
@ActionRegistration(displayName = "Python Platforms", lazy = false)
@ActionReference(path = "Menu/Tools", position = 350)
public class PythonManagerAction extends CallableSystemAction {

    @Override
    public void performAction() {
        PythonPlatformPanel.showPlatformManager();
    }

    @Override
    public String getName() {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        return "Python Platforms";
    }

    @Override
    public HelpCtx getHelpCtx() {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
return null;
    }

    @Override
    public boolean accept(Object sender) {
        return super.accept(sender); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }
    
}
