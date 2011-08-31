package uk.co.anthonycampbell.grails.flickr.service.client

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
 * Enum declaring all of the sported Flickr requests.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
public enum Request {
	// Declare constants
	GET_ALL_SET("flickr.photosets.getList"),
	GET_ALL_TAGS("flickr.tags.getListUser"),
	GET_ALL_COLLECTIONS("flickr.collections.getTree"),
	GET_SET_PHOTOS("flickr.photosets.getPhotos"),
	GET_PHOTO_COMMENTS("flickr.photos.comments.getList")
	
	// Declare properties
	private final String method
	
	/**
	 * Constructor.
	 * 
	 * @param method - Flickr API method name.
	 */
	Request(final String method) {
		this.method = method;
	}
	
	/**
	 * Return the flickr API method name for this enum instance.
	 * 
	 * @return Flickr API method name.
	 */
	public String getMethod() {
		return this.method
	}
}
