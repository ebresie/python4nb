/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.netbeans.modules.python4nb.project;

/**
 *
 */
public final class PythonProjectConstants {

    private PythonProjectConstants() {
    }

    /**
     * Constant for Source Files sources group.
     */
    public static final String SOURCES_TYPE_PYTHON = "PYTHON-Sources"; // NOI18N

    /**
     * Constant for Site Root sources group.
     * @since 1.0
     */
    public static final String SOURCES_TYPE_PYTHON_SITE_ROOT = "PYTHON-SiteRoot"; // NOI18N

    /**
     * Constant for Test Files sources group.
     */
    public static final String SOURCES_TYPE_PYTHON_TEST = "PYTHON-Tests"; // NOI18N

    /**
     * Constant for Test Selenium Files sources group.
     */
    /* TODO: Based on Web constants.  This may not be needed and can be removed 
    or need to change to equivalent python testing */
    public static final String SOURCES_TYPE_PYTHON_TEST_SELENIUM = "PYTHON-Tests-Selenium"; // NOI18N

    /**
     * Constant for Project Properties > Sources panel.
     * @since 1.73
     */
    public static final String CUSTOMIZER_SOURCES_IDENT = "SOURCES"; // NOI18N

    /**
     * Constant for Project Properties > Run panel.
     * @since 1.73
     */
    public static final String CUSTOMIZER_RUN_IDENT = "RUN"; // NOI18N

}
