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

/**
 * Domain class to encapsulate the flickr photo set.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class PhotoSet {
	// Declare properties
	String flickrId = ""
	String title = ""
	String description = ""
	long photoCount = 0
	long VideoCount = 0
	long viewCount = 0
	boolean visible = false
	Date dateCreated = new Date()
	Date dateModified  = new Date()
	
    static constraints = {
		
    }
	
	/*
	<photoset id="72157627180790191" primary="5986532042" secret="fed4ce50cb" server="6124"
			farm="7" photos="180" videos="0" needs_interstitial="0" visibility_can_see_set="1"
			count_views="18" count_comments="0" can_comment="0" date_create="1311905918"
			date_update="1312739756">
		<title>Tallinn (Estonia, 2011)</title>
		<description/>
	</photoset>
	*/
}
