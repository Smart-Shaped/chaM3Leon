package com.smartshaped.chameleon.harvester.request;

/**
 * The RequestValidator class is responsible for validating requests. It checks that the content and
 * harvester IDs are not blank.
 */
public class RequestValidator {

  /**
   * Validates the request by checking that its content and harvester IDs are not blank.
   *
   * @param request The request to be validated.
   * @return true if the content and harvester IDs are not blank, false otherwise.
   */
  public boolean isRequestValid(Request request) {

    if (request == null) {
      return false;
    }

    if (request.getId() == null) {
      return false;
    }

    if (request.getState() == null || request.getState().isBlank()) {
      return false;
    }
    String state = request.getState().toLowerCase();
    if (!state.equals("completed") && !state.equals("false") && !state.equals("error")) {
      return false;
    }

    if (request.getContent() == null || request.getContent().isBlank()) {
      return false;
    }

    return request.getHarvesterIds() != null && !request.getHarvesterIds().isBlank();
  }
}
