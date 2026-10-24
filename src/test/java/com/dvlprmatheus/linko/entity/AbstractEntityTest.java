package com.dvlprmatheus.linko.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Abstract entity lifecycle")
class AbstractEntityTest {

  @Test
  @DisplayName("Sets the creation timestamp before inserting an entity")
  void setsCreatedAtBeforeInsert() {
    Link link = new Link();
    Instant beforePersist = Instant.now();

    link.prePersist();

    assertNotNull(link.getCreatedAt());
    assertFalse(link.getCreatedAt().isBefore(beforePersist));
    assertFalse(link.getCreatedAt().isAfter(Instant.now()));
  }

  @Test
  @DisplayName("Sets the update timestamp without changing the creation timestamp")
  void setsUpdatedAtBeforeUpdate() {
    Link link = new Link();
    Instant createdAt = Instant.parse("2025-01-01T00:00:00Z");
    link.setCreatedAt(createdAt);
    Instant beforeUpdate = Instant.now();

    link.preUpdate();

    assertEquals(createdAt, link.getCreatedAt());
    assertNotNull(link.getUpdatedAt());
    assertFalse(link.getUpdatedAt().isBefore(beforeUpdate));
    assertFalse(link.getUpdatedAt().isAfter(Instant.now()));
  }
}
