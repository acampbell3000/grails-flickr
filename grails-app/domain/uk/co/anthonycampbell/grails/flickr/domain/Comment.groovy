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

import org.codehaus.groovy.grails.web.json.JSONObject

/**
 * Domain class to encapsulate a flickr photo comment.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class Comment {
	// Do not persist
	static mapWith = "none"
	
	// Declare properties
	String flickrId = ""
	String flickrUserId = ""
	String flickrUserName = ""
	String text = ""
	Date dateCreated = new Date()
	Date dateModified  = new Date()
	
	/**
	 * Constructor.
	 * 
	 * @param flickrId - flickr comment ID.
	 * @param flickrUserId - flickr user ID for the comment author.
	 * @param flickrUserName - flickr user name for the comment author.
	 * @param text - comment text
	 * @param dateCreated - comment creation date.
	 * @param dateModified - comment last modified date.
	 */
	public Comment(final def flickrId, final String flickrUserId, final String flickrUserName,
			final String text, final def dateCreated, final def dateModified) {	
		
			this.flickrId = (flickrId instanceof JSONObject ? flickrId?._content : flickrId)
		this.flickrUserId = flickrUserId
		this.flickrUserName = flickrUserName
		this.text = text
		
		if (dateCreated instanceof String && dateCreated?.isLong()) {
			this.dateCreated = new Date(dateCreated?.toLong())
		} else if (dateCreated instanceof Date) {
			this.dateCreated = dateCreated
		}
		if (dateModified instanceof String && dateModified?.isLong()) {
			this.dateModified = new Date(dateModified?.toLong())
		} else if (dateModified instanceof Date) {
			this.dateModified = dateModified
		}
	}

	// Declare contraints
    static constraints = {
		flickrId(blank: false, unique: true)
		flickrUserId(blank: false)
		teflickrUserName(blank: false)
		text(blank: false)
		dateCreated(nullable: false)
		dateModified(nullable: false)
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
		if (obj == null || !(obj instanceof Comment))
			return false
		
		final Comment other = (Comment) obj
		if (flickrId == null && other.flickrId != null) {
			return false
		} else if (!flickrId.equals(other.flickrId))
			return false
		
		return true
	}
}
