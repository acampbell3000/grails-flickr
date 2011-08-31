package uk.co.anthonycampbell.grails.flickr.service.client

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
 * Service Failure Exception
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
public class ServiceFailureException extends Exception {
    // Declare exception properties
    private final String message
	private final int code

    /**
     * Constructor.
     *
     * @param message the exception message.
     */
    public ServiceFailureException(final String message) {
        this(message, null)
    }

    /**
     * Constructor.
     *
     * @param message the exception message.
     * @param exception the wrapped exception.
     */
    public ServiceFailureException(final String message, final def code) {
        this(message, code, null)
    }

    /**
     * Constructor.
     *
     * @param message the exception message.
     * @param exception the wrapped exception.
     */
    public ServiceFailureException(final String message, final def code, final Exception exception) {
        super(message, exception)
        this.message = message
		this.code = (code instanceof Number ? code :
			(code instanceof String && code?.asLong() ? code?.toLong() : -1))
    }

    /**
     * Return the exception error message.
     *
     * @return the exception error message.
     */
    public String getMessage() {
       return this.message
    }

    /**
     * Return the exception error code..
     *
     * @return error code associated with the message.
     */
    public int getCode() {
       return this.code
    }
}