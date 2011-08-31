package uk.co.anthonycampbell.grails.flickr.service

/**
 * Copyright 2011 Anthony Campbell (anthonycampbell.co.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import grails.test.*

import org.junit.Test

import uk.co.anthonycampbell.grails.flickr.domain.Photoset
import uk.co.anthonycampbell.grails.flickr.service.client.ServiceFailureException

/**
 * Integration test for the {@link FlickrService#getPhotosetsForCollection(String)}.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class WhenRequestingPhotosetsForCollectionTest extends GrailsUnitTestCase {
    // Declare dependency
    FlickrService flickrService
	
	// Test photo set ID
	private static final String TEST_COLLECTION_ID = "test.collection.id"
    
    protected void setUp() {
        super.setUp()
		
		assertNotNull flickrService
		flickrService.resetConfig()
    }

    protected void tearDown() {
        super.tearDown()
    }

    @Test
    void validConfigurationShouldReturnPopulatedPhotosets() {
		final Set<Collection> collections = flickrService.getAllCollections()
		assertNotNull collections
		
		if (collections) {
			final Set<Photoset> photosets =
				flickrService.getPhotosetsForCollection(collections?.toArray()[0]?.flickrId)
			assertNotNull photosets
			assertTrue !photosets.isEmpty()
		}
    }

    @Test
    void invalidUserIdShouldThrowException() {
		// Reset config
		flickrService.flickrUserId = ""
		
		try {
			flickrService.getPhotosetsForCollection(TEST_COLLECTION_ID)
			fail "Expected ServiceFailureException to be thrown!"
		} catch (ServiceFailureException sfe) {
			assertNotNull sfe
			assertEquals(1, sfe.getCode())
		}
    }

    @Test
    void invalidAppKeyShouldThrowException() {
		// Reset config
		flickrService.flickrApiKey = ""
		
		try {
			flickrService.getPhotosetsForCollection(TEST_COLLECTION_ID)
			fail "Expected ServiceFailureException to be thrown!"
		} catch (ServiceFailureException sfe) {
			assertNotNull sfe
			assertEquals(100, sfe.getCode())
		}
    }

    @Test
    void nullSetIdShouldThrowException() {
		try {
			flickrService.getPhotosetsForCollection(null)
			fail "Expected IllegalArgumentException to be thrown!"
		} catch (IllegalArgumentException iae) {
			assertNotNull iae
			assertTrue iae.getMessage()?.contains("must be provided")
		}
    }

    @Test
    void emptySetIdShouldThrowException() {
		try {
			flickrService.getPhotosetsForCollection("")
			fail "Expected IllegalArgumentException to be thrown!"
		} catch (IllegalArgumentException iae) {
			assertNotNull iae
			assertTrue iae.getMessage()?.contains("must be provided")
		}
    }
}
