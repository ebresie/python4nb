/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/* TODO: This is temporart file for refernece based on NBPython Code.  
Need to review and recreate were applicable. */
package org.apache.netbeans.modules.python4nb.exec;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
//import java.io.StringReader;
import java.io.Writer;
//import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.apache.netbeans.modules.python4nb.api.PythonOutputProcessor;
import org.netbeans.api.extexecution.ExecutionDescriptor;
//import org.netbeans.api.extexecution.ExecutionDescriptor.InputProcessorFactory;
import org.netbeans.api.extexecution.ExecutionDescriptor.InputProcessorFactory2;
import org.netbeans.api.extexecution.ExecutionDescriptor.LineConvertorFactory;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.base.Environment;
//import org.netbeans.api.extexecution.ExternalProcessBuilder;  // Use java ProcessBuilder
//import org.netbeans.api.extexecution.ProcessBuilder; // depreicated use .base.ProcessBuilder
import org.netbeans.api.extexecution.base.ProcessBuilder;
//import org.netbeans.api.extexecution.input.InputProcessor;
import org.netbeans.api.extexecution.base.input.InputProcessor;
import org.netbeans.api.extexecution.print.LineConvertor;
import org.netbeans.api.extexecution.print.LineConvertors.FileLocator;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.windows.InputOutput;

public final class PythonExecution {
    // execution commands
    private String command;
    private String workingDirectory;
    private String commandArgs;
    private String[] cmdArgs;
    private String path;
    private String javapath;
    private String script;
    private String scriptArgs;
    private String[] scptArgs;
    private String displayName;    
    private boolean redirect;
    private String wrapperCommand;
    private String[] wrapperArgs;
    private String[] wrapperEnv;
    private List<LineConvertor> outConvertors = new ArrayList<>();
    private List<LineConvertor> errConvertors = new ArrayList<>();
    private InputProcessorFactory2 outProcessorFactory;
    private InputProcessorFactory2 errProcessorFactory;
    private boolean addStandardConvertors;
    private FileLocator fileLocator;
    private boolean lineBased;
    private Runnable postExecutionHook;
    
    public PythonExecution() {

    }

    public PythonExecution(PythonExecution from) {
        command = from.command;
        workingDirectory = from.workingDirectory;
        commandArgs = from.commandArgs;
        cmdArgs = from.cmdArgs;
        path = from.path;
        javapath = from.javapath;
        script = from.script;
        scriptArgs = from.scriptArgs;
        scptArgs = from.scptArgs;
        displayName = from.displayName;
        redirect = from.redirect;
        wrapperCommand = from.wrapperCommand;
        if (from.wrapperArgs != null) {
            wrapperArgs = new String[from.wrapperArgs.length];
            System.arraycopy(from.wrapperArgs, 0, wrapperArgs, 0, from.wrapperArgs.length);
        }
        if (from.wrapperEnv != null) {
            wrapperEnv = new String[from.wrapperEnv.length];
            System.arraycopy(from.wrapperEnv, 0, wrapperEnv, 0, from.wrapperEnv.length);
        }
        fileLocator = from.fileLocator;
        outConvertors = new ArrayList<>(from.outConvertors);
        errConvertors = new ArrayList<>(from.errConvertors);
        setOutProcessorFactory(from.outProcessorFactory);
        setErrProcessorFactory(from.errProcessorFactory);
        lineBased(from.lineBased);
        if (from.addStandardConvertors) {
            addStandardRecognizers();
        }
        postExecutionHook = from.postExecutionHook;
    }

    /* 
    
    Sample code from 
    http://bits.netbeans.org/dev/javadoc/org-netbeans-modules-extexecution/org/netbeans/api/extexecution/ExecutionService.html
    
     ExecutionDescriptor descriptor = new ExecutionDescriptor().frontWindow(true).controllable(true);

     ExternalProcessBuilder processBuilder = new ExternalProcessBuilder("ls");

     ExecutionService service = ExecutionService.newService(processBuilder, descriptor, "ls command");
     Future<Integer> task = service.run();
     */
    public ExecutionDescriptor toExecutionDescriptor() {
        return descriptor;
    }

    //internal process control    
    private ExecutionDescriptor descriptor = new ExecutionDescriptor()
            .frontWindow(true).controllable(true).inputVisible(true)
                .showProgress(true).showSuspended(true);
    
