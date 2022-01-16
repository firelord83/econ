package org.econ.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.econ.domain.FattureAttivo;
import org.econ.repository.FattureAttivoRepository;
import org.econ.repository.search.FattureAttivoSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link FattureAttivo}.
 */
@Service
@Transactional
public class FattureAttivoService {

    private final Logger log = LoggerFactory.getLogger(FattureAttivoService.class);

    private final FattureAttivoRepository fattureAttivoRepository;

    private final FattureAttivoSearchRepository fattureAttivoSearchRepository;

    public FattureAttivoService(
        FattureAttivoRepository fattureAttivoRepository,
        FattureAttivoSearchRepository fattureAttivoSearchRepository
    ) {
        this.fattureAttivoRepository = fattureAttivoRepository;
        this.fattureAttivoSearchRepository = fattureAttivoSearchRepository;
    }

    /**
     * Save a fattureAttivo.
     *
     * @param fattureAttivo the entity to save.
     * @return the persisted entity.
     */
    public FattureAttivo save(FattureAttivo fattureAttivo) {
        log.debug("Request to save FattureAttivo : {}", fattureAttivo);
        FattureAttivo result = fattureAttivoRepository.save(fattureAttivo);
        fattureAttivoSearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a fattureAttivo.
     *
     * @param fattureAttivo the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FattureAttivo> partialUpdate(FattureAttivo fattureAttivo) {
        log.debug("Request to partially update FattureAttivo : {}", fattureAttivo);

        return fattureAttivoRepository
            .findById(fattureAttivo.getId())
            .map(existingFattureAttivo -> {
                if (fattureAttivo.getNumeroFattura() != null) {
                    existingFattureAttivo.setNumeroFattura(fattureAttivo.getNumeroFattura());
                }
                if (fattureAttivo.getRagSociale() != null) {
                    existingFattureAttivo.setRagSociale(fattureAttivo.getRagSociale());
                }
                if (fattureAttivo.getNomeCliente() != null) {
                    existingFattureAttivo.setNomeCliente(fattureAttivo.getNomeCliente());
                }
                if (fattureAttivo.getImponibile() != null) {
                    existingFattureAttivo.setImponibile(fattureAttivo.getImponibile());
                }
                if (fattureAttivo.getIva() != null) {
                    existingFattureAttivo.setIva(fattureAttivo.getIva());
                }
                if (fattureAttivo.getStato() != null) {
                    existingFattureAttivo.setStato(fattureAttivo.getStato());
                }
                if (fattureAttivo.getDataEmissione() != null) {
                    existingFattureAttivo.setDataEmissione(fattureAttivo.getDataEmissione());
                }
                if (fattureAttivo.getDataPagamento() != null) {
                    existingFattureAttivo.setDataPagamento(fattureAttivo.getDataPagamento());
                }

                return existingFattureAttivo;
            })
            .map(fattureAttivoRepository::save)
            .map(savedFattureAttivo -> {
                fattureAttivoSearchRepository.save(savedFattureAttivo);

                return savedFattureAttivo;
            });
    }

    /**
     * Get all the fattureAttivos.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<FattureAttivo> findAll() {
        log.debug("Request to get all FattureAttivos");
        return fattureAttivoRepository.findAll();
    }

    /**
     * Get one fattureAttivo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FattureAttivo> findOne(Long id) {
        log.debug("Request to get FattureAttivo : {}", id);
        return fattureAttivoRepository.findById(id);
    }

    /**
     * Delete the fattureAttivo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete FattureAttivo : {}", id);
        fattureAttivoRepository.deleteById(id);
        fattureAttivoSearchRepository.deleteById(id);
    }

    /**
     * Search for the fattureAttivo corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<FattureAttivo> search(String query) {
        log.debug("Request to search FattureAttivos for query {}", query);
        return StreamSupport.stream(fattureAttivoSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
