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

import java.io.File;
import java.io.FileInputStream;
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
import org.apache.netbeans.modules.python4nb.project.PythonProject;
import org.apache.netbeans.modules.python4nb.project.SourceRoots;
import org.apache.netbeans.modules.python4nb.util.Pair;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.queries.FileEncodingQuery;
import org.openide.filesystems.FileObject;
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
    
    //Relative path from project directory to the customary shared properties file.
    public static final String PROJECT_PROPERTIES_PATH = "nbproject/project.properties"; // NOI18N
    
    // Relative path from project directory to the customary private properties file.
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

    public PythonProjectProperties (final PythonProject project) {
        assert project != null;
        this.project = project;
//        this.eval = project.getEvaluator();
//        Map<> properties = new HashMap();
        this.projectProperties = new Properties();
        loadProperties();
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
        if (this.encoding == null) {
            this.encoding = projectProperties.getProperty(SOURCE_ENCODING);
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
            for (int i=0; i< rootURLs.length; i++) {                
                final File f  = new File (URI.create (rootURLs[i].toExternalForm()));            
                final String s = sourceRoots.getRootDisplayName(rootLabels[i], rootProps[i]);
                data.add(Pair.of(f, s));
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
        if (mainModule == null && projectProperties != null) {
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
            if (this.sourceRoots != null) {
                final SourceRoots sr = this.project.getSourceRoots();
                sr.putRoots(this.sourceRoots);
                // TODO: Make sure this works correctly and stores all needed
                this.projectProperties.put(SRC_DIR , sr.toString());
            }
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
     * Takes current properties in memory and saves them into project projecteist file
    */
    private void saveProperties () throws IOException {
        if (mainModule != null) {
            projectProperties.put(MAIN_FILE, mainModule);
        }
        
        if (encoding != null) {
            projectProperties.put(SOURCE_ENCODING, encoding);
        }
        
        if (appArgs != null) {
            projectProperties.put(APPLICATION_ARGS, appArgs);
        }
        if (pythonPath != null){
            projectProperties.put(PYTHON_LIB_PATH, buildPathString(pythonPath));
        }
        if (javaPath != null){
            projectProperties.put(JAVA_LIB_PATH, buildPathString(javaPath));
        }
        if (activePlatformId != null)
            projectProperties.put(ACTIVE_PLATFORM, activePlatformId);
        
        // store all the properties        
//        helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, projectProperties);
//        helper.putProperties(AntProjectHelper.PRIVATE_PROPERTIES_PATH, privateProperties);

        try (FileOutputStream output = new FileOutputStream(PROJECT_PROPERTIES_PATH)) {;
            this.projectProperties.store(output, "Python Project Properties");
            output.close();
        }
        // additional changes
        // encoding
        if (encoding != null) {
            try {
                FileEncodingQuery.setDefaultEncoding(Charset.forName(encoding));
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
     * Reads values from properties file and set them in the 
     * PythonProjectProperties attributes for use elsewhere
     */
    void loadProperties() {

        try ( FileInputStream input = new FileInputStream(PROJECT_PROPERTIES_PATH)) {
            this.projectProperties.load(input);
        } catch (IOException ex) {
            // properties file not found or does not exist yet
            Exceptions.printStackTrace(ex);
        }

        
        this.projectProperties.computeIfPresent(MAIN_FILE, 
                (key, value) -> this.mainModule = (String)value);
//        if (projectProperties.get(MAIN_FILE) != null) {
//            projectProperties.put(MAIN_FILE, mainModule);
//            mainModule = (String)projectProperties.get(MAIN_FILE);
//        }

        this.projectProperties.computeIfPresent(SOURCE_ENCODING, 
                (key, value) -> this.encoding = (String)value);

//        if (encoding != null) {
//            projectProperties.put(SOURCE_ENCODING, encoding);
//        }

        this.projectProperties.computeIfPresent(APPLICATION_ARGS, 
                (key, value) -> this.appArgs = (String)value);
//        if (appArgs != null) {
//            projectProperties.put(APPLICATION_ARGS, appArgs);
//        }
        this.projectProperties.computeIfPresent(PYTHON_LIB_PATH, 
                (key, value) -> this.pythonPath = (buildPathList((String)value)));

//        if (pythonPath != null) {
//            projectProperties.put(PYTHON_LIB_PATH, buildPathString(pythonPath));
//        }

        this.projectProperties.computeIfPresent(JAVA_LIB_PATH, 
                (key, value) -> this.javaPath = (buildPathList((String)value)));

//        if (javaPath != null) {
//            projectProperties.put(JAVA_LIB_PATH, buildPathString(javaPath));
//        }
        this.projectProperties.computeIfPresent(ACTIVE_PLATFORM, 
                (key, value) -> this.activePlatformId = (String)value);

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
    }

}
