package org.apache.netbeans.modules.python4nb.exec;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

// TODO: Based on nbPython code


//import <any?>;
import org.apache.netbeans.modules.python4nb.platform.PythonPlatform;
import java.io.File;
import java.util.Properties;
import java.util.concurrent.Future;
import org.netbeans.junit.NbTestCase;
import org.openide.util.io.ReaderInputStream;

public class PythonExecutionTest extends NbTestCase{

    public PythonExecutionTest(String name) {
        super(name);
    }
//    public void testJythonExecution() throws Exception{
//
//    }
    
    /**
     * This test checks on "python environment" setup capabilities.
     * 
     * If setup is present it should be usable in subsequent testing.  If not
     * then many of the subsequent tests will fail.
     * 
     * Expect to account for following cases:
     * (1) No python installed - notification of python installation or platform should show
     * (2) Python available on the path -> leverage this
     * (3) Python not available on path -> need to discover, if not available then see (1)
     * 
     * @throws Exception 
     */
    public void testPythonExecutionSetup() throws Exception {
        //TODO: FIx this test

             // * Expect to account for following cases:
     // * (1) No python installed - notification of python installation or platform should show
     // * (2) Python available on the path -> leverage this
     //* (3) Python not available on path -> need to discover, if not available then see (1)

        
        System.out.println("Run python test");

        // TODO: FIX THIS
//        PythonExecution pyexec = new PythonExecution();
//        assertTrue("Test Failed: No Python Executable is available",
//                (pyexec != null && 
//                        (pyexec.getCommand() != null )));
    }
    public void testPythonExecution() throws Exception{
        //TODO: FIx this test
        
        System.out.println("Run python test");
//        PythonExecution pyexec = new PythonExecution();
//        assertTrue("Test Failed: No Python Executable is available", 
//                (pyexec != null && pyexec.getCommand() != null));
//        pyexec.setDisplayName("Python Console Test");
//        pyexec.setWorkingDirectory(getDataSourceDir().getAbsolutePath());
//        pyexec.setCommand("/usr/bin/python2");
//        pyexec.setScript(getTestFile("hello.py").getAbsolutePath());            
//        pyexec.setCommandArgs("-u");
//        Future<Integer> result  =  pyexec.run();
//        
//        assertEquals(0, result.get().intValue());
    }
    
    public void testPythonWriter() throws Exception{
        // TODO: FIX THIS
//        String command = "/usr/bin/python2";
//        PythonPlatform platform = new PythonPlatform("testid"); 
//        PythonExecution pye = new PythonExecution();
//        pye.setCommand(command);
//        pye.setDisplayName("Python Properties");
//        File info = getTestFile("platform_info.py");
//             
//        pye.setScript(info.getAbsolutePath());
//        pye.setShowControls(false);
//        pye.setShowInput(false);
//        pye.setShowWindow(false);
//        pye.setShowProgress(false);
//        pye.setShowSuspended(false);
//        pye.setWorkingDirectory(info.getAbsolutePath().substring(0, info.getAbsolutePath().lastIndexOf(File.separator)));
//        pye.attachOutputProcessor();
//        Future<Integer> result  =  pye.run();
//        
//        assertEquals(0, result.get().intValue());
//        //pye.waitFor();
//        Properties prop = new Properties();
//        prop.load(new ReaderInputStream(pye.getOutput()));
//        platform.setInterpreterCommand(prop.getProperty("python.command"));
//        platform.setName(prop.getProperty("platform.name"));
//
//        assertEquals(command, platform.getInterpreterCommand());

    }
    public void testPythonPath() throws Exception{
        // TODO FIX THIS
//        String command = "/usr/bin/python2";
//        PythonPlatform platform = new PythonPlatform("testid");
//        PythonExecution pye = new PythonExecution();
//        pye.setCommand(command);
//        pye.setDisplayName("Python Properties");
//        File info = getTestFile("platform_info.py");
//
//        pye.setScript(info.getAbsolutePath());
//        pye.setShowControls(false);
//        pye.setShowInput(false);
//        pye.setShowWindow(false);
//        pye.setShowProgress(false);
//        pye.setShowSuspended(false);
//        pye.setWorkingDirectory(info.getAbsolutePath().substring(0, info.getAbsolutePath().lastIndexOf(File.separator)));
//        pye.attachOutputProcessor();
//        Future<Integer> result  =  pye.run();
//
//        assertEquals(0, result.get().intValue());
//        //pye.waitFor();
//        Properties prop = new Properties();
//        prop.load(new ReaderInputStream(pye.getOutput()));
//        platform.setInterpreterCommand(prop.getProperty("python.command"));
//        platform.setName(prop.getProperty("platform.name"));
//        platform.addPythonPath(prop.getProperty("python.path").split(File.pathSeparator));
//
//        assertEquals(command, platform.getInterpreterCommand());

    }

     protected File getTestFile(String relFilePath) {
        File wholeInputFile = new File(getDataSourceDir(), relFilePath);
        if (!wholeInputFile.exists()) {
            NbTestCase.fail("File " + wholeInputFile + " not found.");
        }
        return wholeInputFile;
    }
      protected File getDataSourceDir() {
        // Check whether token dump file exists
        // Try to remove "/build/" from the dump file name if it exists.
        // Otherwise give a warning.
        
        /* TODO: Determine how to setup test file which below uses target/data 
        created during build time */
        File inputFile = getDataDir();
        String inputFilePath = inputFile.getAbsolutePath();
        boolean replaced = false;
        if (inputFilePath.contains(pathJoin("build", "test"))) {
            inputFilePath = inputFilePath.replace(pathJoin("build", "test"), pathJoin("test"));
            replaced = true;
        }
        if (!replaced && inputFilePath.contains(pathJoin("test", "work", "sys"))) {
            inputFilePath = inputFilePath.replace(pathJoin("test", "work", "sys"), pathJoin("test", "unit"));
            replaced = true;
        }
        if (!replaced) {
            System.err.println("Warning: Attempt to use dump file " +
                    "from sources instead of the generated test files failed.\n" +
                    "Patterns '/build/test/' or '/test/work/sys/' not found in " + inputFilePath
            );
        }
        inputFile = new File(inputFilePath);
        assertTrue(inputFile.exists());

        return inputFile;
    }
      private static String pathJoin(String... chunks) {
        StringBuilder result = new StringBuilder(File.separator);
        for (String chunk : chunks) {
            result.append(chunk).append(File.separatorChar);
        }
        return result.toString();
    }
}
