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
package org.apache.netbeans.modules.python4nb.platform;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

// Portions of this code are based on nbPython Code.  

class PythonAutoDetector {
      private static final Logger LOGGER = Logger.getLogger(PythonAutoDetector.class.getName());

    private final ArrayList<String> matches = new ArrayList<>();
    boolean searchNestedDirectoies = true;

    private void processAction(File dir) {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Inspecting: {0}", dir.getAbsolutePath());
        }
        if(dir.isFile()){
            int pos = dir.getName().indexOf(".");
            String name;
            String ext;
            if (pos > -1 ){
                 name = dir.getName().substring(0, pos);
                 ext = dir.getName().substring(pos+1);
            }else{
                name = dir.getName();
                ext = "";
            }

            if (isPythonDirectory(dir.getParent()) && isPythonExecutableFile(name)) {
                if (Utilities.isWindows()) {
                    if (ext.equalsIgnoreCase("exe") || ext.equalsIgnoreCase("bat")) {
                        if( addMatch(dir.getAbsolutePath())) { //don't report duplicates
                            if (LOGGER.isLoggable(Level.CONFIG)) {
                                LOGGER.log(Level.CONFIG, "Match (Windows): {0}", dir.getAbsolutePath());                           
                            }
                        }
                    }
                } else if(Utilities.isMac()) {
                    if (ext.equalsIgnoreCase("")) {
                        if( addMatch(dir.getAbsolutePath())) {
                            if (LOGGER.isLoggable(Level.CONFIG)) {
                                LOGGER.log(Level.CONFIG, "Match (Mac): {0}", dir.getAbsolutePath());                           
                            }
                        }
                    }
                } else { // Not Windows or Mac, must be Unix-like system...
                    if (ext.equalsIgnoreCase("")) {
                        if( addMatch(dir.getAbsolutePath())) {
                            if (LOGGER.isLoggable(Level.FINE)) {
                                LOGGER.log(Level.FINE, "Match (Unix-like): {0}", dir.getAbsolutePath());                           
                            }
                        }                     
                    }
                }
            }
        }
        
