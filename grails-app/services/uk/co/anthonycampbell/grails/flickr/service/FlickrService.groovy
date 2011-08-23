package uk.co.anthonycampbell.grails.flickr.service

import groovyx.net.http.*
import static groovyx.net.http.ContentType.JSON

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
 * Default implementation of the Flickr service.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class FlickrService {

	static transactional = false

	// Declare API requests
	private static final def FLICKR_API_REST_URL = "http://api.flickr.com/services/rest/?"
	//private static final def FLICKR_API_REST_URL = "http://api.flickr.com/services/rest/?"
	
	//method=flickr.photosets.getList&api_key=3d4442864a12fda6f7d5bc49da98135c&user_id=587922ar54@N02

	def getSets() {

		def http = new HTTPBuilder("http://localhost:8080/amazon")

		http.request(Method.GET, JSON) {
			url.path = '/book/list'
			response.success = {resp, json ->
				json.books.each { book -> println book.title }
			}
		}
	}
}
