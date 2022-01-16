package org.econ.repository;

import org.econ.domain.Fornitore;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Fornitore entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FornitoreRepository extends JpaRepository<Fornitore, Long> {}
