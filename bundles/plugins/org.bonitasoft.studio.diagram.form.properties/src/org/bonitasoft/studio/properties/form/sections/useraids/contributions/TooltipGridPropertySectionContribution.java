/**
 * Copyright (C) 2009-2012 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
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
package org.bonitasoft.studio.properties.form.sections.useraids.contributions;

import org.bonitasoft.studio.common.ExpressionConstants;
import org.bonitasoft.studio.common.properties.ExtensibleGridPropertySection;
import org.bonitasoft.studio.common.properties.IExtensibleGridPropertySectionContribution;
import org.bonitasoft.studio.expression.editor.filter.AvailableExpressionTypeFilter;
import org.bonitasoft.studio.expression.editor.viewer.ExpressionViewer;
import org.bonitasoft.studio.form.properties.i18n.Messages;
import org.bonitasoft.studio.model.expression.Expression;
import org.bonitasoft.studio.model.expression.ExpressionFactory;
import org.bonitasoft.studio.model.form.FormButton;
import org.bonitasoft.studio.model.form.FormField;
import org.bonitasoft.studio.model.form.FormPackage;
import org.bonitasoft.studio.model.form.Group;
import org.bonitasoft.studio.model.form.HiddenWidget;
import org.bonitasoft.studio.model.form.IFrameWidget;
import org.bonitasoft.studio.model.form.ImageWidget;
import org.bonitasoft.studio.model.form.Info;
import org.bonitasoft.studio.model.form.Table;
import org.bonitasoft.studio.model.form.Widget;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

/**
 * @author Aurelien Pupier
 * @author Baptiste Mesta
 *
 */
public class TooltipGridPropertySectionContribution implements
IExtensibleGridPropertySectionContribution {

    private Widget element;
    private TransactionalEditingDomain editingDomain;
    private EMFDataBindingContext dataBindingContext;

    /* (non-Javadoc)
     * @see org.bonitasoft.studio.common.properties.IExtensibleGridPropertySectionContribution#createControl(org.eclipse.swt.widgets.Composite, org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory, org.bonitasoft.studio.common.properties.ExtensibleGridPropertySection)
     */
    public void createControl(Composite composite,
            TabbedPropertySheetWidgetFactory widgetFactory,
            ExtensibleGridPropertySection extensibleGridPropertySection) {
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        composite.setLayout(GridLayoutFactory.fillDefaults().numColumns(1).margins(20, 0).create());
        if(dataBindingContext != null){
            dataBindingContext.dispose();
        }
        dataBindingContext = new EMFDataBindingContext();
        ExpressionViewer tooltipViewer = new ExpressionViewer(composite, SWT.BORDER, widgetFactory,editingDomain, FormPackage.Literals.WIDGET__TOOLTIP);
        tooltipViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create()) ;
        Expression tooltipexpression = element.getTooltip();
        if(tooltipexpression == null){
            tooltipexpression = ExpressionFactory.eINSTANCE.createExpression();
            editingDomain.getCommandStack().execute(SetCommand.create(editingDomain, element, FormPackage.Literals.WIDGET__TOOLTIP, tooltipexpression));
        }
        dataBindingContext.bindValue(
                ViewerProperties.singleSelection().observe(tooltipViewer),
                EMFEditProperties.value(editingDomain, FormPackage.Literals.WIDGET__TOOLTIP).observe(element));


        tooltipViewer.addFilter(new AvailableExpressionTypeFilter(new String[]{ExpressionConstants.CONSTANT_TYPE,ExpressionConstants.VARIABLE_TYPE,ExpressionConstants.PARAMETER_TYPE,ExpressionConstants.SCRIPT_TYPE}));


        tooltipViewer.setInput(element);
        tooltipViewer.setMessage(Messages.UserAidsSection_Tooltip_tooltip,IStatus.INFO) ;
    }

    /* (non-Javadoc)
     * @see org.bonitasoft.studio.common.properties.IExtensibleGridPropertySectionContribution#getLabel()
     */
    public String getLabel() {
        return Messages.UserAidsSection_Tooltip;
    }

    /* (non-Javadoc)
     * @see org.bonitasoft.studio.common.properties.IExtensibleGridPropertySectionContribution#isRelevantFor(org.eclipse.emf.ecore.EObject)
     */
    public boolean isRelevantFor(EObject eObject) {
        return
                !(eObject instanceof HiddenWidget) &&
                !(eObject instanceof IFrameWidget) &&
                !(eObject instanceof Group) &&
                (
                        eObject instanceof FormField  ||
                        eObject instanceof ImageWidget ||
                        eObject instanceof FormButton ||
                        eObject instanceof Info ||
                        eObject instanceof Table
                        );
    }

    /* (non-Javadoc)
     * @see org.bonitasoft.studio.common.properties.IExtensibleGridPropertySectionContribution#refresh()
     */
    public void refresh() {

    }

    /* (non-Javadoc)
     * @see org.bonitasoft.studio.common.properties.IExtensibleGridPropertySectionContribution#setEObject(org.eclipse.emf.ecore.EObject)
     */
    public void setEObject(EObject object) {
        element = (Widget) object;
    }

    /* (non-Javadoc)
     * @see org.bonitasoft.studio.common.properties.IExtensibleGridPropertySectionContribution#setEditingDomain(org.eclipse.emf.transaction.TransactionalEditingDomain)
     */
    public void setEditingDomain(TransactionalEditingDomain editingDomain) {
        this.editingDomain = editingDomain;
    }

    /* (non-Javadoc)
     * @see org.bonitasoft.studio.common.properties.IExtensibleGridPropertySectionContribution#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    public void setSelection(ISelection selection) {
        // NOTHING

    }

    /* (non-Javadoc)
     * @see org.bonitasoft.studio.common.properties.IExtensibleGridPropertySectionContribution#dispose()
     */
    public void dispose() {
        if(dataBindingContext != null) {
            dataBindingContext.dispose();
        }
    }
}
