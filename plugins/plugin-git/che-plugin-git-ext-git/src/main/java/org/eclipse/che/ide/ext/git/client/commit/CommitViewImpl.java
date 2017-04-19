/*******************************************************************************
 * Copyright (c) 2012-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.git.client.commit;

import org.eclipse.che.ide.ext.git.client.GitLocalizationConstant;
import org.eclipse.che.ide.ext.git.client.GitResources;
import org.eclipse.che.ide.ui.ShiftableTextArea;
import org.eclipse.che.ide.ui.window.Window;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * The implementation of {@link CommitView}.
 *
 * @author Andrey Plotnikov
 */
@Singleton
public class CommitViewImpl extends Window implements CommitView {
    interface CommitViewImplUiBinder extends UiBinder<Widget, CommitViewImpl> {
    }

    private static CommitViewImplUiBinder ourUiBinder = GWT.create(CommitViewImplUiBinder.class);

    /**
     * The add all uncommited change field.
     */
    @UiField
    CheckBox addAll;
    /**
     * The 'add to index selected files' selection in commit field.
     */
    @UiField
    CheckBox addSelection;
    /**
     * The 'commit only selection' field.
     */
    @UiField
    CheckBox commitAllSelection;

    /**
     * The amend commit flag.
     */
    @UiField
    CheckBox amend;

    /**
     * The commit message input field.
     */
    @UiField(provided = true)
    TextArea message;
    Button btnCommit;
    Button btnCancel;
    @UiField(provided = true)
    final   GitResources            res;
    @UiField(provided = true)
    final   GitLocalizationConstant locale;
    private ActionDelegate          delegate;

    /**
     * Create view.
     *
     * @param res
     * @param locale
     */
    @Inject
    protected CommitViewImpl(GitResources res, GitLocalizationConstant locale) {
        this.res = res;
        this.locale = locale;
        this.message = new ShiftableTextArea();
        this.ensureDebugId("git-commit-window");

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setTitle(locale.commitTitle());
        this.setWidget(widget);

//        NodeStorage nodeStorage = new NodeStorage();
//        NodeLoader nodeLoader = new NodeLoader();
//        tree = new Tree(nodeStorage, nodeLoader);
//        tree.getSelectionModel().setSelectionMode(SelectionModel.Mode.SINGLE);
//        tree.getSelectionModel().addSelectionChangedHandler(new SelectionChangedEvent.SelectionChangedHandler() {
//            @Override
//            public void onSelectionChanged(SelectionChangedEvent event) {
//                List<Node> selection = event.getSelection();
//                if (!selection.isEmpty()) {
//                    delegate.onNodeSelected(selection.get(0));
//                }
//            }
//        });
//        tree.setPresentationRenderer(new ChangedListRender(tree.getTreeStyles()));
//        changedFilesPanel.add(tree);

        btnCancel = createButton(locale.buttonCancel(), "git-commit-cancel", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        btnCommit = createButton(locale.buttonCommit(), "git-commit-commit", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCommitClicked();
            }
        });
        btnCommit.addStyleName(resources.windowCss().primaryButton());

        addButtonToFooter(btnCommit);
        addButtonToFooter(btnCancel);
    }

