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
package org.apache.netbeans.modules.python4nb.editor.file;

/**
 *
 * @author ebres
 */
public class MIMETypes {

      private MIMETypes() {
    throw new IllegalStateException("Attempt to used constuctor for MIMETypes Utility class");
  }
    // based on mimi types defined with https://github.com/python/cpython/blob/3.10/Lib/mimetypes.py
    public static final String PY = "text/x-python";
    public static final String PYC = "application/x-python-code";
    public static final String PYO = "application/x-python-code";
    
    public static final String PY_EXT = "py";
    public static final String PYC_EXT = "pyc";
    public static final String PYO_EXT = "pyo";
    
    public static final String PYTHON_MIME_TYPE = "text/x-python"; // NOI18N
    public static final String PYTHON_EXTENSION ="py";
}
