package uk.co.anthonycampbell.grails.flickr.domain

import java.util.Date

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
class PhotoSet {
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
	public PhotoSet(final def flickrId, final def title, final def description,
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
		if (dateCreated instanceof String && dateModified?.isLong()) {
			this.dateModified = new Date(dateModified?.toLong())
		} else if (dateModified instanceof Date) {
			this.dateModified = dateModified
		}
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
    }
}
