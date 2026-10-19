package com.dvlprmatheus.linko.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Table(name = "links")
@Entity
public class Link extends AbstractEntity {

  @Column(name = "destination_url", nullable = false)
  private String destinationUrl;

  @Column(name = "shortened_url", nullable = false, unique = true)
  private String shortenedUrl;

  @Column(name = "code", nullable = false, unique = true)
  private String code;

  @Column(name = "active", nullable = false)
  private boolean active;
}
