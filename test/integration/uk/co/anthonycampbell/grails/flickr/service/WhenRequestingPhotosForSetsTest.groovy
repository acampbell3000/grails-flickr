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

import uk.co.anthonycampbell.grails.flickr.domain.Photo
import uk.co.anthonycampbell.grails.flickr.domain.Photoset
import uk.co.anthonycampbell.grails.flickr.service.client.ServiceFailureException

/**
 * Integration test for the {@link FlickrService#getPhotosForSet(String)}.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class WhenRequestingPhotosForSetsTest extends GrailsUnitTestCase {
    // Declare dependency
    FlickrService flickrService
	
	// Test photo set ID
	private static final String TEST_SET_ID = "test.set.id"
    
    protected void setUp() {
        super.setUp()
		
		assertNotNull flickrService
		flickrService.resetConfig()
    }

    protected void tearDown() {
        super.tearDown()
    }

    @Test
    void validConfigurationShouldReturnPopulatedSets() {
		final Set<Photoset> photoSets = flickrService.getAllPhotosets()
		assertNotNull photoSets
		assertTrue !photoSets.isEmpty()
		
		final Set<Photo> photos = flickrService.getPhotosForPhotoset(photoSets?.toArray()[0]?.flickrId)
		assertNotNull photos
		assertTrue !photos.isEmpty()
    }

    @Test
    void invalidUserIdShouldThrowException() {
		// Reset config
		flickrService.flickrUserId = ""
		
		try {
			flickrService.getPhotosForPhotoset(TEST_SET_ID)
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
			flickrService.getPhotosForPhotoset(TEST_SET_ID)
			fail "Expected ServiceFailureException to be thrown!"
		} catch (ServiceFailureException sfe) {
			assertNotNull sfe
			assertEquals(100, sfe.getCode())
		}
    }

    @Test
    void nullSetIdShouldThrowException() {
		try {
			flickrService.getPhotosForPhotoset(null)
			fail "Expected IllegalArgumentException to be thrown!"
		} catch (IllegalArgumentException iae) {
			assertNotNull iae
			assertTrue iae.getMessage()?.contains("must be provided")
		}
    }

    @Test
    void emptySetIdShouldThrowException() {
		try {
			flickrService.getPhotosForPhotoset("")
			fail "Expected IllegalArgumentException to be thrown!"
		} catch (IllegalArgumentException iae) {
			assertNotNull iae
			assertTrue iae.getMessage()?.contains("must be provided")
		}
    }
}
