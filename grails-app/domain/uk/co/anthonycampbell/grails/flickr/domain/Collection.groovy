package uk.co.anthonycampbell.grails.flickr.domain

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

import java.util.Date
import java.util.List;

import org.codehaus.groovy.grails.web.json.JSONObject

/**
 * Domain class to encapsulate a flickr collection.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class Collection {
	// Do not persist
	static mapWith = "none"
	
	// Declare properties
	String flickrId = ""
	String title = ""
	String description = ""
	String urlLarge = ""
	String urlSmall = ""
	
	// Associated domain classes
	List<Collection> collections
	List<Photoset> photosets
	
	// Relationships
	static hasMany = [ collections : Collection, photosets : Photoset ]
	
	/**
	 * Constructor.
	 * 
	 * @param flickrId - the flickr ID.
	 * @param title - the collection title.
	 * @param description - the collection description.
	 * @param urlLarge - collection large cover photo.
	 * @param urlSmall - collection small cover photo.
	 */
	public Collection(final String flickrId, final String title, final String description,
			final String urlSmall, final String urlLarge) {
		
		this.flickrId = (flickrId instanceof JSONObject ? flickrId?._content : flickrId)
		this.title = title
		this.description = description
		this.urlSmall = urlSmall
		this.urlLarge = urlLarge
		
		this.collections = []
		this.photosets = []
	}

	// Declare constraints
    static constraints = {
		flickrId(blank: false, unique: true)
		title(blank: false)
		description(blank: true)
		urlSmall(blank: true)
		urlLarge(blank: true)
		collections(nullable: false)
		photosets(nullable: false)
    }
	
	@Override
	public String toString() {
		final def output = new StringBuilder()
		output << "${this.getClass().getName()} : ${this.flickrId}"
		return output.toString()
	}

	@Override
	public int hashCode() {
		return 31 * 1 + ((flickrId == null) ? 0 : flickrId.hashCode())
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof Collection))
			return false
		
		final Collection other = (Collection) obj
		if (flickrId == null && other.flickrId != null) {
			return false
		} else if (!flickrId.equals(other.flickrId))
			return false
		
		return true
	}
}
