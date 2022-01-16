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
import org.econ.domain.Fornitore;
import org.econ.repository.FornitoreRepository;
import org.econ.repository.search.FornitoreSearchRepository;
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
 * REST controller for managing {@link org.econ.domain.Fornitore}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FornitoreResource {

    private final Logger log = LoggerFactory.getLogger(FornitoreResource.class);

    private static final String ENTITY_NAME = "fornitore";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FornitoreRepository fornitoreRepository;

    private final FornitoreSearchRepository fornitoreSearchRepository;

    public FornitoreResource(FornitoreRepository fornitoreRepository, FornitoreSearchRepository fornitoreSearchRepository) {
        this.fornitoreRepository = fornitoreRepository;
        this.fornitoreSearchRepository = fornitoreSearchRepository;
    }

    /**
     * {@code POST  /fornitores} : Create a new fornitore.
     *
     * @param fornitore the fornitore to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fornitore, or with status {@code 400 (Bad Request)} if the fornitore has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/fornitores")
    public ResponseEntity<Fornitore> createFornitore(@Valid @RequestBody Fornitore fornitore) throws URISyntaxException {
        log.debug("REST request to save Fornitore : {}", fornitore);
        if (fornitore.getId() != null) {
            throw new BadRequestAlertException("A new fornitore cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Fornitore result = fornitoreRepository.save(fornitore);
        fornitoreSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/fornitores/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /fornitores/:id} : Updates an existing fornitore.
     *
     * @param id the id of the fornitore to save.
     * @param fornitore the fornitore to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fornitore,
     * or with status {@code 400 (Bad Request)} if the fornitore is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fornitore couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/fornitores/{id}")
    public ResponseEntity<Fornitore> updateFornitore(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Fornitore fornitore
    ) throws URISyntaxException {
        log.debug("REST request to update Fornitore : {}, {}", id, fornitore);
        if (fornitore.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fornitore.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fornitoreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Fornitore result = fornitoreRepository.save(fornitore);
        fornitoreSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fornitore.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /fornitores/:id} : Partial updates given fields of an existing fornitore, field will ignore if it is null
     *
     * @param id the id of the fornitore to save.
     * @param fornitore the fornitore to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fornitore,
     * or with status {@code 400 (Bad Request)} if the fornitore is not valid,
     * or with status {@code 404 (Not Found)} if the fornitore is not found,
     * or with status {@code 500 (Internal Server Error)} if the fornitore couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/fornitores/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Fornitore> partialUpdateFornitore(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Fornitore fornitore
    ) throws URISyntaxException {
        log.debug("REST request to partial update Fornitore partially : {}, {}", id, fornitore);
        if (fornitore.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fornitore.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fornitoreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Fornitore> result = fornitoreRepository
            .findById(fornitore.getId())
            .map(existingFornitore -> {
                if (fornitore.getNomeFornitore() != null) {
                    existingFornitore.setNomeFornitore(fornitore.getNomeFornitore());
                }
                if (fornitore.getIndirizzo() != null) {
                    existingFornitore.setIndirizzo(fornitore.getIndirizzo());
                }
                if (fornitore.getTipo() != null) {
                    existingFornitore.setTipo(fornitore.getTipo());
                }

                return existingFornitore;
            })
            .map(fornitoreRepository::save)
            .map(savedFornitore -> {
                fornitoreSearchRepository.save(savedFornitore);

                return savedFornitore;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fornitore.getId().toString())
        );
    }

    /**
     * {@code GET  /fornitores} : get all the fornitores.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fornitores in body.
     */
    @GetMapping("/fornitores")
    public List<Fornitore> getAllFornitores() {
        log.debug("REST request to get all Fornitores");
        return fornitoreRepository.findAll();
    }

    /**
     * {@code GET  /fornitores/:id} : get the "id" fornitore.
     *
     * @param id the id of the fornitore to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fornitore, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/fornitores/{id}")
    public ResponseEntity<Fornitore> getFornitore(@PathVariable Long id) {
        log.debug("REST request to get Fornitore : {}", id);
        Optional<Fornitore> fornitore = fornitoreRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(fornitore);
    }

    /**
     * {@code DELETE  /fornitores/:id} : delete the "id" fornitore.
     *
     * @param id the id of the fornitore to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/fornitores/{id}")
    public ResponseEntity<Void> deleteFornitore(@PathVariable Long id) {
        log.debug("REST request to delete Fornitore : {}", id);
        fornitoreRepository.deleteById(id);
        fornitoreSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/fornitores?query=:query} : search for the fornitore corresponding
     * to the query.
     *
     * @param query the query of the fornitore search.
     * @return the result of the search.
     */
    @GetMapping("/_search/fornitores")
    public List<Fornitore> searchFornitores(@RequestParam String query) {
        log.debug("REST request to search Fornitores for query {}", query);
        return StreamSupport.stream(fornitoreSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
