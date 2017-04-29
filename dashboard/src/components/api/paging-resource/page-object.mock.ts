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
import {ChePageObject} from './page-object.factory';
import {PageObjectResource} from './page-object-resource';
import {RemotePageLabels} from './remote-page-labels';

type Object = { id: string, attributes: { name: string } };

/**
 * This class creates mock data.
 *
 * @author Oleksii Orel
 */
export class PageObjectMock {

  private chePageObject: ChePageObject;
  private pageObjectResource: PageObjectResource;
  private maxItems: number;
  private countObjects: number;
  private countPages: number;
  private url: string;
  private pageLabels: Array<string>;

  /**
   * Default constructor
   * @ngInject for Dependency injection
   */
  constructor(chePageObject: ChePageObject, url: string, maxItems: number, countObjects: number) {
    this.chePageObject = chePageObject;
    this.countObjects = countObjects;
    this.maxItems = maxItems;
    this.url = url;

    this.pageObjectResource = chePageObject.createPageObjectResource(url, {});
    this.pageLabels = RemotePageLabels.getValues();
    this.countPages = Math.floor((countObjects - 1) / maxItems) + 1;
  }


  getCountObjects(): number {
    return this.countObjects;
  }

  getUrlRegExp(): RegExp {
    return new RegExp(this.url + '?.*$');
  }

  getMaxItems(): number {
    return this.maxItems;
  }

  getSkipCount(): number {
    return this.pageObjectResource.getRequestDataObject().skipCount;
  }

  getCountPages(): number {
    return this.countPages;
  }

  getPageObjectResource(): PageObjectResource {
    return this.pageObjectResource;
  }

  getPageLabels(): Array<string> {
    return this.pageLabels;
  }
}
