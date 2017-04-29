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
import {PageObjectMock} from './page-object.mock';

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

  let pageObjectMock: PageObjectMock;

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

    cheBackend.setup();

    pageObjectMock = new PageObjectMock(factory, '/api/object', 15, 50);
  }));

  /**
   * Check assertion after the test
   */
  afterEach(() => {
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });

  fit('Fetch page objects without header link data', () => {
    const objects: Array<{ id: string; attributes: { name: string } }> = [];
    const pageObjectResource = pageObjectMock.getPageObjectResource();

    for (let n = 0; n < pageObjectMock.getMaxItems(); n++) {
      objects.push({id: `testId_${n}`, attributes: {name: `testName${n}`}});
    }

    pageObjectResource.fetchObjects(pageObjectMock.getMaxItems());

    // make response for object list
    $httpBackend.expect('GET', pageObjectMock.getUrlRegExp()).respond(200, objects);

    // gets page params
    const {countPages, currentPageNumber} = pageObjectResource.getPagesInfo();
    const {maxItems, skipCount} = pageObjectResource.getRequestDataObject();
    const pageObjects = pageObjectResource.getPageObjects();
    const testSkipCount = 0;

    $httpBackend.flush();

    // check objects
    expect(maxItems).toEqual(pageObjectMock.getMaxItems().toLocaleString());
    expect(skipCount).toEqual(testSkipCount.toLocaleString());
    expect(currentPageNumber).toEqual(1);
    expect(countPages).toEqual(1);
    expect(pageObjects).toEqual(objects);
  });

  it('Fetch page objects for the first, next and last pages. Then - preview and return to the first', () => {
    const url = '/api/object';
    const objects: Array<{ id: string; attributes: { name: string } }> = [];
    const pageObjectResource = pageObjectMock.getPageObjectResource();
    const keys = pageObjectMock.getPageLabels();
    const testCountPages = pageObjectMock.getCountPages();


    let first_page_objects: Array<any>;

    for (let n = 0; n < keys.length; n++) {
      let currentPage = pageObjectResource.getPagesInfo().currentPageNumber;
      let testSkipCount = (currentPage - 1) * pageObjectMock.getMaxItems();
      let currentPageLength = pageObjectMock.getCountObjects() - (currentPage * pageObjectMock.getMaxItems());
      currentPageLength = currentPageLength < pageObjectMock.getMaxItems() ? currentPageLength : pageObjectMock.getMaxItems();
      currentPageLength = currentPageLength > 0 ? currentPageLength : 0;

      let first_link = `${url}?skipCount=${testSkipCount}&maxItems=${pageObjectMock.getMaxItems()}`;
      let next_link = `${url}?skipCount=${currentPage * pageObjectMock.getMaxItems()}&maxItems=${pageObjectMock.getMaxItems()}`;
      let last_link = `${url}?skipCount=${(testCountPages - 1) * pageObjectMock.getMaxItems()}&maxItems=${pageObjectMock.getMaxItems()}`;

      let headerLink = `\<${first_link}\>; rel="${keys[0]}",\<${last_link}\>; rel="${keys[2]}",\<${next_link}\>; rel="${keys[1]}"`;

      objects.length = 0;
      for (let n = 0; n < currentPageLength; n++) {
        objects.push({id: `testId_${testSkipCount + n}`, attributes: {name: `testName${testSkipCount + n}`}});
      }

      const {skipCount} = pageObjectResource.getRequestDataObject();
      // find firs page
      if (currentPage === 1) {
        // remember first page object
        first_page_objects = angular.copy(objects);
        pageObjectResource.fetchObjects(pageObjectMock.getMaxItems());
      } else {
        // prepare 'prev' link
        let prev_link = `${url}?skipCount=${skipCount - pageObjectMock.getMaxItems()}&maxItems=${pageObjectMock.getMaxItems()}`;
        // add 'prev' link to header
        headerLink += `,\<${prev_link}\>; rel="${keys[3]}"`;
        // fetch page objects
        pageObjectResource.fetchPageObjects(keys[n]);
      }

      // make response for object list
      $httpBackend.expect('GET', pageObjectMock.getUrlRegExp()).respond(200, objects, {link: headerLink});

      $httpBackend.flush();

      // check objects
      expect(pageObjectResource.getRequestDataObject().maxItems).toEqual(pageObjectMock.getMaxItems().toLocaleString());
      expect(pageObjectResource.getPagesInfo().countPages).toEqual(testCountPages);
      expect(pageObjectResource.getPageObjects()).toEqual(objects);
    }

    // fetch first page again
    pageObjectResource.fetchPageObjects(keys[0]);

    // change response
    $httpBackend.expect('GET', pageObjectMock.getUrlRegExp()).respond(304, {});

    $httpBackend.flush();

    // check objects
    expect(pageObjectResource.getRequestDataObject().maxItems).toEqual(pageObjectMock.getMaxItems().toLocaleString());
    expect(pageObjectResource.getPagesInfo().countPages).toEqual(testCountPages);
    expect(pageObjectResource.getPageObjects()).toEqual(first_page_objects);
  });

});
