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
import org.econ.domain.FatturePassivo;
import org.econ.repository.FatturePassivoRepository;
import org.econ.service.FatturePassivoQueryService;
import org.econ.service.FatturePassivoService;
import org.econ.service.criteria.FatturePassivoCriteria;
import org.econ.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.econ.domain.FatturePassivo}.
 */
@RestController
@RequestMapping("/api")
public class FatturePassivoResource {

    private final Logger log = LoggerFactory.getLogger(FatturePassivoResource.class);

    private static final String ENTITY_NAME = "fatturePassivo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FatturePassivoService fatturePassivoService;

    private final FatturePassivoRepository fatturePassivoRepository;

    private final FatturePassivoQueryService fatturePassivoQueryService;

    public FatturePassivoResource(
        FatturePassivoService fatturePassivoService,
        FatturePassivoRepository fatturePassivoRepository,
        FatturePassivoQueryService fatturePassivoQueryService
    ) {
        this.fatturePassivoService = fatturePassivoService;
        this.fatturePassivoRepository = fatturePassivoRepository;
        this.fatturePassivoQueryService = fatturePassivoQueryService;
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
        FatturePassivo result = fatturePassivoService.save(fatturePassivo);
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

        FatturePassivo result = fatturePassivoService.save(fatturePassivo);
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

        Optional<FatturePassivo> result = fatturePassivoService.partialUpdate(fatturePassivo);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fatturePassivo.getId().toString())
        );
    }

    /**
     * {@code GET  /fatture-passivos} : get all the fatturePassivos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fatturePassivos in body.
     */
    @GetMapping("/fatture-passivos")
    public ResponseEntity<List<FatturePassivo>> getAllFatturePassivos(FatturePassivoCriteria criteria) {
        log.debug("REST request to get FatturePassivos by criteria: {}", criteria);
        List<FatturePassivo> entityList = fatturePassivoQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /fatture-passivos/count} : count all the fatturePassivos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/fatture-passivos/count")
    public ResponseEntity<Long> countFatturePassivos(FatturePassivoCriteria criteria) {
        log.debug("REST request to count FatturePassivos by criteria: {}", criteria);
        return ResponseEntity.ok().body(fatturePassivoQueryService.countByCriteria(criteria));
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
        Optional<FatturePassivo> fatturePassivo = fatturePassivoService.findOne(id);
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
        fatturePassivoService.delete(id);
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
        return fatturePassivoService.search(query);
    }
}
