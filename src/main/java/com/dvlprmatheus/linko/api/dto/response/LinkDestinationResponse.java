package com.dvlprmatheus.linko.api.dto.response;

import com.dvlprmatheus.linko.entity.Link;

public record LinkDestinationResponse(String destinationUrl) {
  public LinkDestinationResponse(Link link) {
    this(link.getDestinationUrl());
  }
}
