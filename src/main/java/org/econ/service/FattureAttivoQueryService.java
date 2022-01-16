package org.econ.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.econ.domain.*; // for static metamodels
import org.econ.domain.FattureAttivo;
import org.econ.repository.FattureAttivoRepository;
import org.econ.repository.search.FattureAttivoSearchRepository;
import org.econ.service.criteria.FattureAttivoCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link FattureAttivo} entities in the database.
 * The main input is a {@link FattureAttivoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FattureAttivo} or a {@link Page} of {@link FattureAttivo} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FattureAttivoQueryService extends QueryService<FattureAttivo> {

    private final Logger log = LoggerFactory.getLogger(FattureAttivoQueryService.class);

    private final FattureAttivoRepository fattureAttivoRepository;

    private final FattureAttivoSearchRepository fattureAttivoSearchRepository;

    public FattureAttivoQueryService(
        FattureAttivoRepository fattureAttivoRepository,
        FattureAttivoSearchRepository fattureAttivoSearchRepository
    ) {
        this.fattureAttivoRepository = fattureAttivoRepository;
        this.fattureAttivoSearchRepository = fattureAttivoSearchRepository;
    }

    /**
     * Return a {@link List} of {@link FattureAttivo} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FattureAttivo> findByCriteria(FattureAttivoCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<FattureAttivo> specification = createSpecification(criteria);
        return fattureAttivoRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link FattureAttivo} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FattureAttivo> findByCriteria(FattureAttivoCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<FattureAttivo> specification = createSpecification(criteria);
        return fattureAttivoRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FattureAttivoCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<FattureAttivo> specification = createSpecification(criteria);
        return fattureAttivoRepository.count(specification);
    }

    /**
     * Function to convert {@link FattureAttivoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<FattureAttivo> createSpecification(FattureAttivoCriteria criteria) {
        Specification<FattureAttivo> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), FattureAttivo_.id));
            }
            if (criteria.getNumeroFattura() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNumeroFattura(), FattureAttivo_.numeroFattura));
            }
            if (criteria.getRagSociale() != null) {
                specification = specification.and(buildStringSpecification(criteria.getRagSociale(), FattureAttivo_.ragSociale));
            }
            if (criteria.getNomeCliente() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNomeCliente(), FattureAttivo_.nomeCliente));
            }
            if (criteria.getImponibile() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getImponibile(), FattureAttivo_.imponibile));
            }
            if (criteria.getIva() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getIva(), FattureAttivo_.iva));
            }
            if (criteria.getStato() != null) {
                specification = specification.and(buildSpecification(criteria.getStato(), FattureAttivo_.stato));
            }
            if (criteria.getDataEmissione() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDataEmissione(), FattureAttivo_.dataEmissione));
            }
            if (criteria.getDataPagamento() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDataPagamento(), FattureAttivo_.dataPagamento));
            }
            if (criteria.getClienteId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getClienteId(),
                            root -> root.join(FattureAttivo_.cliente, JoinType.LEFT).get(Cliente_.id)
                        )
                    );
            }
            if (criteria.getCantiereId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCantiereId(),
                            root -> root.join(FattureAttivo_.cantiere, JoinType.LEFT).get(Cantiere_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
