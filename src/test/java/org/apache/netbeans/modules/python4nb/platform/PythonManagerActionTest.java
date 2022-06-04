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
package org.apache.netbeans.modules.python4nb.platform;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.HelpCtx;

/**
 *
 * @author ebres
 */
public class PythonManagerActionTest {
    
    public PythonManagerActionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of performAction method, of class PythonManagerAction.
     */
    @Test
    public void testPerformAction() {
        System.out.println("performAction");
        PythonManagerAction instance = new PythonManagerAction();
// TODO: FIX THIS        instance.performAction();
    }

    /**
     * Test of getName method, of class PythonManagerAction.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        PythonManagerAction instance = new PythonManagerAction();
        String expResult = "Python Platforms";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getHelpCtx method, of class PythonManagerAction.
     */
    @Test
    public void testGetHelpCtx() {
        System.out.println("getHelpCtx");
        PythonManagerAction instance = new PythonManagerAction();
        HelpCtx expResult = null;
        HelpCtx result = instance.getHelpCtx();
        assertEquals(expResult, result);
    }

    /**
     * Test of accept method, of class PythonManagerAction.
     */
    @Test
    public void testAccept() {
        System.out.println("accept");
        Object sender = null;
        PythonManagerAction instance = new PythonManagerAction();
        boolean expResult = true;  
        
        /* TODO: Determine if it should be true/false - note accept 
        returns false when disabled */
        
        boolean result = instance.accept(sender);
        assertEquals(expResult, result);
    }
    
}
