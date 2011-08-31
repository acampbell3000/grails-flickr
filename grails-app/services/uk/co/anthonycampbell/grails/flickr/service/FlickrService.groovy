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

import grails.converters.JSON
import grails.plugin.springcache.annotations.CacheFlush
import grails.plugin.springcache.annotations.Cacheable

import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.codehaus.groovy.grails.web.json.*
import org.springframework.beans.factory.InitializingBean
import org.springframework.util.StringUtils

import uk.co.anthonycampbell.grails.flickr.domain.Collection
import uk.co.anthonycampbell.grails.flickr.domain.Comment
import uk.co.anthonycampbell.grails.flickr.domain.Photo
import uk.co.anthonycampbell.grails.flickr.domain.Photoset
import uk.co.anthonycampbell.grails.flickr.service.client.Request
import uk.co.anthonycampbell.grails.flickr.service.client.Response
import uk.co.anthonycampbell.grails.flickr.service.client.ServiceFailureException
import uk.co.anthonycampbell.grails.flickr.service.client.ServiceUnavailableException

/**
 * Default implementation of the Flickr service.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
public class FlickrService implements InitializingBean {
	
    // Stateless session bean
	static transactional = false
    boolean serviceInitialised = false

	// Declare API constants
	private static final def FLICKR_API_REST_URL       = "http://api.Flickr.com"
	
    // Service properties
    def grailsApplication
    def flickrApiKey
    def flickrUserId
    
	/**
	 * Attempt to retrieve all photos for the configured Flickr account.
	 * 
	 * @return set of photos.
	 * @throws ServiceUnavailableException Flickr service is currently available.
	 * @throws ServiceFailureException Unable to retrieve photos.
	 */
	@Cacheable("flickrServiceCache")
	public Set<Photo> getAllPhotos() throws ServiceUnavailableException,
			ServiceFailureException {
		// Initialise result
		final Set<Photo> photos = new LinkedHashSet<Photo>()
				
		// Get all available sets
		final Set<Photoset> photosets = getAllPhotosets()
		
		if (photosets) {
			photosets.each {
				// Photos for each set
				photos.addAll(getPhotosForPhotoset(it?.flickrId))
			}
		}
		
		return photos
	}
				
	/**
	 * Attempt to retrieve all photo sets for the configured Flickr account.
	 *
	 * @return set of photo sets.
	 * @throws ServiceUnavailableException Flickr service is currently available.
	 * @throws ServiceFailureException Unable to retrieve photo sets.
	 */
	@Cacheable("flickrServiceCache")
	public Set<Photoset> getAllPhotosets() throws ServiceUnavailableException,
			ServiceFailureException {
        // Check state
		isInitialised()
		
		// Initialise result
		final Set<Photoset> photosets = new LinkedHashSet<Photoset>()
		
		log?.info "Retrieving sets for user ID ${this.flickrUserId}..."
		
		// Request
		final JSONObject json = getFlickr([method: Request.GET_ALL_SET.getMethod()])
		
		log?.debug "Response recieved"
		
		// Validate response
		if (json?.photosets) {
			// Parse JSON
			json?.photosets?.photoset?.each {
				log?.debug "- photoset ID: ${it?.id}"
				
				photosets.add(new Photoset(it?.id, it?.title, it?.description,
					it?.photos, it?.videos, it?.count_views, it?.count_comments,
					it?.can_comment, it?.visibility_can_see_set, it?.date_create,
					it?.date_update))
			}
		}
		
		log?.info "${photosets?.size()} photoset(s) retrieved"
		
		return photosets
	}
	
	/**
	 * Attempt to retrieve all collections for the configured Flickr user.
	 *
	 * @return set of collections for the configured Flickr user.
	 * @throws ServiceUnavailableException Flickr service is currently available.
	 * @throws ServiceFailureException Unable to retrieve collections for the
	 * 		configured Flickr user.
	 */
	@Cacheable("flickrServiceCache")
	public Set<Collection> getAllCollections() throws ServiceUnavailableException,
			ServiceFailureException {
		// Check state
		isInitialised()
		
		// Initialise result
		final Set<Collection> collections = new LinkedHashSet<Collection>()
		
		// First lets get detailed photo set information
		final Map<String, Photoset> photosetMap = [:]
		final Set<Photoset> allPhotosets = getAllPhotosets()
		if (allPhotosets) {
			allPhotosets.each {
				photosetMap.put(it?.flickrId, it)
			}
		}
		
		log?.info "Retrieving all collections..."
		
		// Request
		final JSONObject json = getFlickr([method: Request.GET_ALL_COLLECTIONS.getMethod()])
		
		log?.debug "Response recieved"
		
		// Validate response
		if (json?.collections) {
			// Parse JSON
			json?.collections?.collection?.each {
				collections.add(parseCollection(it, photosetMap))
			}
		}
		
		log?.info "${collections?.size()} collection(s) retrieved"
		
		return collections
	}
			
	/**
	 * Attempt to retrieve all tags for the configured Flickr user.
	 *
	 * @return set of tags for the configured Flickr user.
	 * @throws ServiceUnavailableException Flickr service is currently available.
	 * @throws ServiceFailureException Unable to retrieve tags for the configured Flickr user.
	 */
	@Cacheable("flickrServiceCache")
	public Set<String> getAllTags() throws ServiceUnavailableException,
			ServiceFailureException {
		// Check state
		isInitialised()
		
		// Initialise result
		final Set<String> tags = new LinkedHashSet<String>()
		
		log?.info "Retrieving all tags..."
		
		// Request
		final JSONObject json = getFlickr([method: Request.GET_ALL_TAGS.getMethod()])
		
		log?.debug "Response recieved"
		
		// Validate response
		if (json?.who) {
			// Parse JSON
			json?.who?.tags?.tag?.each {
				log?.debug "- tag keyword: ${it?._content}"
				
				tags.add(it?._content)
			}
		}
		
		log?.info "${tags?.size()} tag(s) retrieved"
		
		return tags
	}
    
	/**
	 * Attempt to retrieve all photo sets for the selected collection.
	 * 
	 * @param collectionId - collection ID.
	 * @return set of photo sets for the selected collection.
	 * @throws ServiceUnavailableException Flickr service is currently available.
	 * @throws ServiceFailureException Unable to retrieve photo sets for provided collection ID.
	 */
	@Cacheable("flickrServiceCache")
	public Set<Photoset> getPhotosetsForCollection(final String collectionId)
			throws ServiceUnavailableException, ServiceFailureException {
		// Check state
        isInitialised()
		
		// Validate
		if (!StringUtils.hasText(collectionId)) {
			throw new IllegalArgumentException("Photo collection ID must be provided!")
		}
		
		// Initialise result
		final Set<Photoset> photosets = new LinkedHashSet<Photoset>()
		
		// First lets get detailed photo set information
		final Map<String, Photoset> photosetMap = [:]
		final Set<Photoset> allPhotosets = getAllPhotosets()
		if (allPhotosets) {
			allPhotosets.each {
				photosetMap.put(it?.flickrId, it)
			}
		}
		
		log?.info "Retrieving photosets for collection ID ${collectionId}..."
		
		// Request
		final JSONObject json = getFlickr([method: Request.GET_ALL_COLLECTIONS.getMethod(),
			collection_id: collectionId])
		
		log?.debug "Response recieved"
		
		// Validate response
		if (json?.collections) {
			// Parse JSON
			json?.collections?.collection?.each {
				photosets.add(parseCollectionPhotosets(it, photosetMap))
			}
		}
		
		log?.info "${photosets?.size()} photoset(s) retrieved"
		
		return photosets
	}
    
	/**
	 * Attempt to retrieve all photos for the selected photo set.
	 * 
	 * @param setId - photo set ID.
	 * @return set of photos for the selected photo set.
	 * @throws ServiceUnavailableException Flickr service is currently available.
	 * @throws ServiceFailureException Unable to retrieve photos for provided set ID.
	 */
	@Cacheable("flickrServiceCache")
	public Set<Photo> getPhotosForPhotoset(final String setId) throws ServiceUnavailableException,
			ServiceFailureException {
		// Check state
        isInitialised()
		
		// Validate
		if (!StringUtils.hasText(setId)) {
			throw new IllegalArgumentException("Photo set ID must be provided!")
		}
		
		// Initialise result
		final Set<Photo> photos = new LinkedHashSet<Photo>()
		
		log?.info "Retrieving photos for set ID ${setId}..."
		
		// Request
		final JSONObject json = getFlickr([method: Request.GET_SET_PHOTOS.getMethod(),
			extras: "license,date_upload,date_taken,owner_name,last_update,geo,tags,o_dims,views,media,path_alias,url_sq,url_t,url_s,url_m,url_o",
			photoset_id: setId])
		
		log?.debug "Response recieved"
		
		// Validate response
		if (json?.photoset) {
			// Parse JSON
			json?.photoset?.photo?.each {
				log?.debug "- photo ID: ${it?.id}"
				
				photos.add(new Photo(it?.id, it?.license, it?.ownername, it?.title, it?.tags,
					it?.latitude, it?.longitude, it?.views, it?.isprimary, it?.dateupload, it?.datetaken,
					it?.lastupdate, it?.url_sq, it?.height_sq, it?.width_sq, it?.url_t, it?.height_t,
					it?.width_t, it?.url_s, it?.height_s, it?.width_s, it?.url_m, it?.height_m,
					it?.width_m, it?.url_o, it?.height_o, it?.width_o))
			}
		}
		
		log?.info "${photos?.size()} photo(s) retrieved"
		
		return photos
	}
    
	/**
	 * Attempt to retrieve all photos for the provided tag.
	 * 
	 * @param tag - the selected tag.
	 * @return set of photos for the provided tag.
	 * @throws ServiceUnavailableException Flickr service is currently available.
	 * @throws ServiceFailureException Unable to retrieve photos for the provided tag.
	 */
	@Cacheable("flickrServiceCache")
	public Set<Photo> getPhotosForTag(final String tag) throws ServiceUnavailableException,
			ServiceFailureException {
		// Check state
        isInitialised()
		
		// Validate
		if (!StringUtils.hasText(tag)) {
			throw new IllegalArgumentException("Photo tag must be provided!")
		}
		
		// Initialise result
		final Set<Photo> matchedPhotos = new LinkedHashSet<Photo>()
		
		log?.info "Retrieving photos for tag ${tag}..."
		
		// First get all photo sets
		final Set<Photoset> photosets = getAllPhotosets()
		if (photosets) {
			photosets.each {
				final Set<Photo> photos = getPhotosForPhotoset(it?.flickrId)
				
				if (photos) {
					photos.each {
						if (it?.tags?.contains(tag)) {
							log?.debug "- photo ID ${it?.flickrId}"
							
							matchedPhotos.add(it)
						}
					}
				}
			}	
		}
		
		log?.info "${matchedPhotos?.size()} photo(s) retrieved for tag ${tag}"
		
		return matchedPhotos
	}
    
	/**
	 * Attempt to retrieve all comments for the selected photo.
	 * 
	 * @param photoId - ID of the selected photo.
	 * @return set of comments for the provided photo
	 * @throws ServiceUnavailableException Flickr service is currently available.
	 * @throws ServiceFailureException Unable to retrieve comments for the selected photo.
	 */
	@Cacheable("flickrServiceCache")
	public Set<Comment> getCommentsForPhoto(final String photoId) throws ServiceUnavailableException,
			ServiceFailureException {
		// Check state
        isInitialised()
		
		// Validate
		if (!StringUtils.hasText(photoId)) {
			throw new IllegalArgumentException("Photo ID must be provided!")
		}
		
		// Initialise result
		final Set<Comment> comments = new LinkedHashSet<Comment>()
		
		log?.info "Retrieving comments for photo ${photoId}..."
		
		// Request
		final JSONObject json = getFlickr([method: Request.GET_PHOTO_COMMENTS.getMethod(),
			photo_id: photoId])
		
		log?.debug "Response recieved"
		
		// Validate response
		if (json?.comments) {
			// Parse JSON
			json?.comments?.comment?.each {
				log?.debug "- comment ID: ${it?.id}"
				
				comments.add(new Comment(it?.id, it?.author, it?.authorname, it?._content,
					it?.datecreate, it?.datecreate))
			}
		}
		
		log?.info "${comments?.size()} comment(s) retrieved for photo ${photoId}"
		
		return comments
	}
	
	/**
	 * Helper method to perform Flickr API HTTP REST GET request.
	 * 
	 * @param parameters Flickr request parameters.
	 * @return JSON response.
	 * @throws ServiceFailureException Flickr HTTP REST request failed.
	 */
	private JSONObject getFlickr(final Map<String, String> parameters)
			throws ServiceFailureException {
		// Declare response
		JSONObject json
		
		// Validate
		if (parameters && !parameters.isEmpty()) {
			// Add configuration
			parameters["api_key"] = this.flickrApiKey
			parameters["user_id"] = this.flickrUserId
			parameters["format"] = "json"
			parameters["nojsoncallback"] = "1"
			
			StringReader jsonResponse
			try {
				// Rest client
				jsonResponse = withHttp(uri: FLICKR_API_REST_URL) {
					get(path: '/services/rest',
						query : parameters)
				}
			} catch (Exception ex) {
				throwFailureException("Flickr HTTP request failed.", parameters, null, ex)
			}

			// Read response
			final String responseText = jsonResponse?.getText();
			
			// Validate
			if (StringUtils.hasText(responseText)) {
				try {
					json = JSON.parse(responseText)
					
					// Validate
					if (!json || json?.stat != Response.OK.getState()) {
						def errorMessage =
							"Unable to perform Flickr request. Flickr API returned error code."
						if (json?.code && json?.message) {
							errorMessage += " ${json.code}=${json.message}."
						}
					
						throwFailureException(errorMessage, parameters, json?.code, null)
					}
				} catch (ConverterException ce) {
					throwFailureException(
						"Unable to perform Flickr request. Response from Flickr API was not valid JSON.",
							parameters, null, ce)
				}
			} else {
				throwFailureException(
					"Unable to perform Flickr request. Response from Flickr API not received.",
						parameters)
			}
		} else {
			throwFailureException(
				"Unable to perform Flickr request. Flickr API method not provided.", parameters)
		}
		
		return json
	}	
			
	/**
	 * Helper method to parse a json object for Flickr collection details and
	 * any associated child collection or photo sets.
	 *
	 * @param json JSON object to parse collection details.
	 * @param photosetMap helper map containing all photo set details.
	 * @return Flickr collection and associated children.
	 */
	private Collection parseCollection(final JSONObject json, final Map<String, Photoset> photosetMap) {
		// Initialise result
		Collection collection = null
		
		// Validate
		if (json && photosetMap) {
			log?.debug "- collection ID: ${json?.id}"
			
			collection = new Collection(json?.id, json?.title, json?.description,
				json?.iconsmall, json?.iconlarge)
			
			// Children
			json?.set?.each { collection.addToPhotosets(photosetMap?.get(it?.id)) }
			json?.collection?.each { collection.addToCollections(parseCollection(it, photosetMap)) }
		}
		
		return collection
	}	
			
	/**
	 * Helper method to parse a json object for Flickr collection details and
	 * return all associated photo sets.
	 *
	 * @param json JSON object to parse collection details.
	 * @param photosetMap helper map containing all photo set details.
	 * @return Flickr collection and associated children.
	 */
	private Set<Photoset> parseCollectionPhotosets(final JSONObject json,
			final Map<String, Photoset> photosetMap) {
		// Initialise result
		final Set<Photoset> photosets = new LinkedHashSet<Photoset>()
		
		// Validate
		if (json && photosetMap) {
			log?.debug "- collection ID: ${json?.id}"
			
			// Children
			json?.set?.each { photosets.add(photosetMap?.get(it?.id)) }
			json?.collection?.each { photosets.addAll(parseCollectionPhotosets(it, photosetMap)) }
		}
		
		return photosets
	}

    /**
     * Initialise configuration properties.
     */
    @Override
    void afterPropertiesSet() {
        log?.info "Initialising the ${this.getClass().getSimpleName()}..."
        
        resetConfig()
    }

    /**
     * Attempt to load Flickr API connection details available in the
     * grails-app/conf/Config.groovy file.
     *
     * @return whether configuration successfully loaded.
     */
	@CacheFlush("flickrServiceCache")
    boolean resetConfig() {
        log?.info "Setting ${this.getClass().getSimpleName()} configuration..."

        // Get configuration from Config.groovy
        this.flickrUserId = grailsApplication?.config?.flickr?.userid
        this.flickrApiKey = grailsApplication?.config?.flickr?.apikey

        // Validate properties and attempt to initialise the service
        return validateService()
    }

    /**
     * Validate the service properties and attempt to initialise.
     *
     * @return whether the service has been successfully initialised.
     */
    private boolean validateService() {
        // Lets be optimistic
        boolean configValid = true
        
        log?.info "Begin ${this.getClass().getSimpleName()} configuration validation..."
        
        // Validate properties
        if (!isConfigValid(this.flickrUserId)) {
            log?.error "Unable to connect to Flickr API - invalid user ID (flickrUserId=$flickrUserId). " +
			    "Please ensure you have declared the property Flickr.userid in your application's config."
            configValid = false
        }
        if (!isConfigValid(this.flickrApiKey)) {
            log?.error "Unable to connect to Flickr API - invalid API key (flickrUserId=$flickrApiKey). " +
                "Please ensure you have declared the property Flickr.apikey in your application's config."
            configValid = false
        }

        // Only initialise the service if the configuration is valid
        this.serviceInitialised = configValid
        
        // Return initialisation result
        return this.serviceInitialised
    }
    
    /**
     * Check whether the provided configuration reference is valid and set.
     *
     * @param setting the configuration value to validate.
     * @return whether the current configuration value is valid and set.
     */
    private boolean isConfigValid(final def setting) {
        // Initialise result
        boolean result = false
		
        // Validate
        if (setting && setting instanceof String) {
            // Non empty string
            result = true
        }
        
        // Return result
        return result
    }
	
	/**
	 * Helper method to throw {@link ServiceFailureException} when the
	 * service is not initialised.
	 */
	private void isInitialised() {
		if (!this.serviceInitialised) {
			final def ouput = "Some of the plug-in configuration is missing. " +
				"Please refer to the documentation and ensure you have declared all " +
				"of the required configuration."
			
			log?.error(ouput)
			throw new ServiceUnavailableException(ouput)
		}
	}
	
	/**
	 * Helper method to throw {@link ServiceFailureException}.
	 * Will include provided error message text and request parameters.
	 * 
	 * @param errorMessage - error message.
	 * @param parameters - request parameters.
	 */
	private void throwFailureException(final String errorMessage, final Map<String, String> parameters) {
		throwFailureException(errorMessage, parameters, -1, null)
	}
	
	/**
	 * Helper method to throw {@link ServiceFailureException}.
	 * Will include provided error message text, code, request parameters and
	 * exception cause (if provided).
	 * 
	 * @param errorMessage - error message.
	 * @param parameters - request parameters.
	 * @param code - (if any) error code returned from the Flickr API.
	 * @param exception - (if any) cause exception.
	 */
	private void throwFailureException(final String errorMessage, final Map<String, String> parameters,
			final def code, final Exception exception) {
		final def ouput = "${errorMessage}${printParameters(parameters)}"
	
		log?.error(ouput)
		throw new ServiceFailureException(ouput, code, exception)
	}
	
	/**
	 * Helper method to print the parameter map provided to the Flickr API request.
	 * 
	 * @param parameters - map containing the Flickr request parameters.
	 * @return string representation of the parameter map.
	 */
	private String printParameters(final Map<String, String> parameters) {
		final def output = new StringBuilder()
		
		if (parameters && !parameters.isEmpty()) {
			boolean firstElement = true
		
			output << " ("
			parameters?.each {
				if (!firstElement) {
					output << ", "
				} else {
					firstElement = false
				}
				
				output << it
			}
			output << ")"
		}
		
		return output.toString()
	}
}
