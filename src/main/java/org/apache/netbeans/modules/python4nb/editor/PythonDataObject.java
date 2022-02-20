/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/templateDataObjectAnno.java to edit this template
 */
package org.apache.netbeans.modules.python4nb.editor;

import java.io.IOException;
import org.apache.netbeans.modules.python4nb.editor.file.MIMETypes;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.text.MultiViewEditorElement;
import org.netbeans.modules.textmate.lexer.api.GrammarRegistration;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@Messages({
    "LBL_Python_LOADER=Files of Python"
})
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_Python_LOADER",
        mimeType = MIMETypes.PY,
        extension = {"py"}
)
@DataObject.Registration(
        mimeType = MIMETypes.PY,
        iconBase = "org/apache/netbeans/modules/python4nb/editor/py.png",
        displayName = "#LBL_Python_LOADER",
        position = 300
)
@ActionReferences({
    @ActionReference(
            path = "Loaders/" + MIMETypes.PY + "/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200
    ),
    @ActionReference(
            path = "Loaders/" + MIMETypes.PY + "/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300
    ),
    @ActionReference(
            path = "Loaders/" + MIMETypes.PY + "/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500
    ),
    @ActionReference(
            path = "Loaders/text" + MIMETypes.PY + "Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600
    ),
    @ActionReference(
            path = "Loaders/text" + MIMETypes.PY + "Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800
    ),
    @ActionReference(
            path = "Loaders/text" + MIMETypes.PY + "Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000
    ),
    @ActionReference(
            path = "Loaders/text" + MIMETypes.PY + "Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200
    ),
    @ActionReference(
            path = "Loaders/text" + MIMETypes.PY + "Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300
    ),
    @ActionReference(
            path = "Loaders/text" + MIMETypes.PY + "Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400
    )
})
@GrammarRegistration(grammar="python.tmLanguage.json", mimeType=MIMETypes.PY)
public class PythonDataObject extends MultiDataObject {

    public PythonDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        registerEditor(MIMETypes.PY, true);
    }

    @Override
    protected int associateLookup() {
        return 1;
    }

    @MultiViewElement.Registration(
            displayName = "#LBL_Python_EDITOR",
            iconBase = "org/apache/netbeans/modules/python4nb/editor/py.png",
            mimeType = MIMETypes.PY,
            persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED,
            preferredID = "Python",
            position = 1000
    )
    @Messages("LBL_Python_EDITOR=Source")
    public static MultiViewEditorElement createEditor(Lookup lkp) {
        return new MultiViewEditorElement(lkp);
    }

}
