/**
 * Copyright (C) 2010 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.studio.engine.test;

import junit.framework.TestCase;

import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.bpm.flownode.AutomaticTaskDefinition;
import org.bonitasoft.studio.common.repository.Repository;
import org.bonitasoft.studio.common.repository.RepositoryManager;
import org.bonitasoft.studio.common.repository.model.IRepository;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;

/**
 * @author Romain Bioteau
 *
 */
public class TestJavaDoc extends TestCase {

    public void testHasJavaDoc() throws CoreException {
    	
        final IRepository currentRepository = RepositoryManager.getInstance().getCurrentRepository();
		final IJavaProject javaProject = currentRepository.getJavaProject();
        final String javadoc = javaProject.findType(AutomaticTaskDefinition.class.getName()).getAttachedJavadoc(Repository.NULL_PROGRESS_MONITOR);
        assertNotNull("JavaDoc not working for bonita-common", javadoc);
        assertTrue(javadoc.contains("An AutomaticTask is a concrete implementation of a Task. It requires no human interactions."));

        final String javadocClient = javaProject.findType(TenantAPIAccessor.class.getName()).getAttachedJavadoc(Repository.NULL_PROGRESS_MONITOR);
        assertNotNull("JavaDoc not working for bonita-client", javadocClient);
        assertTrue(javadocClient.contains("Matthieu Chaffotte"));
    }
}
