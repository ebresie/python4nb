/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/* TODO: This is based on nbPython code and needs to be reconciled as 
applicable.
*/
package org.apache.netbeans.modules.python4nb.api;

import java.util.List;
import javax.swing.AbstractListModel;
//import org.netbeans.modules.python.api.PythonPlatform;
//import org.netbeans.modules.python.api.PythonPlatformManager;
import org.apache.netbeans.modules.python4nb.platform.PythonPlatform;
import org.apache.netbeans.modules.python4nb.platform.PythonPlatformManager;

public class PythonPlatformListModel extends AbstractListModel {
    private PythonPlatformManager manager = PythonPlatformManager.getInstance();
    private List<PythonPlatform> model = manager.getPlatforms();

    @Override
    public int getSize() {
        return model.size();
    }

    @Override
    public Object getElementAt(int index) {
        if (index >= 0 && index < model.size()) {
            return model.get(index);
        } else {
            return null;
        }
    }
    
    public void refresh(){
        model = manager.getPlatforms();
        fireContentsChanged(this, 0, model.size() -1);
    }
}
