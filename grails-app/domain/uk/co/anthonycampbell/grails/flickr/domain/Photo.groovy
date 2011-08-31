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
 * Domain class to encapsulate a flickr photo.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class Photo {
	// Do not persist
	static mapWith = "none" 

	// Declare properties
	String flickrId = ""
	String licenseId = ""
	String owner = ""
	String title = ""
	long latitude = 0
	long longitude = 0
	long viewCount = 0
	boolean primary = false
	Date dateUploaded = new Date()
	Date dateTaken = new Date()
	Date dateModified = new Date()
	
	// Associated domain classes
	List<String> tags
	List<Comment> comments
	
	// Photo URLs
	String squareUrl = ""
	long squareHeight = 0
	long squareWidth = 0
	String tinyUrl = ""
	long tinyHeight = 0
	long tinyWidth = 0
	String smallUrl = ""
	long smallHeight = 0
	long smallWidth = 0
	String mediumUrl = ""
	long mediumHeight = 0
	long mediumWidth = 0
	String originalUrl = ""
	long originalHeight = 0
	long originalWidth = 0
	
	// Relationships
	static hasMany = [ comments : Comment ]

	/**
	 * Constructor.
	 * 
	 * @param flickrId - flickr photo set ID.
	 * @param licenseId - flickr license ID.
	 * @param owner - photo owner.
	 * @param title - photo title.
	 * @param tags - photo tags.
	 * @param latitude- photo latitude.
	 * @param longitude - photo longitude.
	 * @param viewCount - photo view count.
	 * @param primary - whether the photo is the key photo for the photo set.
	 * @param dateUploaded - the date the photo was uploaded.
	 * @param dateTaken - the date the photo was taken.
	 * @param lastUpdated - the date the photo was last updated.
	 * @param squareUrl - URL of the square photo size.
	 * @param squareHeight - Height of the square photo.
	 * @param squareWidth - Width of the square photo.
	 * @param tinyUrl - URL of the tiny photo size.
	 * @param tinyHeight - Height of the tiny photo.
	 * @param tinyWidth - Width of the tiny photo.
	 * @param smallUrl - URL of the small photo size.
	 * @param smallHeight - Height of the small photo.
	 * @param smallWidth - Width of the small photo.
	 * @param mediumUrl - URL of the medium photo size.
	 * @param mediumHeight - Height of the medium photo.
	 * @param mediumWidth - Width of the medium photo.
	 * @param originalUrl - URL of the original photo size.
	 * @param originalHeight - Height of the original photo.
	 * @param originalWidth - Width of the original photo.
	 */
	public Photo(final def flickrId, def String licenseId, final String owner, final String title,
			final def tags, final def latitude, final def longitude, final String viewCount,
			final def primary, final def dateUploaded, final def dateTaken, final def lastUpdated,
			final String squareUrl, final def squareHeight, final def squareWidth,
			final String tinyUrl, def String tinyHeight, def String tinyWidth,
			final String smallUrl, def String smallHeight, def String smallWidth,
			final String mediumUrl, def String mediumHeight, def String mediumWidth,
			final String originalUrl, final def originalHeight, def String originalWidth) {

		this.flickrId = (flickrId instanceof JSONObject ? flickrId?._content : flickrId)
		this.licenseId = licenseId
		this.owner = (owner instanceof JSONObject ? owner?._content : owner)
		this.title = (title instanceof JSONObject ? title?._content : title)
		this.primary = primary?.asBoolean()
		
		this.tags = (tags instanceof String ? tags?.split()?.toList() :
			(tags instanceof Collection ? tags : []))
		this.comments = []
		
		this.latitude = (latitude instanceof Integer ? latitude :
			(latitude instanceof String && latitude?.isLong() ? latitude?.toLong() : 0L))
		this.longitude = (longitude instanceof Integer ? longitude :
			(longitude instanceof String && longitude?.isLong() ? longitude?.toLong() : 0L))
		this.viewCount = (viewCount instanceof Integer ? viewCount :
			(viewCount instanceof String && viewCount?.isLong() ? viewCount?.toLong() : 0L))
		
		if (dateUploaded instanceof String && dateUploaded?.isLong()) {
			this.dateUploaded = new Date(dateUploaded?.toLong())
		} else if (dateUploaded instanceof Date) {
			this.dateUploaded = dateUploaded
		}
		if (dateTaken instanceof String && dateTaken?.isLong()) {
			this.dateTaken = new Date(dateTaken?.toLong())
		} else if (dateTaken instanceof Date) {
			this.dateTaken = dateTaken
		}
		if (lastUpdated instanceof String && lastUpdated?.isLong()) {
			this.dateModified = new Date(lastUpdated?.toLong())
		} else if (lastUpdated instanceof Date) {
			this.dateModified = lastUpdated
		}
		
		this.squareUrl = squareUrl
		this.squareHeight = (squareHeight instanceof Integer ? squareHeight :
			(squareHeight instanceof String && squareHeight?.isLong() ? squareHeight?.toLong() : 0L))
		this.squareWidth = (squareWidth instanceof Integer ? squareWidth :
			(squareWidth instanceof String && squareWidth?.isLong() ? squareWidth?.toLong() : 0L))
		
		this.tinyUrl = tinyUrl
		this.tinyHeight = (tinyHeight instanceof Integer ? tinyHeight :
			(tinyHeight instanceof String && tinyHeight?.isLong() ? tinyHeight?.toLong() : 0L))
		this.tinyWidth = (tinyWidth instanceof Integer ? tinyWidth :
			(tinyWidth instanceof String && tinyWidth?.isLong() ? tinyWidth?.toLong() : 0L))
		
		this.smallUrl = smallUrl
		this.smallHeight = (smallHeight instanceof Integer ? smallHeight :
			(smallHeight instanceof String && smallHeight?.isLong() ? smallHeight?.toLong() : 0L))
		this.smallWidth = (smallWidth instanceof Integer ? smallWidth :
			(smallWidth instanceof String && smallWidth?.isLong() ? smallWidth?.toLong() : 0L))
		
		this.mediumUrl = mediumUrl
		this.mediumHeight = (mediumHeight instanceof Integer ? mediumHeight :
			(mediumHeight instanceof String && mediumHeight?.isLong() ? mediumHeight?.toLong() : 0L))
		this.mediumWidth = (mediumWidth instanceof Integer ? mediumWidth :
			(mediumWidth instanceof String && mediumWidth?.isLong() ? mediumWidth?.toLong() : 0L))
		
		this.originalUrl = originalUrl
		this.originalHeight = (originalHeight instanceof Integer ? originalHeight :
			(originalHeight instanceof String && originalHeight?.isLong() ? originalHeight?.toLong() : 0L))
		this.originalWidth = (originalWidth instanceof Integer ? originalWidth :
			(originalWidth instanceof String && originalWidth?.isLong() ? originalWidth?.toLong() : 0L))
	}

	// Declare constraints
    static constraints = {
		flickrId(blank: false, unique: true)
		licenseId(blank: true)
		owner(blank: true)
		title(blank: true)
		latitude(blank: true)
		longitude(blank: true)
		viewCount(min: 0L)
		dateUploaded(nullable: false)
		dateTaken(nullable: false)
		dateModified(nullable: false)
		tags(nullable: false)
		comments(nullable: false)
		
		squareUrl(blank: false)
		squareHeight(min: 0L)
		squareWidth(min: 0L)
		tinyUrl(blank: false)
		tinyHeight(min: 0L)
		tinyWidth(min: 0L)
		smallUrl(blank: false)
		smallHeight(min: 0L)
		smallWidth(min: 0L)
		mediumUrl(blank: true)
		mediumHeight(min: 0L)
		mediumWidth(min: 0L)
		originalUrl(blank: true)
		originalHeight(min: 0L)
		originalWidth(min: 0L)
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
		if (obj == null || !(obj instanceof Photo))
			return false
			
		final Photo other = (Photo) obj
		if (flickrId == null && other.flickrId != null) {
			return false
		} else if (!flickrId.equals(other.flickrId))
			return false
		
		return true
	}
}
