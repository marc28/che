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
package org.eclipse.che.ide.ext.git.client.tree;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.ide.api.data.tree.Node;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.ext.git.client.GitLocalizationConstant;
import org.eclipse.che.ide.ext.git.client.GitResources;
import org.eclipse.che.ide.ext.git.client.compare.ComparePresenter;
import org.eclipse.che.ide.ext.git.client.compare.FileStatus.Status;
import org.eclipse.che.ide.project.shared.NodesResources;
import org.eclipse.che.ide.ui.smartTree.Tree;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Presenter for displaying list of changed files.
 *
 * @author Igor Vinokur
 * @author Vlad Zhukovskyi
 */
//@Singleton
public class TreePresenter implements TreeView.ActionDelegate {
    private final TreeView                view;
    private final GitLocalizationConstant locale;

    private Map<String, Status> changedFiles;
    private TreeCallBack        callBack;
    private boolean             treeViewEnabled;

    @Inject
    public TreePresenter(GitResources resources, GitLocalizationConstant locale, NodesResources nodesResources) {
        this.view = new TreeViewImpl(resources, locale, nodesResources);
        this.locale = locale;
        this.view.setDelegate(this);
    }

    /**
     * Show window with changed files.
     *
     * @param changedFiles
     *         Map with files and their status
     */
    public void show(Map<String, Status> changedFiles,
                     TreeCallBack callBack) {
        this.changedFiles = changedFiles;
        this.callBack = callBack;

        view.setEnableExpandCollapseButtons(treeViewEnabled);


        viewChangedFiles();
    }

    public TreeView getView() {
        return view;
    }

    public Set<String> getUnselected() {
        Set<String> unselected = new HashSet<>();
        for (Node node : view.getNodes()) {
            if (((ChangedNode)node).isSelected()) {
                unselected.add(node.getName());
            }
        }
        return unselected;
    }

    @Override
    public void onFileNodeDoubleClicked() {
        callBack.onFileNodeDoubleClicked();
    }

    @Override
    public void onFileNodeCheckBoxValueChanged(Node node) {
        ((ChangedNode)node).setSelected(!((ChangedNode)node).isSelected());
    }

    @Override
    public void onChangeViewModeButtonClicked() {
        treeViewEnabled = !treeViewEnabled;
        viewChangedFiles();
        view.setEnableExpandCollapseButtons(treeViewEnabled);
    }

    @Override
    public void onExpandButtonClicked() {
        view.expandAllDirectories();
    }

    @Override
    public void onCollapseButtonClicked() {
        view.collapseAllDirectories();
    }

    @Override
    public void onNodeSelected(@NotNull Node node) {
        callBack.onNodeSelected(node);
    }

    private void viewChangedFiles() {
        if (treeViewEnabled) {
            view.viewChangedFilesAsTree(changedFiles);
            view.setTextToChangeViewModeButton(locale.changeListRowListViewButtonText());
        } else {
            view.viewChangedFilesAsList(changedFiles);
            view.setTextToChangeViewModeButton(locale.changeListGroupByDirectoryButtonText());
        }
    }
}
