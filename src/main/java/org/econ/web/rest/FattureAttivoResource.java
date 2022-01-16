package org.econ.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.econ.domain.FattureAttivo;
import org.econ.repository.FattureAttivoRepository;
import org.econ.service.FattureAttivoQueryService;
import org.econ.service.FattureAttivoService;
import org.econ.service.criteria.FattureAttivoCriteria;
import org.econ.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.econ.domain.FattureAttivo}.
 */
@RestController
@RequestMapping("/api")
public class FattureAttivoResource {

    private final Logger log = LoggerFactory.getLogger(FattureAttivoResource.class);

    private static final String ENTITY_NAME = "fattureAttivo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FattureAttivoService fattureAttivoService;

    private final FattureAttivoRepository fattureAttivoRepository;

    private final FattureAttivoQueryService fattureAttivoQueryService;

    public FattureAttivoResource(
        FattureAttivoService fattureAttivoService,
        FattureAttivoRepository fattureAttivoRepository,
        FattureAttivoQueryService fattureAttivoQueryService
    ) {
        this.fattureAttivoService = fattureAttivoService;
        this.fattureAttivoRepository = fattureAttivoRepository;
        this.fattureAttivoQueryService = fattureAttivoQueryService;
    }

    /**
     * {@code POST  /fatture-attivos} : Create a new fattureAttivo.
     *
     * @param fattureAttivo the fattureAttivo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fattureAttivo, or with status {@code 400 (Bad Request)} if the fattureAttivo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/fatture-attivos")
    public ResponseEntity<FattureAttivo> createFattureAttivo(@Valid @RequestBody FattureAttivo fattureAttivo) throws URISyntaxException {
        log.debug("REST request to save FattureAttivo : {}", fattureAttivo);
        if (fattureAttivo.getId() != null) {
            throw new BadRequestAlertException("A new fattureAttivo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FattureAttivo result = fattureAttivoService.save(fattureAttivo);
        return ResponseEntity
            .created(new URI("/api/fatture-attivos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /fatture-attivos/:id} : Updates an existing fattureAttivo.
     *
     * @param id the id of the fattureAttivo to save.
     * @param fattureAttivo the fattureAttivo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fattureAttivo,
     * or with status {@code 400 (Bad Request)} if the fattureAttivo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fattureAttivo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/fatture-attivos/{id}")
    public ResponseEntity<FattureAttivo> updateFattureAttivo(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FattureAttivo fattureAttivo
    ) throws URISyntaxException {
        log.debug("REST request to update FattureAttivo : {}, {}", id, fattureAttivo);
        if (fattureAttivo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fattureAttivo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fattureAttivoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FattureAttivo result = fattureAttivoService.save(fattureAttivo);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fattureAttivo.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /fatture-attivos/:id} : Partial updates given fields of an existing fattureAttivo, field will ignore if it is null
     *
     * @param id the id of the fattureAttivo to save.
     * @param fattureAttivo the fattureAttivo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fattureAttivo,
     * or with status {@code 400 (Bad Request)} if the fattureAttivo is not valid,
     * or with status {@code 404 (Not Found)} if the fattureAttivo is not found,
     * or with status {@code 500 (Internal Server Error)} if the fattureAttivo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/fatture-attivos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FattureAttivo> partialUpdateFattureAttivo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FattureAttivo fattureAttivo
    ) throws URISyntaxException {
        log.debug("REST request to partial update FattureAttivo partially : {}, {}", id, fattureAttivo);
        if (fattureAttivo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fattureAttivo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fattureAttivoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FattureAttivo> result = fattureAttivoService.partialUpdate(fattureAttivo);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fattureAttivo.getId().toString())
        );
    }

    /**
     * {@code GET  /fatture-attivos} : get all the fattureAttivos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fattureAttivos in body.
     */
    @GetMapping("/fatture-attivos")
    public ResponseEntity<List<FattureAttivo>> getAllFattureAttivos(FattureAttivoCriteria criteria) {
        log.debug("REST request to get FattureAttivos by criteria: {}", criteria);
        List<FattureAttivo> entityList = fattureAttivoQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /fatture-attivos/count} : count all the fattureAttivos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/fatture-attivos/count")
    public ResponseEntity<Long> countFattureAttivos(FattureAttivoCriteria criteria) {
        log.debug("REST request to count FattureAttivos by criteria: {}", criteria);
        return ResponseEntity.ok().body(fattureAttivoQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /fatture-attivos/:id} : get the "id" fattureAttivo.
     *
     * @param id the id of the fattureAttivo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fattureAttivo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/fatture-attivos/{id}")
    public ResponseEntity<FattureAttivo> getFattureAttivo(@PathVariable Long id) {
        log.debug("REST request to get FattureAttivo : {}", id);
        Optional<FattureAttivo> fattureAttivo = fattureAttivoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fattureAttivo);
    }

    /**
     * {@code DELETE  /fatture-attivos/:id} : delete the "id" fattureAttivo.
     *
     * @param id the id of the fattureAttivo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/fatture-attivos/{id}")
    public ResponseEntity<Void> deleteFattureAttivo(@PathVariable Long id) {
        log.debug("REST request to delete FattureAttivo : {}", id);
        fattureAttivoService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/fatture-attivos?query=:query} : search for the fattureAttivo corresponding
     * to the query.
     *
     * @param query the query of the fattureAttivo search.
     * @return the result of the search.
     */
    @GetMapping("/_search/fatture-attivos")
    public List<FattureAttivo> searchFattureAttivos(@RequestParam String query) {
        log.debug("REST request to search FattureAttivos for query {}", query);
        return fattureAttivoService.search(query);
    }
}
