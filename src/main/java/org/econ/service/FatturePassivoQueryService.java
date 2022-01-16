package org.econ.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.econ.domain.*; // for static metamodels
import org.econ.domain.FatturePassivo;
import org.econ.repository.FatturePassivoRepository;
import org.econ.repository.search.FatturePassivoSearchRepository;
import org.econ.service.criteria.FatturePassivoCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link FatturePassivo} entities in the database.
 * The main input is a {@link FatturePassivoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FatturePassivo} or a {@link Page} of {@link FatturePassivo} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FatturePassivoQueryService extends QueryService<FatturePassivo> {

    private final Logger log = LoggerFactory.getLogger(FatturePassivoQueryService.class);

    private final FatturePassivoRepository fatturePassivoRepository;

    private final FatturePassivoSearchRepository fatturePassivoSearchRepository;

    public FatturePassivoQueryService(
        FatturePassivoRepository fatturePassivoRepository,
        FatturePassivoSearchRepository fatturePassivoSearchRepository
    ) {
        this.fatturePassivoRepository = fatturePassivoRepository;
        this.fatturePassivoSearchRepository = fatturePassivoSearchRepository;
    }

    /**
     * Return a {@link List} of {@link FatturePassivo} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FatturePassivo> findByCriteria(FatturePassivoCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<FatturePassivo> specification = createSpecification(criteria);
        return fatturePassivoRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link FatturePassivo} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FatturePassivo> findByCriteria(FatturePassivoCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<FatturePassivo> specification = createSpecification(criteria);
        return fatturePassivoRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FatturePassivoCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<FatturePassivo> specification = createSpecification(criteria);
        return fatturePassivoRepository.count(specification);
    }

    /**
     * Function to convert {@link FatturePassivoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<FatturePassivo> createSpecification(FatturePassivoCriteria criteria) {
        Specification<FatturePassivo> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), FatturePassivo_.id));
            }
            if (criteria.getNumeroFattura() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNumeroFattura(), FatturePassivo_.numeroFattura));
            }
            if (criteria.getRagSociale() != null) {
                specification = specification.and(buildStringSpecification(criteria.getRagSociale(), FatturePassivo_.ragSociale));
            }
            if (criteria.getNomeFornitore() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNomeFornitore(), FatturePassivo_.nomeFornitore));
            }
            if (criteria.getImponibile() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getImponibile(), FatturePassivo_.imponibile));
            }
            if (criteria.getIva() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getIva(), FatturePassivo_.iva));
            }
            if (criteria.getStato() != null) {
                specification = specification.and(buildSpecification(criteria.getStato(), FatturePassivo_.stato));
            }
            if (criteria.getDataEmissione() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDataEmissione(), FatturePassivo_.dataEmissione));
            }
            if (criteria.getDataPagamento() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDataPagamento(), FatturePassivo_.dataPagamento));
            }
            if (criteria.getFornitoreId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getFornitoreId(),
                            root -> root.join(FatturePassivo_.fornitore, JoinType.LEFT).get(Fornitore_.id)
                        )
                    );
            }
            if (criteria.getCantiereId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCantiereId(),
                            root -> root.join(FatturePassivo_.cantiere, JoinType.LEFT).get(Cantiere_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
