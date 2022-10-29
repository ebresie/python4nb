/*
 * Copyright 2022 Eric Bresie and friends. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this projectPropertiesFile except in compliance with the License.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;
import org.apache.netbeans.modules.python4nb.util.Pair;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.queries.FileEncodingQuery;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Mutex;
import org.openide.util.MutexException;

public class PythonProjectProperties {
    
    public static final String SRC_DIR = "src.dir"; //NOI18N
    public static final String MAIN_FILE = "main.file"; //NOI18N
    public static final String APPLICATION_ARGS = "application.args";   //NOI18N
    public static final String ACTIVE_PLATFORM = "platform.active"; //NOI18N
    public static final String PYTHON_LIB_PATH = "python.lib.path"; //NOI18N
    public static final String JAVA_LIB_PATH = "java.lib.path";     //NOI18N
    public static final String SOURCE_ENCODING = "source.encoding"; //NOI18N
    public static final String PROJECT_NAME = "python.project.name"; //NOI18N
    public static final String PROJECT_DESCRIPTION = "python.project.description"; //NOI18N
   
   public static final String EMPTY_VALUE = ""; 
    //Relative path from project directory to the customary shared properties projectPropertiesFile.
    public static final String PROJECT_PROPERTIES_PATH = "nbproject/project.properties"; // NOI18N
    
    // Relative path from project directory to the customary private properties projectPropertiesFile.
    public static final String PRIVATE_PROPERTIES_PATH = "nbproject/private/private.properties"; // NOI18N    
    private final PythonProject project;
//    private final PropertyEvaluator eval;
    
    private final Properties projectProperties;
            
    private volatile String encoding;
    private volatile List<Pair<File,String>> sourceRoots;
//    private volatile List<Pair<File,String>> testRoots;
    private volatile String mainModule;
    private volatile String appArgs;
    private volatile ArrayList<String>pythonPath;
    private volatile ArrayList<String>javaPath;
    private volatile String activePlatformId;
    private volatile String name;
    private volatile String description;


    private static final Logger LOGGER = Logger.getLogger(PythonProjectProperties.class.getName());
    public static  final String DEFAULT_ENCODING = "utf-8";

    public PythonProjectProperties (final PythonProject project) {
        assert project != null;
        this.project = project;
//        this.eval = project.getEvaluator();
//        Map<> properties = new HashMap();
        this.projectProperties = new Properties();
        try {
            loadProperties();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            LOGGER.severe("Error generating PythonProjectProperties file.");
        }
    }
    
    //Properties
    
    public PythonProject getProject () {
        return this.project;
    }
    
    public FileObject getProjectDirectory () {
        return this.project.getProjectDirectory();
    }
    
    public void setEncoding (final String encoding) {       
        this.encoding = encoding;
    }
    
    public String getEncoding () {
        if (this.encoding == null || this.encoding.equals("")) {
            this.encoding = projectProperties.getProperty(SOURCE_ENCODING);
            // still no encoding to set to default
            if (this.encoding == null || this.encoding.equals("")) {
              this.encoding = DEFAULT_ENCODING;
            }
        }
        return this.encoding;
    }
    
    public List<Pair<File,String>> getSourceRoots () {
        if (sourceRoots == null) {
            final SourceRoots sourceRoots = project.getSourceRoots();
            final String[] rootLabels = sourceRoots.getRootNames();
            final String[] rootProps = sourceRoots.getRootProperties();
            final URL[] rootURLs = sourceRoots.getRootURLs();
            final List<Pair<File,String>> data = new LinkedList<>();
            if (rootURLs==null || rootURLs.length == 0) {
                // no paths identified yet add project path
                 File f = FileUtil.toFile(project.getProjectDirectory());
                 final String s = f.getName();
                data.add(Pair.of(f, s));
            } else {
            for (int i=0; i< rootURLs.length; i++) {                
                final File f  = new File (URI.create (rootURLs[i].toExternalForm()));            
                final String s = sourceRoots.getRootDisplayName(rootLabels[i], rootProps[i]);
                data.add(Pair.of(f, s));
            }
            }
            this.sourceRoots = data;
        }
        return this.sourceRoots;
    }
    
    public void setSourceRoots (final List<Pair<File,String>> sourceRoots) {
        assert sourceRoots != null;
        this.sourceRoots = sourceRoots;
    }
    
    public List<Pair<File,String>> getTestRoots () {
        // TODO: Implement Test functionality
        return null;
//        if (testRoots == null) {
//            final SourceRoots testRoots = project.getTestRoots();
//            final String[] rootLabels = testRoots.getRootNames();
//            final String[] rootProps = testRoots.getRootProperties();
//            final URL[] rootURLs = testRoots.getRootURLs();
//            final List<Pair<File,String>> data = new LinkedList<>();
//            for (int i=0; i< rootURLs.length; i++) {                
//                final File f  = new File (URI.create (rootURLs[i].toExternalForm()));            
//                final String s = testRoots.getRootDisplayName(rootLabels[i], rootProps[i]);
//                data.add(Pair.of(f, s));
//            }
//            this.testRoots = data;
//        }
//        return this.testRoots;
    }
    
    public void setTestRoots (final List<Pair<File,String>> testRoots) {
        assert testRoots != null;
        this.sourceRoots = testRoots;
    }
    
    public String getMainModule () {
        if ((mainModule == null || mainModule.equals(""))
                && projectProperties != null) {
            mainModule = projectProperties.getProperty(MAIN_FILE);
        }
        return mainModule;
    }
    
    public void setMainModule (final String module) {
        this.mainModule = module;
    }
    
    public String getApplicationArgs () {
        if (appArgs == null) {
            appArgs = projectProperties.getProperty(APPLICATION_ARGS);
        }
        return appArgs;
    }
    
    public void setApplicationArgs (final String args) {
        this.appArgs = args;
    }

    public ArrayList<String> getPythonPath() {
        if(pythonPath == null) {
            String buildPath = projectProperties.getProperty(PYTHON_LIB_PATH);
            if (buildPath != null) {
                pythonPath = buildPathList(buildPath);
            }
        }
        return pythonPath;
    }

    public void setPythonPath(ArrayList<String> pythonPath) {
        assert pythonPath != null;
        this.pythonPath = pythonPath;
    }

    public ArrayList<String> getJavaPath() {
        if(javaPath == null) {
            // No java path intiialized try to pull from project properties
           String buildPath = projectProperties.getProperty(JAVA_LIB_PATH);
            if (buildPath != null) { 
                javaPath = buildPathList(buildPath);
            } else {
                javaPath = new ArrayList<String>();
            }
        }
        return javaPath;
    }

    public void setJavaPath(ArrayList<String> javaPath) {
        assert javaPath != null;
        this.javaPath = javaPath;
    }

    public String getActivePlatformId() {
        if(activePlatformId == null)
            activePlatformId = projectProperties.getProperty(ACTIVE_PLATFORM);
        return activePlatformId;
    }

    public void setActivePlatformId(String activePlatformId) {
        this.activePlatformId = activePlatformId;
    }


    
    //Storing
    public void save () {
        try {
//            if (this.sourceRoots != null) {
/* TODO: Determine if still needed, believe save get partly address in 
saveProperties context so commenting some existing logic now and remove if not needed */
//                final SourceRoots sr = this.project.getSourceRoots();
//                if (this.sourceRoots == null) {
//                    this.sourceRoots = this.getSourceRoots();
//                }
//                sr.putRoots(this.sourceRoots);
//                // TODO: Make sure this works correctly and stores all needed
////                this.projectProperties.put(SRC_DIR , sr.toString());
//                this.projectProperties.put(SRC_DIR , sr.getRoots());
//            }
//            if (this.testRoots != null) {
//                final SourceRoots sr = this.project.getTestRoots();
//                sr.putRoots(this.testRoots);
//            }
            // store properties
            ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    saveProperties();
                    return null;
                }
            });
            ProjectManager.getDefault().saveProject(project);
        } catch (MutexException e) {
            Exceptions.printStackTrace((IOException) e.getException());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    /**
     * Takes current properties in memory and saves into projectProperties file
    */
    private void saveProperties () throws IOException {
        
        // make sure current values are set prior to saving
        
        
        // name
        if (this.name != null) {
            this.projectProperties.put(PROJECT_NAME, this.name);
        } else {
            // do it based on project name or specically the ProjectInfo attribure
            
            /* TODO: Need to determine if "PythonInfo" may be serving similar 
            puporpse to PythontProjectProperties and needs to be refactoried */
            this.projectProperties.put(PROJECT_NAME, this.project.getName());
        }
        // description
        if (this.description != null) {
            this.projectProperties.put(PROJECT_DESCRIPTION, this.description);
        } else {
            // do it based on project name or specically the ProjectInfo attribure
            
            /* TODO: Need to determine if "PythonInfo" may be serving similar 
            puporpse to PythontProjectProperties and needs to be refactoried */
            
            this.projectProperties.put(PROJECT_NAME, this.project.getDescription());
        }

        if (this.mainModule != null) {
            this.projectProperties.put(MAIN_FILE, this.mainModule);
        } else {
            this.projectProperties.put(MAIN_FILE, EMPTY_VALUE);
        }
        
//        if (this.encoding != null||!this.encoding.equals("")) {
//            this.projectProperties.put(SOURCE_ENCODING, this.encoding);
//       } else {
//            this.projectProperties.put(SOURCE_ENCODING, DEFAULT_ENCODING);
//        }
        if (this.encoding == null || this.encoding.equals("")) {
            this.projectProperties.put(SOURCE_ENCODING, DEFAULT_ENCODING);
       } else {
            this.projectProperties.put(SOURCE_ENCODING, this.encoding);
        }
  
        if (this.appArgs != null) {
            this.projectProperties.put(APPLICATION_ARGS, this.appArgs);
        } else {
            this.projectProperties.put(APPLICATION_ARGS, EMPTY_VALUE);
        }
        
        if (this.pythonPath != null){
            this.projectProperties.put(PYTHON_LIB_PATH, buildPathString(this.pythonPath));
        } else {
            this.projectProperties.put(PYTHON_LIB_PATH, EMPTY_VALUE);
        }
        
        
        if (this.javaPath != null){
            this.projectProperties.put(JAVA_LIB_PATH, buildPathString(this.javaPath));
        } else {
            this.projectProperties.put(JAVA_LIB_PATH, EMPTY_VALUE);
        }
        
        if (this.activePlatformId != null){
            this.projectProperties.put(ACTIVE_PLATFORM, this.activePlatformId);
        } else {
            this.projectProperties.put(ACTIVE_PLATFORM, EMPTY_VALUE);
        }
        // TODO: Maybe move "save" activity to save project source related properties here instead of in save
        
        // capture source related attributes to project properties
        final SourceRoots sr = this.project.getSourceRoots();
        if (this.sourceRoots == null) {
            this.sourceRoots = this.getSourceRoots();
        }
        sr.putRoots(this.sourceRoots);
        this.projectProperties.put(SRC_DIR, sr.getRoots());

        // store all the properties        
//        helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, projectProperties);
//        helper.putProperties(AntProjectHelper.PRIVATE_PROPERTIES_PATH, privateProperties);

//        LOGGER.log(Level.INFO, "Project Dir = {0}", project.getProjectDirectory().getPath());
//        File projectPropertiesFile = new File(project.getProjectDirectory().getPath(), PROJECT_PROPERTIES_PATH);
//        LOGGER.log(Level.INFO, "Project Properties File = {0}", projectPropertiesFile);
//        File projectPropertyFolder = new File(projectPropertiesFile.getParent());
//        LOGGER.log(Level.INFO, "Project Properties Folder= {0}", projectPropertyFolder);
//        
//        // project projectPropertyFolder doesn't exist so create it
//        if ( !projectPropertyFolder.exists()) {
//                boolean isFolderCreated  = projectPropertyFolder.mkdir();
//                if (!isFolderCreated) {
//                    LOGGER.log(Level.SEVERE, "Issue creating {0}", projectPropertyFolder);
//                } else {
//                    LOGGER.log(Level.INFO, "Created project folder {0}", projectPropertyFolder);
//                
//                }
//        }

        File projectPropertiesFile = findProjectPropertiesFile(this.project);
//        if (projectPropertiesFile == null ) {
//            createPythonProjectPropertiesFolder(projectPropertiesFile);
//        }

        // save properties files
        LOGGER.info("Project Properties Path = " + projectPropertiesFile);
        try (FileOutputStream output = new FileOutputStream(projectPropertiesFile)) {
            this.projectProperties.store(output, "Python Project Properties");
            output.close();
        }
        // additional changes
        // encoding
        if (this.encoding != null) {
            try {
                FileEncodingQuery.setDefaultEncoding(Charset.forName(this.encoding));
            } catch (UnsupportedCharsetException e) {
                //When the encoding is not supported by JVM do not set it as default
            }
        }
    }
    
    private static final String PYTHON_PATH_SEP = "|";
    /**
     *Build a path string from arraylist
     * @param path
     * @return
     */
    private static String buildPathString(ArrayList<String> path){
        StringBuilder pathString = new StringBuilder();
        int count = 0;
        for(String pathEle: path){
            pathString.append(pathEle);
            if (count++ < path.size()){
                pathString.append(PYTHON_PATH_SEP);
            }
        }
        return pathString.toString();
    }
    /**
     * Takes a provided path string and converts it into an array of individual
     * string path entries.
     * 
     * @param pathString
     * @return
     */
    private static ArrayList<String> buildPathList(String pathString){
        ArrayList<String> pathList = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(pathString, PYTHON_PATH_SEP);        
        while(tokenizer.hasMoreTokens()){
            pathList.add(tokenizer.nextToken());
        }
        return pathList;
    }

    /**
     * Reads values from properties projectPropertiesFile and set them in the 
     * PythonProjectProperties attributes for use elsewhere
     */
    void loadProperties() throws FileNotFoundException, IOException {
//        File file = new File(project.getProjectDirectory().getPath(), PROJECT_PROPERTIES_PATH); 
//        
        boolean isNewPropertyFile = false;
//        
//        // if project properties doesn't exist then create one
//        if(!file.exists()) {
//            LOGGER.log(Level.INFO, "Creating new PythonProjectProperties.");
//            isNewPropertyFile = true;
//            // check if nbproject projectPropertyFolder exists and create if needed
//            File folder = file.getParentFile();
//
//            if ( !folder.exists()) {
//                boolean isFolderCreated  = folder.mkdir();
//                if (!isFolderCreated) {
//                    LOGGER.log(Level.SEVERE, "Issue creating {0}", folder);
//                } else {
//                    LOGGER.log(Level.INFO, "Created project folder {0}", folder);
//                
//                }
//            }
//            
//            saveProperties();
//            // store initial empty project projectPropertiesFile
////            storeProperties( this.projectProperties, PROJECT_PROPERTIES_PATH);
//        }

                
        // load project properties from properties projectPropertiesFile
        File projectPropertiesFile = findProjectPropertiesFile(project); 
//        File projectPropertiesFile = new File(project.getProjectDirectory().getPath(), PROJECT_PROPERTIES_PATH); 
        if (projectPropertiesFile != null && projectPropertiesFile.exists()) {
            isNewPropertyFile = false;
            // existing project properties file exists so load properties
        try ( FileInputStream input = new FileInputStream(projectPropertiesFile)) {
            this.projectProperties.load(input);
        } catch (IOException ex) {
            // properties projectPropertiesFile not found or does not exist yet
            Exceptions.printStackTrace(ex);
            
            LOGGER.log(Level.SEVERE, "Issue loading input file {0}", ex.toString());

        }
        
        } else {
        // project properties file doesn'e exist so populate defaults and store
        isNewPropertyFile = true;
        }
//        this.projectProperties.computeIfAbsent(MAIN_FILE,  ()this.mainModule = EMPTY_VALUE);
//        this.projectProperties.computeIfPresent(MAIN_FILE, 
//                (key, value) -> this.mainModule = (String)value);

        // load in properties to memory  and if not defined use default values
        
        this.projectProperties.put(PROJECT_NAME, 
                this.projectProperties.getOrDefault(PROJECT_NAME, 
                        (name!=null)?name:EMPTY_VALUE));

        this.projectProperties.put(PROJECT_DESCRIPTION, 
                this.projectProperties.getOrDefault(PROJECT_DESCRIPTION, 
                        (description!=null)?description:EMPTY_VALUE));

        this.projectProperties.put(MAIN_FILE, 
                this.projectProperties.getOrDefault(MAIN_FILE, 
                        (mainModule!=null)?mainModule:EMPTY_VALUE));
//        if (projectProperties.get(MAIN_FILE) != null) {
//            projectProperties.put(MAIN_FILE, mainModule);
//            mainModule = (String)projectProperties.get(MAIN_FILE);
//        }

//        this.projectProperties.computeIfPresent(SOURCE_ENCODING, 
//                (key, value) -> this.encoding = (String)value);
        this.projectProperties.put(SOURCE_ENCODING, 
            this.projectProperties.getOrDefault(MAIN_FILE, 
                    (encoding!=null)?encoding:DEFAULT_ENCODING));


//        if (encoding != null) {
//            projectProperties.put(SOURCE_ENCODING, encoding);
//        }

//        this.projectProperties.computeIfPresent(APPLICATION_ARGS, 
//                (key, value) -> this.appArgs = (String)value);
        this.projectProperties.put(APPLICATION_ARGS, 
            this.projectProperties.getOrDefault(APPLICATION_ARGS, 
                    (appArgs != null)?appArgs:EMPTY_VALUE));
//        if (appArgs != null) {
//            projectProperties.put(APPLICATION_ARGS, appArgs);
//        }
//        this.projectProperties.computeIfPresent(PYTHON_LIB_PATH, 
//                (key, value) -> this.pythonPath = (buildPathList((String)value)));
        this.projectProperties.put(PYTHON_LIB_PATH, 
//            buildPathList((String)
                    (this.projectProperties.getOrDefault(PYTHON_LIB_PATH, 
                            (pythonPath != null)?pythonPath:EMPTY_VALUE))
//            )
        );
//        if (pythonPath != null) {
//            projectProperties.put(PYTHON_LIB_PATH, buildPathString(pythonPath));
//        }

//        this.projectProperties.computeIfPresent(JAVA_LIB_PATH, 
//                (key, value) -> this.javaPath = (buildPathList((String)value)));
        this.projectProperties.put(JAVA_LIB_PATH, 
//            buildPathList((String)
                    (this.projectProperties.getOrDefault(JAVA_LIB_PATH, 
                            (javaPath == null?EMPTY_VALUE:javaPath)))
//        )
         );
        
//        if (javaPath != null) {
//            projectProperties.put(JAVA_LIB_PATH, buildPathString(javaPath));
//        }
//        this.projectProperties.computeIfPresent(ACTIVE_PLATFORM, 
//                (key, value) -> this.activePlatformId = (String)value);

        this.projectProperties.put(ACTIVE_PLATFORM, 
            this.projectProperties.getOrDefault(ACTIVE_PLATFORM, 
                            (activePlatformId == null ?EMPTY_VALUE:activePlatformId)));
        
//        if (activePlatformId != null) {
//            projectProperties.put(ACTIVE_PLATFORM, activePlatformId);
//        }

        // store all the properties        
//        helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, projectProperties);
//        helper.putProperties(AntProjectHelper.PRIVATE_PROPERTIES_PATH, privateProperties);
        // additional changes
        // encoding
        if (this.encoding != null) {
            try {
                FileEncodingQuery.setDefaultEncoding(Charset.forName(this.encoding));
            } catch (UnsupportedCharsetException e) {
                //When the encoding is not supported by JVM do not set it as default
            }
        }
        
        if (isNewPropertyFile && projectPropertiesFile != null) {
//            File projectPropertiesFile = findProjectPropertiesFile(project); 
//            // TODO: Determine if this is the right store/save method and if needed here or elsewhere
//            storeProperties( this.projectProperties, PROJECT_PROPERTIES_PATH);
            storeProperties( this.projectProperties,projectPropertiesFile.getPath());
////            saveProperties();
//            save();
//        } else {
//            LOGGER.log(Level.SEVERE, "Issue creating properties file {0}", 
//                    projectPropertiesFile.toPath());
        }
    }
    
    /**
     *  For given project, find the project properties file if it exists
     * 
     * @param project
     * @return returns null if not available else returns path to properties file.
     */
     File findProjectPropertiesFile(PythonProject project) {
        // look for nbproject\project.properties
         File file = new File(project.getProjectDirectory().getPath(), PROJECT_PROPERTIES_PATH);
        
        boolean isNewPropertyFile = false;
        
        // if project properties folder doesn't exist then create it
        if(!file.getParentFile().exists()) {
            createPythonProjectPropertiesFolder(file);
        }
        // if project properties doesn't exist then create one
//        if(!file.exists()) {
//             try {
//                 saveProperties();
//                 save();
//            return null;
//             } catch (IOException ex) {
//                 Exceptions.printStackTrace(ex);
//             }
//        }
        // TODO: try to lookfor a "setup.py" file in which case leverage this
        
        return file;
        }

     public void createPythonProjectPropertiesFolder(File file) { // throws IOException {
        boolean isNewPropertyFile;
        LOGGER.log(Level.INFO, "Creating new PythonProjectProperties.");

        // check if nbproject projectPropertyFolder exists and create if needed
        File folder = null;
        try {
        folder = file.getParentFile();
        if ( !folder.exists()) {
            boolean isFolderCreated  = folder.mkdir();
            if (!isFolderCreated) {
                LOGGER.log(Level.SEVERE, "Issue creating {0}", folder);
            } else {
                LOGGER.log(Level.INFO, "Created project folder {0}", folder);
                
            }
        }
        // store initial empty project projectPropertiesFile
//        saveProperties();
        
        } catch (Exception e ) {
                        LOGGER.log(Level.SEVERE, "Issue creating {0}", folder);
        }
//        try {
//           
//            // store initial empty project projectPropertiesFile
////            storeProperties( this.projectProperties, PROJECT_PROPERTIES_PATH);
//        } catch (IOException ex) {
//            Exceptions.printStackTrace(ex);
//        }
    }
//    }

    /**
     * Uses to store NetBeans Python project properties file.
     * 
     * @param propertyPath
     * @throws IOException 
     */
    public void storeProperties(Properties properties, String propertyPath) throws IOException {
        LOGGER.log(Level.INFO, "Storing PythonProjectProperties {0}.", propertyPath);

        try ( FileOutputStream  out = new FileOutputStream(propertyPath)) {
            properties.store(out, "Python Project");
//                boolean isCreated = projectPropertiesFile.createNewFile(); // create your projectPropertiesFile on the projectPropertiesFile system
//            } catch (IOException ex) {
// problems creating properties projectPropertiesFile
//                Exceptions.printStackTrace(ex);
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
            throw ex;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            throw ex;
        }
    }

        // TODO Implement logic to determine project properties based on setup.py
        
//     private static Properties findProjectPropertiesFile(FileObject projectDirectory, FileChangeListener listener) throws PythonException {
//        Properties props = new Properties();
//        
//        PythonExecution pye;
//        try {
//            FileObject setuppy = projectDirectory.getFileObject(SETUPPY);
//            if (listener != null && !REGISTRED_SETUPPY.contains(setuppy.getPath())) {
//                REGISTRED_SETUPPY.add(setuppy.getPath());
//                setuppy.addFileChangeListener(listener);
//            }
//            pye = createProjectPropertiesReader(projectDirectory, setuppy);
//            Future<Integer> result = pye.run();
//            Integer value = result.get();
//            if (value == 0) {
//                fillPropertiesFromSetupOutput(props, pye.getOutput());
//            } else {
//                findProjectPropertiesForceUtf8InSetuppy(props, projectDirectory, setuppy);
//            }
//        } catch (InterruptedException | ExecutionException | IOException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//
//        return props;
//    }

    public String getName() {
        // if not set try to get from in memory property
         if (this.name == null) {
            this.name = projectProperties.getProperty(PROJECT_NAME);
        }
         // still not name so set to default
         if (this.name == null) {
            this.name = EMPTY_VALUE;
        }

         
        return this.name;
    }
    public void setName(String projectNameField) {
        this.name = projectNameField;
    }
    
    public String getDescription() {
        // if not set try to get from in memory property
        if (this.description == null) {
            this.description = projectProperties.getProperty(PROJECT_DESCRIPTION);
        }
         // still not name so set to default
         if (this.description == null) {
            this.description = EMPTY_VALUE;
        }
        return description;
    }

    public void setDescription(String projectDescriptionField) {
        this.description = projectDescriptionField;
    }
    
}