    //private InputOutput io;

    /**
     * Execute the process described by this object
     * @return a Future object that provides the status of the running process
     */
    public synchronized Future<Integer> run(){
        try {
            // Setup process Information
            
            
            // Setup Descriptor Information
            descriptor = buildDescriptor();
            String encoding = null;
            if (script != null) {
                File scriptFile = new File(script);
                FileObject scriptFileObject = FileUtil.toFileObject(scriptFile);
                
                // TODO: Need to evaluate Encoding within Python scripts
//                PythonFileEncodingQuery encodingQuery = new PythonFileEncodingQuery();
//                encoding = encodingQuery.getPythonFileEncoding(scriptFileObject.getInputStream());
//                if (encoding != null) {
//                    descriptor = descriptor.charset(Charset.forName(encoding));                    
//                }
            }
            // TODO: Figure out how to handle "WorkingDirectory"
            // setup process build with command, arguments, and wrappers details
            ProcessBuilder pb = buildProcess(encoding);
//            Process process = pb.call();
//            Process process = pb.start();
            //build Service
            ExecutionService service = 
                    ExecutionService.newService(
                            pb,
                    descriptor, displayName);
//            ExecutionService service = 
//                    ExecutionService.newService(
//                            (Callable<Process>) process,
//                    descriptor, displayName);
            //io = descriptor.getInputOutput();
            // Start Service
           return service.run();
            //io = InputOutputManager.getInputOutput(displayName, true, path).getInputOutput();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
        
    }

/* TODO: Update to non-depriated interfaces.  
    See https://bits.netbeans.org/9.0/javadoc/org-netbeans-modules-extexecution/deprecated-list.html#class */
    public ProcessBuilder buildProcess(String encoding) throws IOException{
//    public ExternalProcessBuilder buildProcess(String encoding) throws IOException{
//        ExternalProcessBuilder processBuilder =
//                    new ExternalProcessBuilder(command);
    
        ProcessBuilder processBuilder = ProcessBuilder.getLocal();
        processBuilder.setExecutable(command);
        
        List<String> cmdList = getArguments();

        processBuilder.setArguments(cmdList);
        processBuilder.setWorkingDirectory(workingDirectory);
//        processBuilder.setRedirectErrorStream(redirect);
        processBuilder.setRedirectErrorStream(true); // TODO Determine if should/shouldn't redirect error to output

        setEnvironmentVariables(processBuilder, encoding);

        /* from server startup code
            ProcessBuilder pythonServerBuilder = new ProcessBuilder(python, "-m", "pyls");
        
            Process pythonServerProcess = pythonServerBuilder.redirectError(ProcessBuilder.Redirect.INHERIT).start();
            // TODO: If unable to start pyls support need to error out and/or notify user for setup
        
        InputStream inputStream = pythonServerProcess.getInputStream();
            OutputStream outputStream = pythonServerProcess.getOutputStream();
            LanguageServerDescription lspDescription
                    = LanguageServerDescription.create(inputStream, outputStream, pythonServerProcess);
            return lspDescription;
        */
        return processBuilder;
    }

    private void setEnvironmentVariables(ProcessBuilder processBuilder, String encoding) {
        //        ProcessBuilder processBuilder
//                = new ProcessBuilder(cmd)
//                        .directory(new File(workingDirectory))
//                        .redirectInput(ProcessBuilder.Redirect.INHERIT)
//                        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
//                        .redirectErrorStream(redirect);

//        Map<String, String> envVarables = processBuilder.environment();
Environment envVarables = processBuilder.getEnvironment();

if (wrapperEnv != null && wrapperEnv.length > 0) {
    for (String env : wrapperEnv) {
        int index = env.indexOf('=');
        assert index != -1;
//                envVarables.put(env.substring(0, index), env.substring(index + 1));
envVarables.setVariable(env.substring(0, index), env.substring(index + 1));
//                        processBuilder = processBuilder.addEnvironmentVariable(env.substring(0, index), env.substring(index+1));
    }
}

if (path != null) {
    if (encoding != null) {
//                envVarables.put("PYTHONIOENCODING", encoding);
envVarables.setVariable("PYTHONIOENCODING", encoding);
//
//                    processBuilder = 
//                            processBuilder.addEnvironmentVariable("PYTHONIOENCODING", encoding); // NOI18N
    }
    if (command.toLowerCase().contains("jython")) {
//                    String commandPath = "-Dpython.path=" + path;
//                    processBuilder = processBuilder.addArgument(commandPath);

//                     processBuilder =
//                            processBuilder.addEnvironmentVariable("JYTHONPATH", path);
//                envVarables.put("JYTHONPATH", path);
envVarables.setVariable("JYTHONPATH", path);
if (javapath != null) {
//                        processBuilder =
//                               processBuilder.addEnvironmentVariable("CLASSPATH", javapath);
//                    envVarables.put("CLASSPATH", javapath);
envVarables.setVariable("CLASSPATH", javapath);
}
    } else {
//                    processBuilder =
//                            processBuilder.addEnvironmentVariable("PYTHONPATH", path);
//                envVarables.put("PYTHONPATH", path);
envVarables.setVariable("PYTHONPATH", path);
    }
}
    }

    private List<String> getArguments() {
        //        List<String> cmdList = new ArrayList<String>();
        List<String> cmdList = new ArrayList<>();
//        cmdList.add(command);
if ((commandArgs != null) && (commandArgs.trim().length() > 0)) {
//               processBuilder = processBuilder.addArgument(commandArgs);

// TODO: May need to break up command argument string into individual tokens
cmdList.add(commandArgs);
//               processBuilder = new ProcessBuilder(command, commandArgs );
//            processBuilder.addArgument(commandArgs);
//            processBuilder.setArguments(cmdList);
}
if (wrapperCommand != null) {
    cmdList.add(wrapperCommand);
//                processBuilder = processBuilder.addArgument(wrapperCommand);
if (wrapperArgs != null && wrapperArgs.length > 0) {
    cmdList.addAll(Arrays.asList(wrapperArgs));
//                        processBuilder = processBuilder.addArgument(arg);
}
//                if (wrapperEnv != null && wrapperEnv.length > 0) {
//                    for (String env : wrapperEnv) {
//                        int index = env.indexOf('=');
//                        assert index != -1;
//                        processBuilder = processBuilder.addEnvironmentVariable(env.substring(0, index), env.substring(index+1));
//                    }
//                }
}
if (script != null) {
    // processBuilder = processBuilder.addArgument(script);
    cmdList.add(script);
}
if (scriptArgs != null) {
    // a natural python tuple on python side
    String args[] = org.openide.util.Utilities.parseParameters(scriptArgs);
    cmdList.addAll(Arrays.asList(args));
//                    processBuilder = processBuilder.addArgument(arg);
}
//            processBuilder = processBuilder.redirectErrorStream(redirect);

//            String[] cmd = (String[]) cmdList.toArray();
//        String[] cmd = cmdList.toArray(new String[0]);

        return cmdList;
    }
    
    private ExecutionDescriptor buildDescriptor(){
        
        ExecutionDescriptor descriptor = new ExecutionDescriptor()
                .frontWindow(true)
                .controllable(true);
        // TODO: see if input/output works 
//        descriptor.inputOutput(InputOutput.NULL); 

        return descriptor;
    }
    
    public synchronized String getCommand() {
        return command;
    }

    public synchronized void setCommand(String command) {
        this.command = command;
    }

    public synchronized String getCommandArgs() {
        return commandArgs;
    }

    public synchronized void setCommandArgs(String commandArgs) {
        this.commandArgs = commandArgs;
    }

    public synchronized String getPath() {
        return path;
    }

    public synchronized void setJavaPath(String javapath) {
        this.javapath = javapath;
    }

    public synchronized String getJavaPath() {
        return javapath;
    }

    public synchronized void setPath(String path) {
        this.path = path;
    }

    public synchronized String getScript() {
        return script;
    }

    public synchronized void setScript(String script) {
        this.script = script;
    }

    public synchronized String getScriptArgs() {
        return scriptArgs;
    }

    public synchronized void setScriptArgs(String scriptArgs) {
        this.scriptArgs = scriptArgs;
    }

    public synchronized String getWorkingDirectory() {
        return workingDirectory;
    }

    public synchronized void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public synchronized String getDisplayName() {
        return displayName;
    }

    public synchronized void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public synchronized void setWrapperCommand(String wrapperCommand, String[] wrapperArgs, String[] wrapperEnv) {
        this.wrapperCommand = wrapperCommand;
        this.wrapperArgs = wrapperArgs;
        this.wrapperEnv = wrapperEnv;
    }

    public synchronized void setShowControls(boolean showControls) {
       descriptor = descriptor.controllable(showControls);
    }

    public PythonExecution addOutConvertor(LineConvertor convertor) {
        this.outConvertors.add(convertor);
        descriptor = descriptor.outConvertorFactory(lineConvertorFactory(outConvertors));
        return this;
    }

    public PythonExecution addErrConvertor(LineConvertor convertor) {
        this.errConvertors.add(convertor);
        descriptor = descriptor.errConvertorFactory(lineConvertorFactory(errConvertors));
        return this;
    }

    public synchronized void addStandardRecognizers() {
        this.addStandardConvertors = true;
        descriptor = descriptor.outConvertorFactory(lineConvertorFactory(outConvertors));
        descriptor = descriptor.errConvertorFactory(lineConvertorFactory(errConvertors));
    }

    public void setErrProcessorFactory(InputProcessorFactory2 errProcessorFactory) {
        this.errProcessorFactory = errProcessorFactory;
        descriptor = descriptor.errProcessorFactory(errProcessorFactory);
    }

    public void setOutProcessorFactory(InputProcessorFactory2 outProcessorFactory) {
        this.outProcessorFactory = outProcessorFactory;
        descriptor = descriptor.outProcessorFactory(outProcessorFactory);
    }

    public PythonExecution lineBased(boolean lineBased) {
        this.lineBased = lineBased;
        if (lineBased) {
            descriptor = descriptor.errLineBased(lineBased).outLineBased(lineBased);
        }

        return this;
    }

    /* TODO: Determine how to handle line converter in new code. 
    
    Maybe use https://bits.netbeans.org/9.0/javadoc/org-netbeans-modules-extexecution/org/netbeans/api/extexecution/print/LineProcessors.html 
    */
    private LineConvertorFactory lineConvertorFactory(List<LineConvertor> convertors) {
        
//        LineConvertor[] convertorArray = convertors.toArray(new LineConvertor[convertors.size()]);
//        if (addStandardConvertors) {
//            return PythonLineConvertorFactory.withStandardConvertors(fileLocator, convertorArray);
//        }
//        return PythonLineConvertorFactory.create(fileLocator, convertorArray);
        return null;
    }


    public synchronized void setShowInput(boolean showInput) {
        descriptor = descriptor.inputVisible(showInput);
    }

    public synchronized void setRedirectError(boolean redirect){
        this.redirect = redirect;
    }

    public synchronized void setShowProgress(boolean showProgress) {
        descriptor = descriptor.showProgress(showProgress);
    }
    /**
     * Can the process be suspended
     * @param showSuspended boolean to set the status 
     */
    public synchronized void setShowSuspended(boolean showSuspended) {
        descriptor = descriptor.showSuspended(showSuspended);
    }    
    /**
     * Show the window of the running process
     * @param showWindow display the window or not?
     */
    public synchronized void setShowWindow(boolean showWindow) {
        descriptor = descriptor.frontWindow(showWindow);
    }
    
//    private final PythonOutputProcessor outProcessor = new PythonOutputProcessor();
    private final PythonOutputProcessor outProcessor = new PythonOutputProcessor();
    /**
     * Attach a Processor to collect the output of the running process
     */
    public void attachOutputProcessor(){
        descriptor = descriptor.outProcessorFactory(new ExecutionDescriptor.InputProcessorFactory2() {

            public InputProcessor newInputProcessor() {
                return outProcessor;
//                return null;
            }

            @Override
            public InputProcessor newInputProcessor(InputProcessor defaultProcessor) {
                return outProcessor;
//                return null;
            }
        });
    }

    public void setPostExecutionHook(Runnable runnable) {
        postExecutionHook = runnable;
        descriptor = descriptor.postExecution(runnable);
    }

    public Runnable getPostExecutionHook() {
        return postExecutionHook;
    }

    /**
     * Retive the output form the running process
     * @return a string reader for the process
     */
    public Reader getOutput(){
        return new StringReader(outProcessor.getData());

//        return null;
    }
    /**
     * Attach input processor to the running process
     */
    public void attachInputProcessor(){
        //descriptor = descriptor.
    }
    /**
     * Writes data to the running process
     * @return StringWirter
     */
    public Writer getInput(){
        return null;
    }
}
