package org.econ.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.econ.domain.FatturePassivo;
import org.econ.repository.FatturePassivoRepository;
import org.econ.repository.search.FatturePassivoSearchRepository;
import org.econ.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.econ.domain.FatturePassivo}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FatturePassivoResource {

    private final Logger log = LoggerFactory.getLogger(FatturePassivoResource.class);

    private static final String ENTITY_NAME = "fatturePassivo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FatturePassivoRepository fatturePassivoRepository;

    private final FatturePassivoSearchRepository fatturePassivoSearchRepository;

    public FatturePassivoResource(
        FatturePassivoRepository fatturePassivoRepository,
        FatturePassivoSearchRepository fatturePassivoSearchRepository
    ) {
        this.fatturePassivoRepository = fatturePassivoRepository;
        this.fatturePassivoSearchRepository = fatturePassivoSearchRepository;
    }

    /**
     * {@code POST  /fatture-passivos} : Create a new fatturePassivo.
     *
     * @param fatturePassivo the fatturePassivo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fatturePassivo, or with status {@code 400 (Bad Request)} if the fatturePassivo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/fatture-passivos")
    public ResponseEntity<FatturePassivo> createFatturePassivo(@Valid @RequestBody FatturePassivo fatturePassivo)
        throws URISyntaxException {
        log.debug("REST request to save FatturePassivo : {}", fatturePassivo);
        if (fatturePassivo.getId() != null) {
            throw new BadRequestAlertException("A new fatturePassivo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FatturePassivo result = fatturePassivoRepository.save(fatturePassivo);
        fatturePassivoSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/fatture-passivos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /fatture-passivos/:id} : Updates an existing fatturePassivo.
     *
     * @param id the id of the fatturePassivo to save.
     * @param fatturePassivo the fatturePassivo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fatturePassivo,
     * or with status {@code 400 (Bad Request)} if the fatturePassivo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fatturePassivo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/fatture-passivos/{id}")
    public ResponseEntity<FatturePassivo> updateFatturePassivo(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FatturePassivo fatturePassivo
    ) throws URISyntaxException {
        log.debug("REST request to update FatturePassivo : {}, {}", id, fatturePassivo);
        if (fatturePassivo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fatturePassivo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fatturePassivoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FatturePassivo result = fatturePassivoRepository.save(fatturePassivo);
        fatturePassivoSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fatturePassivo.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /fatture-passivos/:id} : Partial updates given fields of an existing fatturePassivo, field will ignore if it is null
     *
     * @param id the id of the fatturePassivo to save.
     * @param fatturePassivo the fatturePassivo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fatturePassivo,
     * or with status {@code 400 (Bad Request)} if the fatturePassivo is not valid,
     * or with status {@code 404 (Not Found)} if the fatturePassivo is not found,
     * or with status {@code 500 (Internal Server Error)} if the fatturePassivo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/fatture-passivos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FatturePassivo> partialUpdateFatturePassivo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FatturePassivo fatturePassivo
    ) throws URISyntaxException {
        log.debug("REST request to partial update FatturePassivo partially : {}, {}", id, fatturePassivo);
        if (fatturePassivo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fatturePassivo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fatturePassivoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FatturePassivo> result = fatturePassivoRepository
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

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fatturePassivo.getId().toString())
        );
    }

    /**
     * {@code GET  /fatture-passivos} : get all the fatturePassivos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fatturePassivos in body.
     */
    @GetMapping("/fatture-passivos")
    public List<FatturePassivo> getAllFatturePassivos() {
        log.debug("REST request to get all FatturePassivos");
        return fatturePassivoRepository.findAll();
    }

    /**
     * {@code GET  /fatture-passivos/:id} : get the "id" fatturePassivo.
     *
     * @param id the id of the fatturePassivo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fatturePassivo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/fatture-passivos/{id}")
    public ResponseEntity<FatturePassivo> getFatturePassivo(@PathVariable Long id) {
        log.debug("REST request to get FatturePassivo : {}", id);
        Optional<FatturePassivo> fatturePassivo = fatturePassivoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(fatturePassivo);
    }

    /**
     * {@code DELETE  /fatture-passivos/:id} : delete the "id" fatturePassivo.
     *
     * @param id the id of the fatturePassivo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/fatture-passivos/{id}")
    public ResponseEntity<Void> deleteFatturePassivo(@PathVariable Long id) {
        log.debug("REST request to delete FatturePassivo : {}", id);
        fatturePassivoRepository.deleteById(id);
        fatturePassivoSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/fatture-passivos?query=:query} : search for the fatturePassivo corresponding
     * to the query.
     *
     * @param query the query of the fatturePassivo search.
     * @return the result of the search.
     */
    @GetMapping("/_search/fatture-passivos")
    public List<FatturePassivo> searchFatturePassivos(@RequestParam String query) {
        log.debug("REST request to search FatturePassivos for query {}", query);
        return StreamSupport.stream(fatturePassivoSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
