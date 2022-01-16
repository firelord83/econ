package org.econ.repository;

import org.econ.domain.FatturePassivo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the FatturePassivo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FatturePassivoRepository extends JpaRepository<FatturePassivo, Long> {}
