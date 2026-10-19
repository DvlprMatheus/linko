package com.dvlprmatheus.linko.api.dto.response;

import com.dvlprmatheus.linko.entity.Link;

public record LinkShortenedResponse(String shortenedUrl) {
  public LinkShortenedResponse(Link link) {
    this(link.getShortenedUrl());
  }
}