        if (dir.isDirectory() && isPythonDirectory(dir.getName())) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    searchNestedDirectoies = true;
                    traverse(new File(dir, child), false); // false had been searchNestedDirectoies
                }
            }
        }

    }

    public ArrayList<String> getMatches() {
        return matches;
    }
    
    public int countMatches(){
        return matches.size();
    }
    
    private boolean addMatch( String sMatch){
        // ensure that no match is added twice (no duplicates!)
        // tweaking may be required... especially for /python vs. /python/bin
        for( String sTest : matches) { // TODO: Do not compare trailing \ or / 
            if(sMatch.equalsIgnoreCase(sTest)){ // perhaps shouldn't ignore case for non-Windows
                return false; // do not add if it already exists!
            }
//        Iterator<String> iterator = matches.iterator();
//        while (iterator.hasNext()) {
//            String sTest = iterator.next(); // TODO: Do not compare trailing \ or / 
//            if(sMatch.equalsIgnoreCase(sTest)){ // perhaps shouldn't ignore case for non-Windows
//                return false; // do not add if it already exists!
//            }
        }
        matches.add(sMatch); // no preexisting match... add new.
        return true;
    }
    
    public void traverseEnvPaths() throws SecurityException{
        String delims = "[" + System.getProperty("path.separator") + "]";
        String sEnvPath;
        try{
            // Env variables must be upper-case in Unix. Windows is case-insensitive.
            sEnvPath = System.getenv("PATH");
        } catch (SecurityException se) {
            Exceptions.printStackTrace(se);
            return;
        } 
     
        String[] paths = sEnvPath.split(delims);
        int iCount = countMatches();
        // search ONLY paths that contain the substring python/jython/anaconda (not cached pkg folder)
        for(String spath: paths) { 
//            if( spath.toLowerCase().contains("jython") ||
//                spath.toLowerCase().contains("python") ||
//                (spath.toLowerCase().contains("anaconda") && 
//                    !spath.toLowerCase().contains("pkg")) ){
            if( isPythonDirectory(spath))  {
                searchNestedDirectoies = true;
                processAction(new File(spath)); //traverse(new File(spath), false);
//                if( iCount < matches.size()){ // take only the first match
//                    return;
//                }
            }
        }
        
        if( iCount < matches.size()){ // take only the first match
            return;
        }
        // no python found!!!
        if (LOGGER.isLoggable(Level.CONFIG)) {
            LOGGER.log(Level.CONFIG, "Python/Jython not found in environment path {0}", sEnvPath);            
        }
    }

    // Given a directory, search all grandchild directories 
    //   for bin directories containing files to be processed
    // This is specific to Mac which installs its Pythons like
    //  /Library/Frameworks/Python.framework/Versions/3.4/bin/python3
    //      and
    //  /System/Library/Frameworks/Python.framework/Versions/2.7/    
    public void traverseMacDirectories(File dir) {
        
        // Make sure the path is a directory named "Versions".
        if (! dir.getName().endsWith("Versions") || ! dir.isDirectory()) {
            return;
        }

        // Directories which are not symbolic links.
        FileFilter directoriesOnlyFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() &! Files.isSymbolicLink(Paths.get(file.getAbsolutePath()));
            }
        };

        // "bin" directory
        FileFilter binDirectoryFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().equals("bin") && file.isDirectory();
            }
        };

        File[] versionDirs = dir.listFiles(directoriesOnlyFilter);
        File[] binDirs;  // Should have either 0 or 1 element.
        File[] binContents;
        for (File versionDir : versionDirs) {
            binDirs = versionDir.listFiles(binDirectoryFilter);
            if (binDirs.length < 1) {
                continue;
            }

            File binDir = binDirs[0];
            binContents = binDir.listFiles(new FileFilter(){
                @Override public boolean accept(File file){
                    return file.isFile(); // Regular files only.
                }
            });

            for (File binContent : binContents) {
                searchNestedDirectoies = false;
                processAction(binContent);
            }
        }// END for each version dir
    }   
    
    // Given a directory, for each subdirectory within, 
    //   search the subdirectory and next-level subdirectories only
    public void traverseDirectory( File dir) {
        if (dir.isDirectory()) { //are we already IN the ?ython dir?
            String spath = dir.getName();

            /*
            TODO: Make more efficient maybe using Java 8 Files.walk and filters like
                Files.walk(Paths.get(dir))
                .filter(Files::isRegularFile)
                .forEach(filePath -> {
                    String name = filePath.getFilename().toString();
                    if (isPythonDirectory(filePath)) {
                        System.out.println(filePath.getFileName());
                    }
                });
            */
            if( isPythonDirectory(spath) ){
                searchNestedDirectoies = true; // must set each time
                processAction(dir);
                return;
            }
            // otherwise, we check the next level for the ?ython dir?
            String[] children = dir.list();
            if(children != null){
                for (int i=0; i<children.length; i++) {
                    File fDirectory = new File(dir, children[i]);
//                    if (fDirectory.isDirectory() || fDirectory.isFile()) {
                    // check through directories for possible python directories
                    if (fDirectory.isDirectory() ) {
                        spath = fDirectory.getName();
                        if (isPythonDirectory(spath)) {
                            searchNestedDirectoies = true; // must set each time
                            processAction(fDirectory);
                        } else {
                            // the child level is not a python specific directory
                            searchNestedDirectoies = true; // must set each time
                            
                            // but what about the grandchilder
                            // maybe re-run traverseDirectory with fDirectory if directory
                            traverseDirectory(fDirectory);
                        }
                    } else {
                        // not a directory so maybe a file
                        continue;
                        // maybe check if it's a python executable?
                    }
                }
            }
        }        
    } 

    private static boolean isPythonDirectory(String spath) {
        
        LOGGER.log(Level.ALL, "** Checking if {0} is python directory", spath);
        
//        if (!new File(spath).isDirectory()) { return false;}
        return (spath.toLowerCase().contains("jython") ||
                spath.toLowerCase().contains("python") ||
                spath.toLowerCase().contains("anaconda")||
                spath.toLowerCase().contains("pypy") )
                && (!spath.toLowerCase().contains("pkgs")&& 
                !spath.toLowerCase().contains("scripts") &&
                !spath.toLowerCase().contains("condabin")&&
                !spath.toLowerCase().contains("uninstall")) ;
    }
    
        private static boolean isPythonExecutableFile(String spath) {
        
        LOGGER.log(Level.ALL, "** Checking if {0} is python file.", spath);
  //      if (!new File(spath).isFile()) { return false;}

        return (spath.toLowerCase().contains("jython") ||
                spath.toLowerCase().contains("python") ||
                spath.toLowerCase().contains("python2") ||
                spath.toLowerCase().contains("python3") ||
                spath.toLowerCase().contains("anaconda") ||
                spath.toLowerCase().contains("pypy")||
                spath.toLowerCase().contains("pypy3")) && !(
                (spath.toLowerCase().contains("uninstall")) || 
                (spath.toLowerCase().contains("pythonw")));
    }
    
    public void traverse(File dir, boolean recersive) {

        processAction(dir);

        if (dir.isDirectory()) {
            String[] children = dir.list();
            if(children != null){
                if(searchNestedDirectoies){// recursive is bad - better only search one or two levels
                    if(searchNestedDirectoies != recersive)
                        searchNestedDirectoies = recersive;
                    for (String child : children) {
                        traverse(new File(dir, child), recersive);
                    }
                }
            }
        }

    }
    
}
