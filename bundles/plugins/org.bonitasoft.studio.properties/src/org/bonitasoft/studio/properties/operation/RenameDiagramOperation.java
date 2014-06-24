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
package org.bonitasoft.studio.properties.operation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.studio.common.OpenNameAndVersionForDiagramDialog.ProcessesNameVersion;
import org.bonitasoft.studio.common.emf.tools.ModelHelper;
import org.bonitasoft.studio.common.repository.Repository;
import org.bonitasoft.studio.common.repository.RepositoryManager;
import org.bonitasoft.studio.diagram.custom.operation.DuplicateDiagramOperation;
import org.bonitasoft.studio.diagram.custom.repository.DiagramFileStore;
import org.bonitasoft.studio.diagram.custom.repository.DiagramRepositoryStore;
import org.bonitasoft.studio.model.form.Form;
import org.bonitasoft.studio.model.form.FormPackage;
import org.bonitasoft.studio.model.process.MainProcess;
import org.bonitasoft.studio.properties.i18n.Messages;
import org.bonitasoft.studio.properties.sections.forms.FormsUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

/**
 * @author Romain Bioteau
 */
public class RenameDiagramOperation implements IRunnableWithProgress {

    private MainProcess diagram;
    private String diagramVersion;
    private String diagramName;
    private List<ProcessesNameVersion> pools = new ArrayList<ProcessesNameVersion>();
    private DiagramEditor editor;
    private boolean saveAfterRename = true;

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        Assert.isNotNull(diagram);
        Assert.isNotNull(diagramVersion);
        Assert.isNotNull(diagramName);
        Assert.isNotNull(editor);
        monitor.beginTask(Messages.renamingDiagram, IProgressMonitor.UNKNOWN);

        final String oldName = diagram.getName();
        final String oldVersion = diagram.getVersion();

        final String partName = editor.getPartName();
        final DiagramRepositoryStore diagramStore = RepositoryManager.getInstance().getRepositoryStore(DiagramRepositoryStore.class);

        final List<Form> forms = getFormsToReopen(editor);
        final DuplicateDiagramOperation operation = new DuplicateDiagramOperation();
        operation.setDiagramToDuplicate(diagram);
        operation.setNewDiagramName(diagramName);
        operation.setNewDiagramVersion(diagramVersion);
        operation.setPoolsRenamed(pools);
        operation.run(Repository.NULL_PROGRESS_MONITOR);

        if (saveAfterRename) {
            save();
        }

        if (!(oldName.equals(diagramName) && oldVersion.equals(diagramVersion))) {
            final DiagramFileStore diagramFileStore = diagramStore.getDiagram(oldName, oldVersion);
            diagramFileStore.getOpenedEditor().doSave(Repository.NULL_PROGRESS_MONITOR);
            diagramFileStore.delete();
        }

        final DiagramFileStore fStore = diagramStore.getDiagram(diagramName, diagramVersion);
        IWorkbenchPart partToActivate = fStore.open();
        final MainProcess mainProcess = fStore.getContent();
        for (final Form form : forms) {
            final List<Form> allItemsOfTypeForms = ModelHelper.getAllItemsOfType(mainProcess, FormPackage.Literals.FORM);
            for (final Form f : allItemsOfTypeForms) {
                if (EcoreUtil.equals(form, f)) {
                    final DiagramEditor ed = FormsUtils.openDiagram(f, AdapterFactoryEditingDomain.getEditingDomainFor(f));
                    if (partName.equals(ed.getTitle())) {
                        partToActivate = ed;
                    }
                }
            }
        }
        partToActivate.getSite().getPage().activate(partToActivate);
    }


    private void save() throws InvocationTargetException {
        try {
            final ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
            final org.eclipse.core.commands.Command c = service.getCommand("org.eclipse.ui.file.save");
            if (c.isEnabled()) {
                c.executeWithChecks(new ExecutionEvent());
            }
        } catch (final Exception e) {
            throw new InvocationTargetException(e);
        }
    }

    private List<Form> getFormsToReopen(final DiagramEditor editor) {
        final List<Form> formsToReopen = new ArrayList<Form>();
        final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        final IResource diagramResource = (IResource) editor.getEditorInput().getAdapter(IResource.class);
        if (activeWorkbenchWindow != null && activeWorkbenchWindow.getActivePage() != null) {
            final IEditorReference[] editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
            // look for the resource in other editors
            for (final IEditorReference iEditorReference : editors) {
                try {
                    final IEditorInput input = iEditorReference.getEditorInput();
                    final IResource iResource = (IResource) input.getAdapter(IResource.class);
                    if (diagramResource.equals(iResource)) {
                        final IWorkbenchPart part = iEditorReference.getPart(false);
                        if (part != null && part instanceof DiagramDocumentEditor) {
                            final EObject root = ((DiagramDocumentEditor) part).getDiagramEditPart().resolveSemanticElement();
                            if (root instanceof Form) {
                                formsToReopen.add(EcoreUtil.copy((Form) root));
                            }
                        }
                    }
                } catch (final PartInitException e) {
                    // no input? -> nothing to do
                }
            }
        }
        return formsToReopen;
    }

    public void setDiagramToDuplicate(final MainProcess diagram) {
        this.diagram = diagram;
    }

    public void setNewDiagramName(final String diagramName) {
        this.diagramName = diagramName;
    }

    public void setPoolsRenamed(final List<ProcessesNameVersion> pools) {
        this.pools = pools;
    }

    public void setNewDiagramVersion(final String diagramVersion) {
        this.diagramVersion = diagramVersion;
    }

    public void setEditor(final DiagramEditor editor) {
        this.editor = editor;
    }

    public void setSaveAfterRename(final boolean saveAfterRename) {
        this.saveAfterRename = saveAfterRename;
    }

}