//    private class ChangedListRender extends DefaultPresentationRenderer<Node> {
//        ChangedListRender(TreeStyles treeStyles) {
//            super(treeStyles);
//        }
//
//        @Override
//        public Element render(Node node, String domID, Tree.Joint joint, int depth) {
//            NodePresentation presentation;
//            if (node instanceof HasPresentation) {
//                presentation = ((HasPresentation)node).getPresentation(false);
//            } else {
//                presentation = new NodePresentation();
//                presentation.setPresentableText(node.getName());
//            }
//
//            Element rootContainer = getRootContainer(domID);
//
//            Element nodeContainer = getNodeContainer();
//
//            nodeContainer.getStyle().setPaddingLeft((double)depth * 16, Style.Unit.PX);
//
//            Element jointContainer = getJointContainer(joint);
//
//            Element iconContainer = getIconContainer(presentation.getPresentableIcon());
//
//            Element userElement = getUserElement(presentation.getUserElement());
//
//            Element presentableTextContainer = getPresentableTextContainer(createPresentableTextElement(presentation));
//
//            Element infoTextContainer = getInfoTextContainer(createInfoTextElement(presentation));
//
//            Element descendantsContainer = getDescendantsContainer();
//
//            nodeContainer.appendChild(jointContainer);
//            nodeContainer.appendChild(iconContainer);
//            nodeContainer.appendChild(new CheckBox().getElement());
//            nodeContainer.appendChild(userElement == null ? Document.get().createSpanElement() : userElement);
//            nodeContainer.appendChild(presentableTextContainer);
//            nodeContainer.appendChild(infoTextContainer);
//
//            rootContainer.appendChild(nodeContainer);
//            rootContainer.appendChild(descendantsContainer);
//
//            return rootContainer;
//        }
//
//        private Element createInfoTextElement(NodePresentation presentation) {
//            DivElement textElement = Document.get().createDivElement();
//
//            StringBuilder sb = new StringBuilder();
//
//            if (presentation.getInfoTextWrapper() != null) {
//                sb.append(presentation.getInfoTextWrapper().first);
//            }
//
//            if (!Strings.isNullOrEmpty(presentation.getInfoText())) {
//                sb.append(presentation.getInfoText());
//            }
//
//            if (presentation.getInfoTextWrapper() != null) {
//                sb.append(presentation.getInfoTextWrapper().second);
//            }
//
//            textElement.setInnerText(sb.toString());
//            textElement.setAttribute("style", presentation.getInfoTextCss());
//
//            //TODO support text colorization
//
//            return textElement;
//        }
//
//        private Element createPresentableTextElement(NodePresentation presentation) {
//            DivElement textElement = Document.get().createDivElement();
//
//            textElement.setInnerText(Strings.nullToEmpty(presentation.getPresentableText()));
//            textElement.setAttribute("style", presentation.getPresentableTextCss());
//
//            //TODO support text colorization
//
//            return textElement;
//        }
//    }

    @Override
    protected void onEnterClicked() {
        if (isWidgetFocused(btnCommit)) {
            delegate.onCommitClicked();
            return;
        }

        if (isWidgetFocused(btnCancel)) {
            delegate.onCancelClicked();
        }
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getMessage() {
        return message.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setMessage(@NotNull String message) {
        this.message.setText(message);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAddAllExceptNew() {
        return this.addAll.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setAddAllExceptNew(boolean isAddAllExceptNew) {
        this.addAll.setValue(isAddAllExceptNew);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAmend() {
        return amend.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setAmend(boolean isAmend) {
        amend.setValue(isAmend);
    }

    @Override
    public boolean isAddSelectedFiles() {
        return this.addSelection.getValue();
    }

    @Override
    public void setAddSelectedFiles(final boolean includeSelection) {
        this.addSelection.setValue(includeSelection);
    }

    @Override
    public boolean isCommitAllFiles() {
        return this.commitAllSelection.getValue();
    }

    @Override
    public void setCommitAllFiles(final boolean onlySelection) {
        this.commitAllSelection.setValue(onlySelection);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableCommitButton(boolean enable) {
        btnCommit.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void focusInMessageField() {
        new Timer() {
            @Override
            public void run() {
                message.setFocus(true);
            }
        }.schedule(300);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("message")
    public void onMessageChanged(KeyUpEvent event) {
        delegate.onValueChanged();
    }

    @UiHandler("addAll")
    public void onAddAllValueChange(final ValueChangeEvent<Boolean> event) {
        if (event.getValue()) {
            this.addSelection.setValue(false);
        }
    }

    @UiHandler("addSelection")
    public void onAddSelectionValueChange(final ValueChangeEvent<Boolean> event) {
        if (event.getValue()) {
            this.addAll.setValue(false);
        }
    }

    @UiHandler("commitAllSelection")
    public void onOnlySelectionValueChange(final ValueChangeEvent<Boolean> event) {
        this.commitAllSelection.setValue(event.getValue());
    }

    @UiHandler("amend")
    public void onAmendValueChange(final ValueChangeEvent<Boolean> event) {
        if (event.getValue()) {
            this.delegate.setAmendCommitMessage();
        } else {
            this.message.setValue("");
            this.setEnableCommitButton(false);
        }
    }
}
