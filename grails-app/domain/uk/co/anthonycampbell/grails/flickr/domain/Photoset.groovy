package uk.co.anthonycampbell.grails.flickr.domain

import java.util.Date
import java.util.List;

import org.codehaus.groovy.grails.web.json.JSONObject

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
 * Domain class to encapsulate the flickr photo set.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class Photoset {
	// Do not persist
	static mapWith = "none"
	
	// Declare properties
	String flickrId = ""
	String title = ""
	String description = ""
	long photoCount = 0L
	long videoCount = 0L
	long viewCount = 0L
	long commentCount = 0L
	boolean commentable = false
	boolean visible = false
	Date dateCreated = new Date()
	Date dateModified  = new Date()
	
	// Associated domain classes
	List<Photo> photos
	
	// Relationships
	static hasMany = [ photos : Photo ]

	
	/**
	 * Constructor.
	 * 
	 * @param flickrId - flickr photo set ID.
	 * @param title - photo set title.
	 * @param description - photo set description.
	 * @param photoCount - photo set photo count.
	 * @param videoCount - photo set video count.
	 * @param viewCount - photo set view count.
	 * @param commentCount - photo set comment count.
	 * @param commentable - whether this photo set is commentable.
	 * @param visible - whether this photo set is visible.
	 * @param dateCreated - photo set creation date.
	 * @param dateModified - photo set last modified date.
	 */
	public Photoset(final def flickrId, final def title, final def description,
			final def photoCount, final def videoCount, final def viewCount,
			final def commentCount, final def commentable, final def visible,
			final def dateCreated, final def dateModified) {
		
		this.flickrId = (flickrId instanceof JSONObject ? flickrId?._content : flickrId)
		this.title = (title instanceof JSONObject ? title?._content : title)
		this.description = (description instanceof JSONObject ? description?._content : description)
		
		this.photoCount = (photoCount instanceof Integer ? photoCount :
			(photoCount instanceof String && photoCount?.isLong() ? photoCount?.toLong() : 0L))
		this.videoCount = (videoCount instanceof Integer ? videoCount :
			(videoCount instanceof String && videoCount?.isLong() ? videoCount?.toLong() : 0L))
		this.viewCount = (viewCount instanceof Integer ? viewCount :
			(viewCount instanceof String && viewCount?.isLong() ? viewCount?.toLong() : 0L))
		this.commentCount = (commentCount instanceof Integer ? commentCount :
			(commentCount instanceof String && commentCount?.isLong() ? commentCount?.toLong() : 0L))
		
		this.commentable = commentable?.asBoolean()
		this.visible = visible?.asBoolean()
		
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
		
		this.photos = []
	}

	// Declare constraints
    static constraints = {
		flickrId(blank: false, unique: true)
		title(blank: false)
		description(blank: true)
		photoCount(min: 0L)
		videoCount(min: 0L)
		viewCount(min: 0L)
		commentCount(min: 0L)
		dateCreated(nullable: false)
		dateModified(nullable: false)
		photos(nullable: false)
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
		if (obj == null || !(obj instanceof Photoset))
			return false
		
		final Photoset other = (Photoset) obj
		if (flickrId == null && other.flickrId != null) {
			return false
		} else if (!flickrId.equals(other.flickrId))
			return false
		
		return true
	}
}
