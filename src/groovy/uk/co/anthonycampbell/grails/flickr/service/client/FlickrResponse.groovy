package uk.co.anthonycampbell.grails.flickr.service.client;

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

/**
 * Enum declaring all of the supported Flickr response states.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
public enum FlickrResponse {
	// Declare constants
	OK("ok"),
	FAIL("fail")
	
	// Declare properties
	private def state
	
	/**
	 * Constructor.
	 * 
	 * @param state - Flickr API response state.
	 */
	FlickrResponse(final def state) {
		this.state = state;
	}
	
	/**
	 * Return the flickr API response state name for this enum instance.
	 * 
	 * @return Flickr API response state.
	 */
	public def getState() {
		return this.state
	}
}
