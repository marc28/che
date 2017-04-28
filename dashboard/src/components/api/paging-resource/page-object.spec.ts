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
import {CheHttpBackend} from '../test/che-http-backend';
import {CheAPIBuilder} from '../builder/che-api-builder.factory';
import {ChePageObject} from './page-object.factory';

describe('PageObject >', () => {
  /**
   * Page object factory for the test
   */
  let factory: ChePageObject;
  /**
   * API builder.
   */
  let apiBuilder: CheAPIBuilder;
  /**
   * Backend for handling http operations
   */
  let $httpBackend: ng.IHttpBackendService;
  /**
   * Che backend
   */
  let cheBackend: CheHttpBackend;

  /**
   *  setup module
   */
  beforeEach(angular.mock.module('userDashboard'));

  /**
   * Inject factory and http backend
   */
  beforeEach(inject((chePageObject: ChePageObject, cheAPIBuilder: CheAPIBuilder, cheHttpBackend: CheHttpBackend) => {
    factory = chePageObject;
    apiBuilder = cheAPIBuilder;
    cheBackend = cheHttpBackend;
    $httpBackend = cheHttpBackend.getHttpBackend();

    cheBackend.factoriesBackendSetup();
  }));

  /**
   * Check assertion after the test
   */
  afterEach(() => {
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });

  it('Fetch page objects without header link data', () => {
    const testMaxItems = 15;
    const url = '/api/object';
    const objects: { id: string; attributes: { name: string } } = [];
    const pageObjectResource = factory.createPageObjectResource(url, {});

    for (let n = 0; n < testMaxItems; n++) {
      objects.push({id: `testId_${n}`, attributes: {name: `testName${n}`}});
    }

    pageObjectResource.fetchObjects(testMaxItems);

    // make response for object list
    $httpBackend.expect('GET', new RegExp(url + '?.*$')).respond(200, objects);

    // gets page params
    const {countPages, currentPageNumber} = pageObjectResource.getPagesInfo();
    const {maxItems, skipCount} = pageObjectResource.getRequestDataObject();
    const pageObjects = pageObjectResource.getPageObjects();
    const testSkipCount = 0;

    $httpBackend.flush();

    // check objects
    expect(maxItems).toEqual(testMaxItems.toLocaleString());
    expect(skipCount).toEqual(testSkipCount.toLocaleString());
    expect(currentPageNumber).toEqual(1);
    expect(countPages).toEqual(1);
    expect(pageObjects).toEqual(objects);
  });

  it('Fetch page objects for the first, next and last pages. Then - preview and return to the first', () => {
    const testMaxItems = 15;
    const countObjects = 50;
    const url = '/api/object';
    const objects: { id: string; attributes: { name: string } } = [];
    const keys = ['first', 'next', 'last', 'prev'];
    const testCountPages = Math.floor((countObjects - 1) / testMaxItems) + 1;
    const pageObjectResource = factory.createPageObjectResource(url, {});

    let first_page_objects: Array<any>;

    for (let n = 0; n < keys.length; n++) {
      let currentPage = pageObjectResource.getPagesInfo().currentPageNumber;
      let testSkipCount = (currentPage - 1) * testMaxItems;
      let currentPageLength = countObjects - (currentPage * testMaxItems);
      let first_link = `${url}?skipCount=${testSkipCount}&maxItems=${testMaxItems}`;
      let next_link = `${url}?skipCount=${currentPage * testMaxItems}&maxItems=${testMaxItems}`;
      let last_link = `${url}?skipCount=${(testCountPages - 1) * testMaxItems}&maxItems=${testMaxItems}`;
      let headerLink = `\<${first_link}\>; rel="${keys[0]}",\<${last_link}\>; rel="${keys[2]}",\<${next_link}\>; rel="${keys[1]}"`;

      currentPageLength = currentPageLength < testMaxItems ? currentPageLength : testMaxItems;
      currentPageLength = currentPageLength > 0 ? currentPageLength : 0;
      objects.length = 0;
      for (let n = 0; n < currentPageLength; n++) {
        objects.push({id: `testId_${testSkipCount + n}`, attributes: {name: `testName${testSkipCount + n}`}});
      }

      const {skipCount} = pageObjectResource.getRequestDataObject();
      if (!skipCount) {
        first_page_objects = angular.copy(objects);
        pageObjectResource.fetchObjects(testMaxItems);
      } else {
        let prev_link = `${url}?skipCount=${skipCount - testMaxItems}&maxItems=${testMaxItems}`;
        headerLink += `,\<${prev_link}\>; rel="${keys[3]}"`;
        pageObjectResource.fetchPageObjects(keys[n]);
      }

      // make response for object list
      $httpBackend.expect('GET', new RegExp(url + '?.*$')).respond(200, objects, {link: headerLink});

      $httpBackend.flush();

      // check objects
      expect(pageObjectResource.getRequestDataObject().maxItems).toEqual(testMaxItems.toLocaleString());
      expect(pageObjectResource.getPagesInfo().countPages).toEqual(testCountPages);
      expect(pageObjectResource.getPageObjects()).toEqual(objects);
    }

    // fetch first page again
    pageObjectResource.fetchPageObjects(keys[0]);

    // change response
    $httpBackend.expect('GET', new RegExp(url + '?.*$')).respond(304, {});

    $httpBackend.flush();

    // check objects
    expect(pageObjectResource.getRequestDataObject().maxItems).toEqual(testMaxItems.toLocaleString());
    expect(pageObjectResource.getPagesInfo().countPages).toEqual(testCountPages);
    expect(pageObjectResource.getPageObjects()).toEqual(first_page_objects);
  });

});
