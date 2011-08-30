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

import uk.co.anthonycampbell.grails.flickr.service.client.FlickrServiceFailureException

/**
 * Integration test for the {@link FlickrService#getSetPhotos(Object)}.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class WhenRequestingAllTagsTest extends GrailsUnitTestCase {
    // Declare dependency
    FlickrService flickrService
	
	// Test photo tag ID
	private static final String TEST_TAG_ID = "test.tag.id"
    
    protected void setUp() {
        super.setUp()
		
		assertNotNull flickrService
		flickrService.resetConfig()
    }

    protected void tearDown() {
        super.tearDown()
    }

    @Test
    void validConfigurationShouldReturnAllTags() {
		final def tags = flickrService.getAllTags()
		assertNotNull tags
		assertTrue !tags?.isEmpty()
    }

    @Test
    void invalidUserIdShouldThrowException() {
		// Reset config
		flickrService.flickrUserId = ""
		
		try {
			flickrService.getAllTags()
			fail "Expected FlickrServiceFailureException to be thrown!"
		} catch (FlickrServiceFailureException fsfe) {
			assertNotNull fsfe
			assertEquals(1, fsfe.getCode())
		}
    }

    @Test
    void invalidAppKeyShouldThrowException() {
		// Reset config
		flickrService.flickrApiKey = ""
		
		try {
			flickrService.getAllTags()
			fail "Expected FlickrServiceFailureException to be thrown!"
		} catch (FlickrServiceFailureException fsfe) {
			assertNotNull fsfe
			assertEquals(100, fsfe.getCode())
		}
    }
}
