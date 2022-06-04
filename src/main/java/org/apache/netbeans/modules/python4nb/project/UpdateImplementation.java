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

import java.io.IOException;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.w3c.dom.Element;

// Portions of this code are based on nbPython Code.  
/**
 * Represents an implementation of project update. based on UpdateImplementation
 * (java.common.api) When the update support will be moved into ant based
 * project type from common java api this class can be removed.
 */
public interface UpdateImplementation {

    /**
     * Return <code>true</code> if the project is of current version.
     *
     * @return <code>true</code> if the project is of current version.
     */
    boolean isCurrent();

    /**
     * Return <code>true</code> if the project can be updated.
     *
     * @return <code>true</code> if the project can be updated.
     */
    boolean canUpdate();

    /**
     * Saving of update. If the project is of current version it should probably
     * do nothing.
     *
     * @param props project properties to be saved, can be <code>null</code>.
     * There's no need to save them because {@link UpdateHelper} does it.
     * @throws IOException if error occurs during saving.
     */
    void saveUpdate(final EditableProperties props) throws IOException;

    /**
     * Creates probably an in memory update of shared configuration data and
     * return it.
     *
     * @return the configuration data that is available.
     * @see {@link UpdateHelper#getPrimaryConfigurationData(boolean)}
     */
    Element getUpdatedSharedConfigurationData();

    /**
     * Creates probably an in memory update of project properties.
     *
     * @return a set of properties.
     * @see {@link UpdateHelper#getProperties(String)}
     */
    EditableProperties getUpdatedProjectProperties();

    public static final UpdateImplementation NULL = new UpdateImplementation() {

        @Override
        public boolean isCurrent() {
            return true;
        }

        @Override
        public boolean canUpdate() {
            return false;
        }

        @Override
        public void saveUpdate(EditableProperties props) throws IOException {
            throw new UnsupportedOperationException("Nothing to do");
        }

        @Override
        public Element getUpdatedSharedConfigurationData() {
            throw new UnsupportedOperationException("Nothing to do");
        }

        @Override
        public EditableProperties getUpdatedProjectProperties() {
            throw new UnsupportedOperationException("Nothing to do");
        }
    };
}
