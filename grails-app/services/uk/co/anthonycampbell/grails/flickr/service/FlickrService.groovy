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

import org.codehaus.groovy.grails.web.json.*
import org.springframework.beans.factory.InitializingBean
import org.springframework.util.StringUtils

import uk.co.anthonycampbell.grails.flickr.service.client.FlickrRequest
import uk.co.anthonycampbell.grails.flickr.service.client.FlickrResponse
import uk.co.anthonycampbell.grails.flickr.service.client.FlickrServiceFailureException

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
    
	//http://api.flickr.com/services/rest?method=flickr.photosets.getList&api_key=3d4442864a12fda6f7d5bc49da98135c&user_id=587922ar54@N02

	def getSets() throws FlickrServiceFailureException {
        if (this.serviceInitialised) {
			
			log?.debug "Retrieving sets for ${this.flickrUserId}..."
			
			// Request
			final def jsonResponse = getFlickr([method: FlickrRequest.GET_SET_LIST.getMethod()])
			
			log?.debug "Response recieved"
			
			// Validate response
			if (jsonResponse) {
				// Read response
				final String responseText = jsonResponse.getText();
				if (StringUtils.hasText(responseText)) {
					final def json = JSON.parse(responseText)
					
					// Validate
					if (json && json?.stat == FlickrResponse.OK.getState() && json?.photosets) {
						
						// Parse JSON
						json?.photosets?.photoset?.each {
							log.error "Value: ${it}"
							log.error ""
						}
						
					} else {
						throwError(json)
					}
				}
			}
			
			// return list
			
        } else {
            final def errorMessage = "Unable to retrieve Flickr sets. Some of the plug-in " +
                "configuration is missing. Please refer to the documentation and ensure " +
                "you have declared all of the required configuration."
        
            log?.error(errorMessage)
            throw new FlickrServiceFailureException(errorMessage)
        }
	}
	
	/**
	 * Helper method to perform Flickr API HTTP REST GET request.
	 * 
	 * @param parameters Flickr request parameters.
	 * @return JSON response.
	 * @throws FlickrServiceFailureException Flickr HTTP REST request failed.
	 */
	private def getFlickr(final Map<String, String> parameters)
			throws FlickrServiceFailureException {
		// Validate
		if (parameters && !parameters.isEmpty()) {
			// Add configuration
			parameters["api_key"] = this.flickrApiKey
			parameters["user_id"] = this.flickrUserId
			parameters["format"] = "json"
			parameters["nojsoncallback"] = "1"
			
			try {
				// Rest client
				return withHttp(uri: FLICKR_API_REST_URL) {
					get(path: '/services/rest',
						query : parameters)
				}
			} catch (Exception ex) {
				final def errorMessage = "Flickr HTTP request failed. (method=${parameters?.method})"
			
				log?.error(errorMessage)
				throw new FlickrServiceFailureException(errorMessage, ex)
			}
		} else {
			final def errorMessage =
				"Unable to perform Flickr request. Flickr API method not provided."
		
			log?.error(errorMessage)
			throw new FlickrServiceFailureException(errorMessage, ex)
		}
	}
	
	/**
	 * Process error JSON error response and throw checked exception.
	 * 
	 * @param json - error response to process.
	 * @throws FlickrServiceFailureException Contains error message included
	 * 		in the JSON response.
	 */
	private void throwError(final def json) throws FlickrServiceFailureException {
		// Initialise error message
		def errorMessage
		
		// Validate
		if (json && json?.message) {
			errorMessage = "Unable to retrieve Flickr sets. ${json?.message}."
		} else {
			errorMessage = "Unable to retrieve Flickr sets. Unknown error occured."
		}
	
		log?.error(errorMessage)
		throw new FlickrServiceFailureException(errorMessage)
	}

    /**
     * Initialise configuration properties.
     */
    @Override
    void afterPropertiesSet() {
        log?.info "Initialising the ${this.getClass().getSimpleName()}..."
        
        prepareConfig()
    }

    /**
     * Attempt to load Flickr API connection details available in the
     * grails-app/conf/Config.groovy file.
     *
     * @return whether configuration successfully loaded.
     */
    boolean prepareConfig() {
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
}
