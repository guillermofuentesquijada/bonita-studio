/**
 * Copyright (C) 2012 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.studio.common.repository.model;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.edapt.migration.MigrationException;
import org.eclipse.swt.graphics.Image;

/**
 * @author Romain Bioteau
 */
public interface IRepositoryStore<T extends IRepositoryFileStore> {

    void createRepositoryStore(IRepository repository);

    T createRepositoryFileStore(String fileName);

    T importInputStream(String fileName, InputStream inputStream);

    T importIResource(String fileName, IResource resource);

    String getName();

    String getDisplayName();

    Image getIcon();

    IFolder getResource();

    T getChild(String fileName);

    List<T> getChildren();

    boolean isShared();

    boolean canBeShared();

    boolean canBeExported();

    Set<String> getCompatibleExtensions();

    void refresh();

    void migrate(IProgressMonitor monitor) throws CoreException, MigrationException;

    void close();

}
