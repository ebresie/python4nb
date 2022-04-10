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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.lsp.Command;
import org.netbeans.spi.project.ActionProvider;
import org.apache.netbeans.modules.python4nb.ui.actions.RunCommand;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;

/**
 *
 * TODO: Based on nbPython code
 */
public class PythonActionProvider implements ActionProvider {
    PythonProject project;
    
    private final Map<String,Command> commands;

    public PythonActionProvider(PythonProject project) {
        assert project != null;
        commands = new LinkedHashMap<>();
        Command[] commandArray = new Command[] {
//            new DeleteCommand(project),
//            new CopyCommand(project),
//            new MoveCommand(project),
//            new RenameCommand(project),
//            new CleanCommand(project),
//            new RunSingleCommand(project, false),
//            new RunSingleCommand(project, true), // Run as Test
            new RunCommand(project, false),
            new RunCommand(project, true), // Run project as Test
//            new DebugCommand(project) ,
//            new DebugSingleCommand(project, false),
//            new DebugSingleCommand(project, true), // Debug as Test
//            new BuildCommand(project), //Build Egg
//            new CleanBuildCommand(project) //Clean and Build Egg
        };
        for (Command command : commandArray) {
//            commands.put(command.getCommandId(), command);
            commands.put(command.getTitle(), command);
        }
    }

//    public static TestRunner getTestRunner(TestRunner.TestType testType) {
//        Collection<? extends TestRunner> testRunners = Lookup.getDefault().lookupAll(TestRunner.class);
//        for (TestRunner each : testRunners) {
//            if (each.supports(testType)) {
//                return each;
//            }
//        }
//        return null;
//    }

    @Override
    public String[] getSupportedActions() {
        final Set<String> names = commands.keySet();
        return names.toArray(new String[names.size()]);
    }

    @Override
    public void invokeAction(final String commandName, final Lookup context) throws IllegalArgumentException {
        final Command command = findCommand(commandName);
        assert command != null;
        if (command.saveRequired()) {
            LifecycleManager.getDefault().saveAll();
        }
        if (!command.asyncCallRequired()) {
            command.invokeAction(context);
        } else {
            RequestProcessor.getDefault().post(new Runnable() {
                @Override
                public void run() {
                    command.invokeAction(context);
                }
            });
        }
    }

    @Override
    public boolean isActionEnabled(String commandName, Lookup context) throws IllegalArgumentException {
        final Command command = findCommand (commandName);
        assert command != null;
        return command.isActionEnabled(context);
    }
    
    private Command findCommand (final String commandName) {
        assert commandName != null;
        return commands.get(commandName);
    }
}