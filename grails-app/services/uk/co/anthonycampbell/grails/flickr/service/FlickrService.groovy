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
import grails.plugin.springcache.annotations.Cacheable

import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.codehaus.groovy.grails.web.json.*
import org.springframework.beans.factory.InitializingBean
import org.springframework.util.StringUtils

import uk.co.anthonycampbell.grails.flickr.domain.Photo
import uk.co.anthonycampbell.grails.flickr.domain.PhotoSet
import uk.co.anthonycampbell.grails.flickr.service.client.FlickrRequest
import uk.co.anthonycampbell.grails.flickr.service.client.FlickrResponse
import uk.co.anthonycampbell.grails.flickr.service.client.FlickrServiceFailureException
import uk.co.anthonycampbell.grails.flickr.service.client.FlickrServiceUnavailableException

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
	private static final def FLICKR_API_REST_URL       = "http://api.flickr.com"
	
    // Service properties
    def grailsApplication
    def flickrApiKey
    def flickrUserId
    
	/**
	 * Attempts to retrieve all photo sets for the configured Flickr account.
	 * 
	 * @return list of photo sets.
	 * @throws FlickrServiceUnavailableException Flickr service is currently available.
	 * @throws FlickrServiceFailureException Unable to retrieve photo sets.
	 */
	@Cacheable
	public List<PhotoSet> getSets() throws FlickrServiceUnavailableException,
			FlickrServiceFailureException {
        // Check state
		isInitialised()
		
		// Initialise result
		def photoSets = []
		
		log?.info "Retrieving sets for user ID ${this.flickrUserId}..."
		
		// Request
		final JSONObject json = getFlickr([method: FlickrRequest.GET_SET_LIST.getMethod()])
		
		log?.debug "Response recieved"
		
		// Validate response
		if (json?.photosets) {
			// Parse JSON
			json?.photosets?.photoset?.each {
				log?.debug "- photoset ID: ${it?.id}"
				
				photoSets.add(new PhotoSet(it?.id, it?.title, it?.description,
					it?.photos, it?.videos, it?.count_views, it?.count_comments,
					it?.can_comment, it?.visibility_can_see_set, it?.date_create,
					it?.date_update))
			}
		}
		
		return photoSets
	}
    
	/**
	 * Attempts to retrieve all photos for the selected photo set.
	 * 
	 * @param setId - photo set ID.
	 * @return list of photos for the selected photo set.
	 * @throws FlickrServiceUnavailableException Flickr service is currently available.
	 * @throws FlickrServiceFailureException Unable to retrieve photos for provided set ID.
	 */
	@Cacheable
	public List<Photo> getPhotosForSet(final String setId) throws FlickrServiceUnavailableException,
			FlickrServiceFailureException {
		// Check state
        isInitialised()
		
		// Validate
		if (!StringUtils.hasText(setId)) {
			throw new IllegalArgumentException("Photo set ID must be provided!")
		}
		
		// Initialise result
		def photos = []
		
		log?.info "Retrieving photos for set ID ${setId}..."
		
		// Request
		final JSONObject json = getFlickr([method: FlickrRequest.GET_SET_PHOTOS.getMethod(),
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
		
		return photos
	}
    
	/**
	 * Attempts to retrieve all photos for the selected tag.
	 * 
	 * @param tagId - photo tag keyword.
	 * @return list of photos for the selected photo set.
	 * @throws FlickrServiceUnavailableException Flickr service is currently available.
	 * @throws FlickrServiceFailureException Unable to retrieve photos for provided set ID.
	 */
	@Cacheable
	public List<Photo> getPhotosForTag(final String tagId) throws FlickrServiceUnavailableException,
			FlickrServiceFailureException {
		// Check state
        isInitialised()
		
		// Validate
		if (!StringUtils.hasText(tagId)) {
			throw new IllegalArgumentException("Photo tag must be provided!")
		}
		
		// Initialise result
		def photos = []
		
		log?.info "Retrieving photos for tag ${tagId}..."
		
		// Request
		final JSONObject json = getFlickr([method: FlickrRequest.GET_SET_PHOTOS.getMethod(),
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
		
		return photos
	}
	
	/**
	 * Helper method to perform Flickr API HTTP REST GET request.
	 * 
	 * @param parameters Flickr request parameters.
	 * @return JSON response.
	 * @throws FlickrServiceFailureException Flickr HTTP REST request failed.
	 */
	private JSONObject getFlickr(final Map<String, String> parameters)
			throws FlickrServiceFailureException {
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
					if (!json || json?.stat != FlickrResponse.OK.getState()) {
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
			    "Please ensure you have declared the property flickr.userid in your application's config."
            configValid = false
        }
        if (!isConfigValid(this.flickrApiKey)) {
            log?.error "Unable to connect to Flickr API - invalid API key (flickrUserId=$flickrApiKey). " +
                "Please ensure you have declared the property flickr.apikey in your application's config."
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
	 * Helper method to throw {@link FlickrServiceFailureException} when the
	 * service is not initialised.
	 */
	private void isInitialised() {
		if (!this.serviceInitialised) {
			final def ouput = "Some of the plug-in configuration is missing. " +
				"Please refer to the documentation and ensure you have declared all " +
				"of the required configuration."
			
			log?.error(ouput)
			throw new FlickrServiceUnavailableException(ouput)
		}
	}
	
	/**
	 * Helper method to throw {@link FlickrServiceFailureException}.
	 * Will include provided error message text and request parameters.
	 * 
	 * @param errorMessage - error message.
	 * @param parameters - request parameters.
	 */
	private void throwFailureException(final String errorMessage, final Map<String, String> parameters) {
		throwFailureException(errorMessage, parameters, -1, null)
	}
	
	/**
	 * Helper method to throw {@link FlickrServiceFailureException}.
	 * Will include provided error message text, code, request parameters and
	 * exception cause (if provided).
	 * 
	 * @param errorMessage - error message.
	 * @param parameters - request parameters.
	 * @param code - (if any) error code returned from the Flickr API.
	 * @param exception - (if any) cause exception.
	 */
	private void throwFailureException(final String errorMessage, final Map<String, String> parameters,
			final int code, final Exception exception) {
		final def ouput = "${errorMessage}${printParameters(parameters)}"
	
		log?.error(ouput)
		throw new FlickrServiceFailureException(ouput, code, exception)
	}
	
	/**
	 * Helper method to print the parameter map provided to the Flickr API request.
	 * 
	 * @param parameters - map containing the flickr request parameters.
	 * @return string representation of the parameter map.
	 */
	private def printParameters(final Map<String, String> parameters) {
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
