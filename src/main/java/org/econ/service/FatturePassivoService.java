package org.econ.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.econ.domain.FatturePassivo;
import org.econ.repository.FatturePassivoRepository;
import org.econ.repository.search.FatturePassivoSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link FatturePassivo}.
 */
@Service
@Transactional
public class FatturePassivoService {

    private final Logger log = LoggerFactory.getLogger(FatturePassivoService.class);

    private final FatturePassivoRepository fatturePassivoRepository;

    private final FatturePassivoSearchRepository fatturePassivoSearchRepository;

    public FatturePassivoService(
        FatturePassivoRepository fatturePassivoRepository,
        FatturePassivoSearchRepository fatturePassivoSearchRepository
    ) {
        this.fatturePassivoRepository = fatturePassivoRepository;
        this.fatturePassivoSearchRepository = fatturePassivoSearchRepository;
    }

    /**
     * Save a fatturePassivo.
     *
     * @param fatturePassivo the entity to save.
     * @return the persisted entity.
     */
    public FatturePassivo save(FatturePassivo fatturePassivo) {
        log.debug("Request to save FatturePassivo : {}", fatturePassivo);
        FatturePassivo result = fatturePassivoRepository.save(fatturePassivo);
        fatturePassivoSearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a fatturePassivo.
     *
     * @param fatturePassivo the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FatturePassivo> partialUpdate(FatturePassivo fatturePassivo) {
        log.debug("Request to partially update FatturePassivo : {}", fatturePassivo);

        return fatturePassivoRepository
            .findById(fatturePassivo.getId())
            .map(existingFatturePassivo -> {
                if (fatturePassivo.getNumeroFattura() != null) {
                    existingFatturePassivo.setNumeroFattura(fatturePassivo.getNumeroFattura());
                }
                if (fatturePassivo.getRagSociale() != null) {
                    existingFatturePassivo.setRagSociale(fatturePassivo.getRagSociale());
                }
                if (fatturePassivo.getNomeFornitore() != null) {
                    existingFatturePassivo.setNomeFornitore(fatturePassivo.getNomeFornitore());
                }
                if (fatturePassivo.getImponibile() != null) {
                    existingFatturePassivo.setImponibile(fatturePassivo.getImponibile());
                }
                if (fatturePassivo.getIva() != null) {
                    existingFatturePassivo.setIva(fatturePassivo.getIva());
                }
                if (fatturePassivo.getStato() != null) {
                    existingFatturePassivo.setStato(fatturePassivo.getStato());
                }
                if (fatturePassivo.getDataEmissione() != null) {
                    existingFatturePassivo.setDataEmissione(fatturePassivo.getDataEmissione());
                }
                if (fatturePassivo.getDataPagamento() != null) {
                    existingFatturePassivo.setDataPagamento(fatturePassivo.getDataPagamento());
                }

                return existingFatturePassivo;
            })
            .map(fatturePassivoRepository::save)
            .map(savedFatturePassivo -> {
                fatturePassivoSearchRepository.save(savedFatturePassivo);

                return savedFatturePassivo;
            });
    }

    /**
     * Get all the fatturePassivos.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<FatturePassivo> findAll() {
        log.debug("Request to get all FatturePassivos");
        return fatturePassivoRepository.findAll();
    }

    /**
     * Get one fatturePassivo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FatturePassivo> findOne(Long id) {
        log.debug("Request to get FatturePassivo : {}", id);
        return fatturePassivoRepository.findById(id);
    }

    /**
     * Delete the fatturePassivo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete FatturePassivo : {}", id);
        fatturePassivoRepository.deleteById(id);
        fatturePassivoSearchRepository.deleteById(id);
    }

    /**
     * Search for the fatturePassivo corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<FatturePassivo> search(String query) {
        log.debug("Request to search FatturePassivos for query {}", query);
        return StreamSupport.stream(fatturePassivoSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
