package com.dvlprmatheus.linko.repository;

import com.dvlprmatheus.linko.api.dto.projection.LinkCodeProjection;
import com.dvlprmatheus.linko.entity.Link;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LinkRepository extends JpaRepository<Link, UUID> {
  Optional<Link> findByCodeAndActiveTrue(String code);

  boolean existsByCode(String code);

  @Query(
      """
    SELECT l.code AS code
    FROM Link l
    WHERE l.active = true
      AND l.createdAt < :expirationDate
    """)
  List<LinkCodeProjection> findExpiredActiveCodes(@Param("expirationDate") Instant expirationDate);
}
