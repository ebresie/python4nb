/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/package-info.java to edit this template
 */
@GrammarRegistration(mimeType="text/x-python", grammar="python.tmLanguage.json")

@TemplateRegistrations(value = {
    @TemplateRegistration(
            folder = "Python",
            iconBase = "org/apache/netbeans/modules/python4nb/editor/py.png",
            requireProject = false,
            displayName = "#Templates_python",
            content = "PythonTemplate.py",
            description = "Description.html"
    ),
    @TemplateRegistration(
            folder = "Python",
            iconBase = "org/apache/netbeans/modules/python4nb/editor/py.png",
            requireProject = false,
            displayName = "#Setup_Templates_python",
            content = "PythonSetupTemplate.py",
            description = "Description.html"
    )
}
)
@Messages( value = {"Templates_python=Python Files",
    "Setup_Templates_python=Python Setup based Files",
})
package org.apache.netbeans.modules.python4nb.editor;

import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.api.templates.TemplateRegistrations;
import org.netbeans.modules.textmate.lexer.api.GrammarRegistration;
import org.openide.util.NbBundle.Messages;
