/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.apache.netbeans.modules.python4nb.api;

import java.io.IOException;
//import org.netbeans.api.extexecution.input.InputProcessor;
import org.netbeans.api.extexecution.base.input.InputProcessor;

/** TODO: This code is derived from nbPython codebase.  
 * Need to consider proper handling of code.
 */
public class PythonOutputProcessor implements InputProcessor {
    StringBuilder builder = new StringBuilder();
    @Override
    public void processInput(char[] input) throws IOException {
        builder.append(input);
    }

    @Override
    public void reset() throws IOException {
        //builder = new StringBuilder();
    }

    @Override
    public void close() throws IOException {

    }
    public String getData(){
        return builder.toString();
    }

}
