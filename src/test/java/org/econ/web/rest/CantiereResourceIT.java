package org.econ.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.econ.IntegrationTest;
import org.econ.domain.Cantiere;
import org.econ.repository.CantiereRepository;
import org.econ.repository.search.CantiereSearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CantiereResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CantiereResourceIT {

    private static final String DEFAULT_NOME_CANTIERE = "AAAAAAAAAA";
    private static final String UPDATED_NOME_CANTIERE = "BBBBBBBBBB";

    private static final String DEFAULT_INDIRIZZO = "AAAAAAAAAA";
    private static final String UPDATED_INDIRIZZO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cantieres";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/cantieres";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CantiereRepository cantiereRepository;

    /**
     * This repository is mocked in the org.econ.repository.search test package.
     *
     * @see org.econ.repository.search.CantiereSearchRepositoryMockConfiguration
     */
    @Autowired
    private CantiereSearchRepository mockCantiereSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCantiereMockMvc;

    private Cantiere cantiere;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cantiere createEntity(EntityManager em) {
        Cantiere cantiere = new Cantiere().nomeCantiere(DEFAULT_NOME_CANTIERE).indirizzo(DEFAULT_INDIRIZZO);
        return cantiere;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cantiere createUpdatedEntity(EntityManager em) {
        Cantiere cantiere = new Cantiere().nomeCantiere(UPDATED_NOME_CANTIERE).indirizzo(UPDATED_INDIRIZZO);
        return cantiere;
    }

    @BeforeEach
    public void initTest() {
        cantiere = createEntity(em);
    }

    @Test
    @Transactional
    void createCantiere() throws Exception {
        int databaseSizeBeforeCreate = cantiereRepository.findAll().size();
        // Create the Cantiere
        restCantiereMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cantiere)))
            .andExpect(status().isCreated());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeCreate + 1);
        Cantiere testCantiere = cantiereList.get(cantiereList.size() - 1);
        assertThat(testCantiere.getNomeCantiere()).isEqualTo(DEFAULT_NOME_CANTIERE);
        assertThat(testCantiere.getIndirizzo()).isEqualTo(DEFAULT_INDIRIZZO);

        // Validate the Cantiere in Elasticsearch
        verify(mockCantiereSearchRepository, times(1)).save(testCantiere);
    }

    @Test
    @Transactional
    void createCantiereWithExistingId() throws Exception {
        // Create the Cantiere with an existing ID
        cantiere.setId(1L);

        int databaseSizeBeforeCreate = cantiereRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCantiereMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cantiere)))
            .andExpect(status().isBadRequest());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeCreate);

        // Validate the Cantiere in Elasticsearch
        verify(mockCantiereSearchRepository, times(0)).save(cantiere);
    }

    @Test
    @Transactional
    void checkNomeCantiereIsRequired() throws Exception {
        int databaseSizeBeforeTest = cantiereRepository.findAll().size();
        // set the field null
        cantiere.setNomeCantiere(null);

        // Create the Cantiere, which fails.

        restCantiereMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cantiere)))
            .andExpect(status().isBadRequest());

        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCantieres() throws Exception {
        // Initialize the database
        cantiereRepository.saveAndFlush(cantiere);

        // Get all the cantiereList
        restCantiereMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cantiere.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomeCantiere").value(hasItem(DEFAULT_NOME_CANTIERE)))
            .andExpect(jsonPath("$.[*].indirizzo").value(hasItem(DEFAULT_INDIRIZZO)));
    }

    @Test
    @Transactional
    void getCantiere() throws Exception {
        // Initialize the database
        cantiereRepository.saveAndFlush(cantiere);

        // Get the cantiere
        restCantiereMockMvc
            .perform(get(ENTITY_API_URL_ID, cantiere.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cantiere.getId().intValue()))
            .andExpect(jsonPath("$.nomeCantiere").value(DEFAULT_NOME_CANTIERE))
            .andExpect(jsonPath("$.indirizzo").value(DEFAULT_INDIRIZZO));
    }

    @Test
    @Transactional
    void getNonExistingCantiere() throws Exception {
        // Get the cantiere
        restCantiereMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCantiere() throws Exception {
        // Initialize the database
        cantiereRepository.saveAndFlush(cantiere);

        int databaseSizeBeforeUpdate = cantiereRepository.findAll().size();

        // Update the cantiere
        Cantiere updatedCantiere = cantiereRepository.findById(cantiere.getId()).get();
        // Disconnect from session so that the updates on updatedCantiere are not directly saved in db
        em.detach(updatedCantiere);
        updatedCantiere.nomeCantiere(UPDATED_NOME_CANTIERE).indirizzo(UPDATED_INDIRIZZO);

        restCantiereMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCantiere.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCantiere))
            )
            .andExpect(status().isOk());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeUpdate);
        Cantiere testCantiere = cantiereList.get(cantiereList.size() - 1);
        assertThat(testCantiere.getNomeCantiere()).isEqualTo(UPDATED_NOME_CANTIERE);
        assertThat(testCantiere.getIndirizzo()).isEqualTo(UPDATED_INDIRIZZO);

        // Validate the Cantiere in Elasticsearch
        verify(mockCantiereSearchRepository).save(testCantiere);
    }

    @Test
    @Transactional
    void putNonExistingCantiere() throws Exception {
        int databaseSizeBeforeUpdate = cantiereRepository.findAll().size();
        cantiere.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCantiereMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cantiere.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cantiere))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Cantiere in Elasticsearch
        verify(mockCantiereSearchRepository, times(0)).save(cantiere);
    }

    @Test
    @Transactional
    void putWithIdMismatchCantiere() throws Exception {
        int databaseSizeBeforeUpdate = cantiereRepository.findAll().size();
        cantiere.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCantiereMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cantiere))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Cantiere in Elasticsearch
        verify(mockCantiereSearchRepository, times(0)).save(cantiere);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCantiere() throws Exception {
        int databaseSizeBeforeUpdate = cantiereRepository.findAll().size();
        cantiere.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCantiereMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cantiere)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Cantiere in Elasticsearch
        verify(mockCantiereSearchRepository, times(0)).save(cantiere);
    }

    @Test
    @Transactional
    void partialUpdateCantiereWithPatch() throws Exception {
        // Initialize the database
        cantiereRepository.saveAndFlush(cantiere);

        int databaseSizeBeforeUpdate = cantiereRepository.findAll().size();

        // Update the cantiere using partial update
        Cantiere partialUpdatedCantiere = new Cantiere();
        partialUpdatedCantiere.setId(cantiere.getId());

        partialUpdatedCantiere.indirizzo(UPDATED_INDIRIZZO);

        restCantiereMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCantiere.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCantiere))
            )
            .andExpect(status().isOk());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeUpdate);
        Cantiere testCantiere = cantiereList.get(cantiereList.size() - 1);
        assertThat(testCantiere.getNomeCantiere()).isEqualTo(DEFAULT_NOME_CANTIERE);
        assertThat(testCantiere.getIndirizzo()).isEqualTo(UPDATED_INDIRIZZO);
    }

    @Test
    @Transactional
    void fullUpdateCantiereWithPatch() throws Exception {
        // Initialize the database
        cantiereRepository.saveAndFlush(cantiere);

        int databaseSizeBeforeUpdate = cantiereRepository.findAll().size();

        // Update the cantiere using partial update
        Cantiere partialUpdatedCantiere = new Cantiere();
        partialUpdatedCantiere.setId(cantiere.getId());

        partialUpdatedCantiere.nomeCantiere(UPDATED_NOME_CANTIERE).indirizzo(UPDATED_INDIRIZZO);

        restCantiereMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCantiere.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCantiere))
            )
            .andExpect(status().isOk());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeUpdate);
        Cantiere testCantiere = cantiereList.get(cantiereList.size() - 1);
        assertThat(testCantiere.getNomeCantiere()).isEqualTo(UPDATED_NOME_CANTIERE);
        assertThat(testCantiere.getIndirizzo()).isEqualTo(UPDATED_INDIRIZZO);
    }

    @Test
    @Transactional
    void patchNonExistingCantiere() throws Exception {
        int databaseSizeBeforeUpdate = cantiereRepository.findAll().size();
        cantiere.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCantiereMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cantiere.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cantiere))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Cantiere in Elasticsearch
        verify(mockCantiereSearchRepository, times(0)).save(cantiere);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCantiere() throws Exception {
        int databaseSizeBeforeUpdate = cantiereRepository.findAll().size();
        cantiere.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCantiereMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cantiere))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Cantiere in Elasticsearch
        verify(mockCantiereSearchRepository, times(0)).save(cantiere);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCantiere() throws Exception {
        int databaseSizeBeforeUpdate = cantiereRepository.findAll().size();
        cantiere.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCantiereMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(cantiere)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Cantiere in Elasticsearch
        verify(mockCantiereSearchRepository, times(0)).save(cantiere);
    }

    @Test
    @Transactional
    void deleteCantiere() throws Exception {
        // Initialize the database
        cantiereRepository.saveAndFlush(cantiere);

        int databaseSizeBeforeDelete = cantiereRepository.findAll().size();

        // Delete the cantiere
        restCantiereMockMvc
            .perform(delete(ENTITY_API_URL_ID, cantiere.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Cantiere in Elasticsearch
        verify(mockCantiereSearchRepository, times(1)).deleteById(cantiere.getId());
    }

    @Test
    @Transactional
    void searchCantiere() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        cantiereRepository.saveAndFlush(cantiere);
        when(mockCantiereSearchRepository.search("id:" + cantiere.getId())).thenReturn(Stream.of(cantiere));

        // Search the cantiere
        restCantiereMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + cantiere.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cantiere.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomeCantiere").value(hasItem(DEFAULT_NOME_CANTIERE)))
            .andExpect(jsonPath("$.[*].indirizzo").value(hasItem(DEFAULT_INDIRIZZO)));
    }
}
