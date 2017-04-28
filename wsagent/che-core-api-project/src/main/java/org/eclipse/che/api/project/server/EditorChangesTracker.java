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
package org.eclipse.che.api.project.server;

import org.eclipse.che.api.core.jsonrpc.RequestHandlerConfigurator;
import org.eclipse.che.api.project.shared.dto.EditorChangesDto;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.function.BiConsumer;

/**
 *
 *
 * @author Roman Nikitenko
 */
@Singleton
public class EditorChangesTracker {
    private static final String INCOMING_METHOD = "track:editor-changes";

    private EditorWorkingCopyManager editorWorkingCopyManager;

    @Inject
    public EditorChangesTracker(EditorWorkingCopyManager editorWorkingCopyManager) {
        this.editorWorkingCopyManager = editorWorkingCopyManager;
    }

    @Inject
    public void configureHandler(RequestHandlerConfigurator configurator) {
        configurator.newConfiguration()
                    .methodName(INCOMING_METHOD)
                    .paramsAsDto(EditorChangesDto.class)
                    .noResult()
                    .withConsumer(getEditorChangesConsumer());
    }

    private BiConsumer<String, EditorChangesDto> getEditorChangesConsumer() {
        return editorWorkingCopyManager::onEditorContentUpdated;
    }
}
