/**
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
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
package org.bonitasoft.studio.exporter.bpmn.transfo.data;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.studio.exporter.bpmn.transfo.IScope;
import org.bonitasoft.studio.model.process.Data;
import org.bonitasoft.studio.model.process.DataAware;
import org.omg.spec.bpmn.model.TItemDefinition;

/**
 * @author Romain Bioteau
 */
public class DataScope implements IScope<Data, TItemDefinition> {

    private Map<Data, TItemDefinition> dataStore;
    private final ItemDefinitionTransformer itemDefinitionTransformer;

    public DataScope(final ItemDefinitionTransformer itemDefinitionTransformer) {
        this.itemDefinitionTransformer = itemDefinitionTransformer;
    }

    public void initializeContext(final DataAware dataAwareContext) {
        dataStore = new HashMap<Data, TItemDefinition>();
        for (final Data item : dataAwareContext.getData()) {
            dataStore.put(item, itemDefinitionTransformer.transform(item));
        }
    }

    @Override
    public TItemDefinition get(final Data data) {
        checkNotNull(dataStore);
        return dataStore.get(data);
    }

}
