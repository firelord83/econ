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
import org.econ.domain.Cantiere;
import org.econ.repository.CantiereRepository;
import org.econ.repository.search.CantiereSearchRepository;
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
 * REST controller for managing {@link org.econ.domain.Cantiere}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CantiereResource {

    private final Logger log = LoggerFactory.getLogger(CantiereResource.class);

    private static final String ENTITY_NAME = "cantiere";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CantiereRepository cantiereRepository;

    private final CantiereSearchRepository cantiereSearchRepository;

    public CantiereResource(CantiereRepository cantiereRepository, CantiereSearchRepository cantiereSearchRepository) {
        this.cantiereRepository = cantiereRepository;
        this.cantiereSearchRepository = cantiereSearchRepository;
    }

    /**
     * {@code POST  /cantieres} : Create a new cantiere.
     *
     * @param cantiere the cantiere to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cantiere, or with status {@code 400 (Bad Request)} if the cantiere has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cantieres")
    public ResponseEntity<Cantiere> createCantiere(@Valid @RequestBody Cantiere cantiere) throws URISyntaxException {
        log.debug("REST request to save Cantiere : {}", cantiere);
        if (cantiere.getId() != null) {
            throw new BadRequestAlertException("A new cantiere cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Cantiere result = cantiereRepository.save(cantiere);
        cantiereSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/cantieres/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /cantieres/:id} : Updates an existing cantiere.
     *
     * @param id the id of the cantiere to save.
     * @param cantiere the cantiere to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cantiere,
     * or with status {@code 400 (Bad Request)} if the cantiere is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cantiere couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/cantieres/{id}")
    public ResponseEntity<Cantiere> updateCantiere(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Cantiere cantiere
    ) throws URISyntaxException {
        log.debug("REST request to update Cantiere : {}, {}", id, cantiere);
        if (cantiere.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cantiere.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cantiereRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Cantiere result = cantiereRepository.save(cantiere);
        cantiereSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cantiere.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /cantieres/:id} : Partial updates given fields of an existing cantiere, field will ignore if it is null
     *
     * @param id the id of the cantiere to save.
     * @param cantiere the cantiere to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cantiere,
     * or with status {@code 400 (Bad Request)} if the cantiere is not valid,
     * or with status {@code 404 (Not Found)} if the cantiere is not found,
     * or with status {@code 500 (Internal Server Error)} if the cantiere couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/cantieres/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Cantiere> partialUpdateCantiere(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Cantiere cantiere
    ) throws URISyntaxException {
        log.debug("REST request to partial update Cantiere partially : {}, {}", id, cantiere);
        if (cantiere.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cantiere.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cantiereRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Cantiere> result = cantiereRepository
            .findById(cantiere.getId())
            .map(existingCantiere -> {
                if (cantiere.getNomeCantiere() != null) {
                    existingCantiere.setNomeCantiere(cantiere.getNomeCantiere());
                }
                if (cantiere.getIndirizzo() != null) {
                    existingCantiere.setIndirizzo(cantiere.getIndirizzo());
                }

                return existingCantiere;
            })
            .map(cantiereRepository::save)
            .map(savedCantiere -> {
                cantiereSearchRepository.save(savedCantiere);

                return savedCantiere;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cantiere.getId().toString())
        );
    }

    /**
     * {@code GET  /cantieres} : get all the cantieres.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cantieres in body.
     */
    @GetMapping("/cantieres")
    public List<Cantiere> getAllCantieres() {
        log.debug("REST request to get all Cantieres");
        return cantiereRepository.findAll();
    }

    /**
     * {@code GET  /cantieres/:id} : get the "id" cantiere.
     *
     * @param id the id of the cantiere to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cantiere, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cantieres/{id}")
    public ResponseEntity<Cantiere> getCantiere(@PathVariable Long id) {
        log.debug("REST request to get Cantiere : {}", id);
        Optional<Cantiere> cantiere = cantiereRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(cantiere);
    }

    /**
     * {@code DELETE  /cantieres/:id} : delete the "id" cantiere.
     *
     * @param id the id of the cantiere to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cantieres/{id}")
    public ResponseEntity<Void> deleteCantiere(@PathVariable Long id) {
        log.debug("REST request to delete Cantiere : {}", id);
        cantiereRepository.deleteById(id);
        cantiereSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/cantieres?query=:query} : search for the cantiere corresponding
     * to the query.
     *
     * @param query the query of the cantiere search.
     * @return the result of the search.
     */
    @GetMapping("/_search/cantieres")
    public List<Cantiere> searchCantieres(@RequestParam String query) {
        log.debug("REST request to search Cantieres for query {}", query);
        return StreamSupport.stream(cantiereSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
