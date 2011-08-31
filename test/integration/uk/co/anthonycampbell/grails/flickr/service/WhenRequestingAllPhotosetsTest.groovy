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

import uk.co.anthonycampbell.grails.flickr.service.client.ServiceFailureException

/**
 * Integration test for the {@link FlickrService#getAllPhotosets()}.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class WhenRequestingAllPhotosetsTest extends GrailsUnitTestCase {
    // Declare dependency
    FlickrService flickrService
    
    protected void setUp() {
        super.setUp()
		
		assertNotNull flickrService
    }

    protected void tearDown() {
        super.tearDown()
    }

    @Test
    void validConfigurationShouldReturnPopulatedSets() {
		final def photoSets = flickrService.getAllPhotosets()
		
		assertNotNull photoSets
		assertTrue !photoSets.isEmpty()
    }

    @Test
    void invalidUserIdShouldReturnPopulatedSets() {
		// Reset config
		flickrService.flickrUserId = ""
		
		try {
			flickrService.getAllPhotosets()
			fail("Expected ServiceFailureException to be thrown!")
		} catch (ServiceFailureException sfe) {
			assertNotNull sfe
			assertEquals(1, sfe.getCode())
		}
    }

    @Test
    void invalidAppKeyShouldReturnPopulatedSets() {
		// Reset config
		flickrService.flickrApiKey = ""
		
		try {
			flickrService.getAllPhotosets()
			fail("Expected ServiceFailureException to be thrown!")
		} catch (ServiceFailureException sfe) {
			assertNotNull sfe
			assertEquals(100, sfe.getCode())
		}
    }
}
