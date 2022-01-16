package org.econ.repository;

import org.econ.domain.FattureAttivo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the FattureAttivo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FattureAttivoRepository extends JpaRepository<FattureAttivo, Long> {}
