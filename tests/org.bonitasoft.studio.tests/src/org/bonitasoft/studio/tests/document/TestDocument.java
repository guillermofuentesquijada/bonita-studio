/**
 * Copyright (C) 2014 Bonitasoft S.A.
 * Bonitasoft, 32 rue Gustave Eiffel - 38000 Grenoble
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
package org.bonitasoft.studio.tests.document;

import org.assertj.core.api.Assertions;
import org.bonitasoft.engine.bpm.document.DocumentValue;
import org.bonitasoft.studio.common.ExpressionConstants;
import org.bonitasoft.studio.expression.editor.operation.OperatorLabelProvider;
import org.bonitasoft.studio.swtbot.framework.application.BotApplicationWorkbenchWindow;
import org.bonitasoft.studio.swtbot.framework.composite.BotOperationComposite;
import org.bonitasoft.studio.swtbot.framework.diagram.BotProcessDiagramPerspective;
import org.bonitasoft.studio.swtbot.framework.diagram.BotProcessDiagramPropertiesViewFolder;
import org.bonitasoft.studio.swtbot.framework.diagram.general.documents.BotAddDocumentDialog;
import org.bonitasoft.studio.swtbot.framework.diagram.general.documents.BotDocumentsPropertySection;
import org.bonitasoft.studio.swtbot.framework.diagram.general.documents.BotRemoveDocumentDialog;
import org.bonitasoft.studio.swtbot.framework.diagram.general.operations.BotOperationsPropertySection;
import org.bonitasoft.studio.swtbot.framework.expression.BotExpressionEditorDialog;
import org.eclipse.swtbot.eclipse.gef.finder.SWTBotGefTestCase;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class TestDocument extends SWTBotGefTestCase {

    @Test
    public void testAddEditDeleteDocument() {
        final BotDocumentsPropertySection botDocumentsPropertySection = createDiagramAndGoToDocumentSection();
        BotAddDocumentDialog botAddDocumentDialog = botDocumentsPropertySection.addDocument();
        botAddDocumentDialog.setName("doc1");
        botAddDocumentDialog.finish();

        //Edit
        botAddDocumentDialog = botDocumentsPropertySection.editDocument("doc1");
        botAddDocumentDialog.setName("doc1Edited");
        botAddDocumentDialog.chooseExternalInitialContent();
        final boolean isErrorMessageForURLAppeared = botAddDocumentDialog.isErrorMessageUrl();
        botAddDocumentDialog.setURL("http://url-test");
        botAddDocumentDialog.ok();

        //Delete
        final BotRemoveDocumentDialog botRemoveDocumentDialog = botDocumentsPropertySection.removeDocument("doc1Edited");
        botRemoveDocumentDialog.ok();

        Assertions.assertThat(isErrorMessageForURLAppeared).isTrue();
    }

    @Test
    public void testAddEditDeleteMultipleDocument() {
        final BotDocumentsPropertySection botDocumentsPropertySection = createDiagramAndGoToDocumentSection();
        BotAddDocumentDialog botAddDocumentDialog = botDocumentsPropertySection.addDocument();
        botAddDocumentDialog.setName("doc11");
        botAddDocumentDialog.chooseMultileContent();
        botAddDocumentDialog.finish();

        //Edit
        botAddDocumentDialog = botDocumentsPropertySection.editDocument("doc11");
        botAddDocumentDialog.setName("doc11Edited");
        botAddDocumentDialog.setInitialContents("anEmptyScript", "[]");
        botAddDocumentDialog.ok();

        //Delete
        final BotRemoveDocumentDialog botRemoveDocumentDialog = botDocumentsPropertySection.removeDocument("doc11Edited");
        botRemoveDocumentDialog.ok();
    }

    @Test
    public void testErrorMessages() {
        final BotDocumentsPropertySection botDocumentsPropertySection = createDiagramAndGoToDocumentSection();
        BotAddDocumentDialog botAddDocumentDialog = botDocumentsPropertySection.addDocument();
        botAddDocumentDialog.setName("doc1");
        botAddDocumentDialog.finish();

        botAddDocumentDialog = botDocumentsPropertySection.addDocument();
        botAddDocumentDialog.setName("doc1");
        final boolean errorMessageAlreadyExist = botAddDocumentDialog.isErrorMessageAlreadyExist();
        botAddDocumentDialog.setName("");
        final boolean errorMessageNameEmpty = botAddDocumentDialog.isErrorMessageNameEmpty();
        botAddDocumentDialog.setName("doc2");
        botAddDocumentDialog.chooseExternalInitialContent();
        final boolean isErrorMessageForURLAppeared = botAddDocumentDialog.isErrorMessageUrl();
        botAddDocumentDialog.chooseInternalInitialContent();
        final boolean isErrorMessageFileAppeared = botAddDocumentDialog.isErrorMessageFile();

        botAddDocumentDialog.cancel();

        Assertions.assertThat(isErrorMessageForURLAppeared).isTrue();
        Assertions.assertThat(errorMessageAlreadyExist).isTrue();
        Assertions.assertThat(errorMessageNameEmpty).isTrue();
        Assertions.assertThat(isErrorMessageFileAppeared).isTrue();
    }

    @Test
    public void testDocumentOperationSwitch() {
        final BotApplicationWorkbenchWindow botApplicationWorkbenchWindow = new BotApplicationWorkbenchWindow(bot);
        final BotProcessDiagramPerspective botProcessDiagramPerspective = botApplicationWorkbenchWindow.createNewDiagram();
        final BotProcessDiagramPropertiesViewFolder botProcessDiagramPropertiesViewFolder = botProcessDiagramPerspective.getDiagramPropertiesPart();
        final BotDocumentsPropertySection botDocumentsPropertySection = botProcessDiagramPropertiesViewFolder.selectGeneralTab().selectDocumentsTab();
        final BotAddDocumentDialog botAddDocumentDialog = botDocumentsPropertySection.addDocument();
        botAddDocumentDialog.setName("doc1");
        botAddDocumentDialog.finish();

        botProcessDiagramPerspective.drawDiagram().selectElement("Step1");

        final BotOperationsPropertySection botOperationsPropertySection = botProcessDiagramPropertiesViewFolder.selectGeneralTab().selectOperationTab();
        botOperationsPropertySection.addOperation();
        final BotOperationComposite botOperationComposite = botOperationsPropertySection.getOperation(0);
        botOperationComposite.selectLeftOperand("doc1", String.class.getName());
        final String expectedOperator = new OperatorLabelProvider().getText(ExpressionConstants.SET_DOCUMENT_OPERATOR);
        Assertions.assertThat(botOperationComposite.getSelectedOperator()).isEqualTo(expectedOperator);

        final BotExpressionEditorDialog editRightOperand = botOperationComposite.editRightOperand();
        Assertions.assertThat(editRightOperand.selectScriptTab().getReturnType()).isEqualTo(DocumentValue.class.getName());

        editRightOperand.cancel();

    }

    private BotDocumentsPropertySection createDiagramAndGoToDocumentSection() {
        final BotApplicationWorkbenchWindow botApplicationWorkbenchWindow = new BotApplicationWorkbenchWindow(bot);
        final BotProcessDiagramPerspective botProcessDiagramPerspective = botApplicationWorkbenchWindow.createNewDiagram();
        final BotProcessDiagramPropertiesViewFolder botProcessDiagramPropertiesViewFolder = botProcessDiagramPerspective.getDiagramPropertiesPart();
        final BotDocumentsPropertySection botDocumentsPropertySection = botProcessDiagramPropertiesViewFolder.selectGeneralTab().selectDocumentsTab();
        return botDocumentsPropertySection;
    }

}