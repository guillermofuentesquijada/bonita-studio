/**
 * Copyright (C) 2012 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
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
package org.bonitasoft.studio.connectors.repository;

import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.bonitasoft.studio.common.repository.model.IRepository;
import org.bonitasoft.studio.connector.model.definition.AbstractDefinitionRepositoryStore;
import org.bonitasoft.studio.connector.model.definition.Category;
import org.bonitasoft.studio.connector.model.i18n.DefinitionResourceProvider;
import org.bonitasoft.studio.connectors.ConnectorPlugin;
import org.bonitasoft.studio.connectors.i18n.Messages;
import org.bonitasoft.studio.pics.Pics;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.edapt.migration.MigrationException;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;


/**
 * @author Romain Bioteau
 * @author Baptiste Mesta
 * This extends SourceRepositoryStore in order to find message resources
 */
public class ConnectorDefRepositoryStore extends AbstractDefinitionRepositoryStore<ConnectorDefFileStore>{

	public static final String STORE_NAME = "connectors-def";
	private static final Set<String> extensions = new HashSet<String>() ;
	public static final String CONNECTOR_DEF_EXT = "def";
	static{
		extensions.add(CONNECTOR_DEF_EXT) ;
	}


	private DefinitionResourceProvider resourceProvider;

	@Override
	public void createRepositoryStore(IRepository repository) {
		super.createRepositoryStore(repository);
		final ConnectorPlugin plugin = ConnectorPlugin.getDefault();
		final Bundle bundle = plugin.getBundle();
		resourceProvider = DefinitionResourceProvider.getInstance(this,bundle);
		resourceProvider.loadDefinitionsCategories(null);
	}

	@Override
	public ConnectorDefFileStore createRepositoryFileStore(String fileName) {
		if(fileName.endsWith(CONNECTOR_DEF_EXT)){
			return new ConnectorDefFileStore(fileName, this);
		}
		return null;
	}

	public DefinitionResourceProvider getResourceProvider() {
		return resourceProvider;
	}

	@Override
	protected ConnectorDefFileStore doImportInputStream(String fileName, InputStream inputStream) {
		ConnectorDefFileStore definition = super.doImportInputStream(fileName, inputStream);
		if(definition != null){
			final DefinitionResourceProvider resourceProvider = DefinitionResourceProvider.getInstance(this,getBundle());
			reloadCategories(definition.getContent(), resourceProvider);
		}
		return definition;
	}

	private void reloadCategories(org.bonitasoft.studio.connector.model.definition.ConnectorDefinition definition,DefinitionResourceProvider messageProvider) {
		boolean reloadCategories = false ;
		for(Category c : definition.getCategory()){
			if(!messageProvider.getAllCategories().contains(c)){
				reloadCategories = true ;
				break;
			}
		}
		if(reloadCategories){
			messageProvider.loadDefinitionsCategories(null);
		}
	}


	@Override
	public String getName() {
		return STORE_NAME ;
	}

	@Override
	public String getDisplayName() {
		return Messages.connectorDefRepositoryName;
	}

	@Override
	public Image getIcon() {
		return Pics.getImage("connector.png",ConnectorPlugin.getDefault());
	}

	@Override
	public Set<String> getCompatibleExtensions() {
		return extensions;
	}


	@Override
	protected ConnectorDefFileStore getDefFileStore(URL url) {
		return new URLConnectorDefFileStore(url, this);
	}


	@Override
	protected Bundle getBundle() {
		return ConnectorPlugin.getDefault().getBundle();
	}

	@Override
	public void migrate() throws CoreException, MigrationException {
		super.migrate();
		resourceProvider.loadDefinitionsCategories(null);
	}
}
