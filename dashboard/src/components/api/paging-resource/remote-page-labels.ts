/*
 * Copyright (c) 2015-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 */
'use strict';

/**
 * This is class of remote page labels.
 *
 * @author Oleksii Orel
 */
export class RemotePageLabels {

  static get FIRST(): string {
    return 'first';
  }
  static get PREVIOUS(): string {
    return 'prev';
  }
  static get NEXT(): string {
    return 'next';
  }
  static get LAST(): string {
    return 'last';
  }
}
