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
package org.eclipse.che.ide.part.explorer.project.macro;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseProvider;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.macro.Macro;
import org.eclipse.che.ide.api.data.tree.Node;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.part.explorer.project.ProjectExplorerPresenter;
import org.eclipse.che.ide.resources.tree.ResourceNode;

import java.util.List;

/**
 * Provider which is responsible for retrieving the resource's project type from the project explorer.
 * <p>
 * Macro provided: <code>${explorer.current.project.type}</code>
 *
 * @author Vlad Zhukovskyi
 * @see Macro
 * @see ProjectExplorerPresenter
 * @since 4.7.0
 */
@Beta
@Singleton
public class ExplorerCurrentProjectTypeMacro implements Macro {

    public static final String KEY = "${explorer.current.project.type}";

    private ProjectExplorerPresenter projectExplorer;
    private PromiseProvider          promises;
    private final CoreLocalizationConstant localizationConstants;

    @Inject
    public ExplorerCurrentProjectTypeMacro(ProjectExplorerPresenter projectExplorer,
                                           PromiseProvider promises,
                                           CoreLocalizationConstant localizationConstants) {
        this.projectExplorer = projectExplorer;
        this.promises = promises;
        this.localizationConstants = localizationConstants;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return KEY;
    }

    @Override
    public String getDescription() {
        return localizationConstants.macroExplorerCurrentProjectTypeDescription();
    }

    /** {@inheritDoc} */
    @Override
    public Promise<String> expand() {

        List<Node> selectedNodes = projectExplorer.getTree().getSelectionModel().getSelectedNodes();

        if (selectedNodes.isEmpty() || selectedNodes.size() > 1) {
            return promises.resolve("");
        }

        final Node node = selectedNodes.get(0);

        if (node instanceof ResourceNode) {
            final Optional<Project> project = ((ResourceNode)node).getData().getRelatedProject();

            if (!project.isPresent()) {
                return promises.resolve("");
            }

            return promises.resolve(project.get().getType());
        }

        return promises.resolve("");
    }
}
