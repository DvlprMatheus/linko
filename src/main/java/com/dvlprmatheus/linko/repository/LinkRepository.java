package com.dvlprmatheus.linko.repository;

import com.dvlprmatheus.linko.entity.Link;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LinkRepository extends JpaRepository<Link, UUID> {
  Optional<Link> findByCodeAndActiveTrue(String code);

  boolean existsByCode(String code);

  @Modifying
  @Query(
      """
            UPDATE Link l
            SET l.active = false
            WHERE l.active = true
                AND l.createdAt < :expirationDate
            """)
  int deactivateExpiredLinks(@Param("expirationDate") Instant expirationDate);
}
