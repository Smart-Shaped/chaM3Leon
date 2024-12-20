package com.smartshaped.chameleon.harvester.request;

/**
 * The RequestValidator class is responsible for validating requests.
 * It checks that the content and harvester IDs are not blank.
 */
public class RequestValidator {
	
	
    /**
     * Validates the request by checking that its content and harvester IDs are not blank.
     *
     * @param request The request to be validated.
     * @return true if the content and harvester IDs are not blank, false otherwise.
     */
	public boolean isRequestValid(Request request) {

        return !request.getContent().isBlank() && !request.getHarvesterIds().isBlank();

    }

}
