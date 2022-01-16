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
import org.econ.domain.Fornitore;
import org.econ.repository.FornitoreRepository;
import org.econ.repository.search.FornitoreSearchRepository;
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
 * Integration tests for the {@link FornitoreResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FornitoreResourceIT {

    private static final String DEFAULT_NOME_FORNITORE = "AAAAAAAAAA";
    private static final String UPDATED_NOME_FORNITORE = "BBBBBBBBBB";

    private static final String DEFAULT_INDIRIZZO = "AAAAAAAAAA";
    private static final String UPDATED_INDIRIZZO = "BBBBBBBBBB";

    private static final String DEFAULT_TIPO = "AAAAAAAAAA";
    private static final String UPDATED_TIPO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/fornitores";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/fornitores";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FornitoreRepository fornitoreRepository;

    /**
     * This repository is mocked in the org.econ.repository.search test package.
     *
     * @see org.econ.repository.search.FornitoreSearchRepositoryMockConfiguration
     */
    @Autowired
    private FornitoreSearchRepository mockFornitoreSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFornitoreMockMvc;

    private Fornitore fornitore;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Fornitore createEntity(EntityManager em) {
        Fornitore fornitore = new Fornitore().nomeFornitore(DEFAULT_NOME_FORNITORE).indirizzo(DEFAULT_INDIRIZZO).tipo(DEFAULT_TIPO);
        return fornitore;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Fornitore createUpdatedEntity(EntityManager em) {
        Fornitore fornitore = new Fornitore().nomeFornitore(UPDATED_NOME_FORNITORE).indirizzo(UPDATED_INDIRIZZO).tipo(UPDATED_TIPO);
        return fornitore;
    }

    @BeforeEach
    public void initTest() {
        fornitore = createEntity(em);
    }

    @Test
    @Transactional
    void createFornitore() throws Exception {
        int databaseSizeBeforeCreate = fornitoreRepository.findAll().size();
        // Create the Fornitore
        restFornitoreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fornitore)))
            .andExpect(status().isCreated());

        // Validate the Fornitore in the database
        List<Fornitore> fornitoreList = fornitoreRepository.findAll();
        assertThat(fornitoreList).hasSize(databaseSizeBeforeCreate + 1);
        Fornitore testFornitore = fornitoreList.get(fornitoreList.size() - 1);
        assertThat(testFornitore.getNomeFornitore()).isEqualTo(DEFAULT_NOME_FORNITORE);
        assertThat(testFornitore.getIndirizzo()).isEqualTo(DEFAULT_INDIRIZZO);
        assertThat(testFornitore.getTipo()).isEqualTo(DEFAULT_TIPO);

        // Validate the Fornitore in Elasticsearch
        verify(mockFornitoreSearchRepository, times(1)).save(testFornitore);
    }

    @Test
    @Transactional
    void createFornitoreWithExistingId() throws Exception {
        // Create the Fornitore with an existing ID
        fornitore.setId(1L);

        int databaseSizeBeforeCreate = fornitoreRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFornitoreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fornitore)))
            .andExpect(status().isBadRequest());

        // Validate the Fornitore in the database
        List<Fornitore> fornitoreList = fornitoreRepository.findAll();
        assertThat(fornitoreList).hasSize(databaseSizeBeforeCreate);

        // Validate the Fornitore in Elasticsearch
        verify(mockFornitoreSearchRepository, times(0)).save(fornitore);
    }

    @Test
    @Transactional
    void checkNomeFornitoreIsRequired() throws Exception {
        int databaseSizeBeforeTest = fornitoreRepository.findAll().size();
        // set the field null
        fornitore.setNomeFornitore(null);

        // Create the Fornitore, which fails.

        restFornitoreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fornitore)))
            .andExpect(status().isBadRequest());

        List<Fornitore> fornitoreList = fornitoreRepository.findAll();
        assertThat(fornitoreList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFornitores() throws Exception {
        // Initialize the database
        fornitoreRepository.saveAndFlush(fornitore);

        // Get all the fornitoreList
        restFornitoreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fornitore.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomeFornitore").value(hasItem(DEFAULT_NOME_FORNITORE)))
            .andExpect(jsonPath("$.[*].indirizzo").value(hasItem(DEFAULT_INDIRIZZO)))
            .andExpect(jsonPath("$.[*].tipo").value(hasItem(DEFAULT_TIPO)));
    }

    @Test
    @Transactional
    void getFornitore() throws Exception {
        // Initialize the database
        fornitoreRepository.saveAndFlush(fornitore);

        // Get the fornitore
        restFornitoreMockMvc
            .perform(get(ENTITY_API_URL_ID, fornitore.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(fornitore.getId().intValue()))
            .andExpect(jsonPath("$.nomeFornitore").value(DEFAULT_NOME_FORNITORE))
            .andExpect(jsonPath("$.indirizzo").value(DEFAULT_INDIRIZZO))
            .andExpect(jsonPath("$.tipo").value(DEFAULT_TIPO));
    }

    @Test
    @Transactional
    void getNonExistingFornitore() throws Exception {
        // Get the fornitore
        restFornitoreMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFornitore() throws Exception {
        // Initialize the database
        fornitoreRepository.saveAndFlush(fornitore);

        int databaseSizeBeforeUpdate = fornitoreRepository.findAll().size();

        // Update the fornitore
        Fornitore updatedFornitore = fornitoreRepository.findById(fornitore.getId()).get();
        // Disconnect from session so that the updates on updatedFornitore are not directly saved in db
        em.detach(updatedFornitore);
        updatedFornitore.nomeFornitore(UPDATED_NOME_FORNITORE).indirizzo(UPDATED_INDIRIZZO).tipo(UPDATED_TIPO);

        restFornitoreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFornitore.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFornitore))
            )
            .andExpect(status().isOk());

        // Validate the Fornitore in the database
        List<Fornitore> fornitoreList = fornitoreRepository.findAll();
        assertThat(fornitoreList).hasSize(databaseSizeBeforeUpdate);
        Fornitore testFornitore = fornitoreList.get(fornitoreList.size() - 1);
        assertThat(testFornitore.getNomeFornitore()).isEqualTo(UPDATED_NOME_FORNITORE);
        assertThat(testFornitore.getIndirizzo()).isEqualTo(UPDATED_INDIRIZZO);
        assertThat(testFornitore.getTipo()).isEqualTo(UPDATED_TIPO);

        // Validate the Fornitore in Elasticsearch
        verify(mockFornitoreSearchRepository).save(testFornitore);
    }

    @Test
    @Transactional
    void putNonExistingFornitore() throws Exception {
        int databaseSizeBeforeUpdate = fornitoreRepository.findAll().size();
        fornitore.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFornitoreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fornitore.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fornitore))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fornitore in the database
        List<Fornitore> fornitoreList = fornitoreRepository.findAll();
        assertThat(fornitoreList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Fornitore in Elasticsearch
        verify(mockFornitoreSearchRepository, times(0)).save(fornitore);
    }

    @Test
    @Transactional
    void putWithIdMismatchFornitore() throws Exception {
        int databaseSizeBeforeUpdate = fornitoreRepository.findAll().size();
        fornitore.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFornitoreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fornitore))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fornitore in the database
        List<Fornitore> fornitoreList = fornitoreRepository.findAll();
        assertThat(fornitoreList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Fornitore in Elasticsearch
        verify(mockFornitoreSearchRepository, times(0)).save(fornitore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFornitore() throws Exception {
        int databaseSizeBeforeUpdate = fornitoreRepository.findAll().size();
        fornitore.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFornitoreMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fornitore)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Fornitore in the database
        List<Fornitore> fornitoreList = fornitoreRepository.findAll();
        assertThat(fornitoreList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Fornitore in Elasticsearch
        verify(mockFornitoreSearchRepository, times(0)).save(fornitore);
    }

    @Test
    @Transactional
    void partialUpdateFornitoreWithPatch() throws Exception {
        // Initialize the database
        fornitoreRepository.saveAndFlush(fornitore);

        int databaseSizeBeforeUpdate = fornitoreRepository.findAll().size();

        // Update the fornitore using partial update
        Fornitore partialUpdatedFornitore = new Fornitore();
        partialUpdatedFornitore.setId(fornitore.getId());

        partialUpdatedFornitore.indirizzo(UPDATED_INDIRIZZO).tipo(UPDATED_TIPO);

        restFornitoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFornitore.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFornitore))
            )
            .andExpect(status().isOk());

        // Validate the Fornitore in the database
        List<Fornitore> fornitoreList = fornitoreRepository.findAll();
        assertThat(fornitoreList).hasSize(databaseSizeBeforeUpdate);
        Fornitore testFornitore = fornitoreList.get(fornitoreList.size() - 1);
        assertThat(testFornitore.getNomeFornitore()).isEqualTo(DEFAULT_NOME_FORNITORE);
        assertThat(testFornitore.getIndirizzo()).isEqualTo(UPDATED_INDIRIZZO);
        assertThat(testFornitore.getTipo()).isEqualTo(UPDATED_TIPO);
    }

    @Test
    @Transactional
    void fullUpdateFornitoreWithPatch() throws Exception {
        // Initialize the database
        fornitoreRepository.saveAndFlush(fornitore);

        int databaseSizeBeforeUpdate = fornitoreRepository.findAll().size();

        // Update the fornitore using partial update
        Fornitore partialUpdatedFornitore = new Fornitore();
        partialUpdatedFornitore.setId(fornitore.getId());

        partialUpdatedFornitore.nomeFornitore(UPDATED_NOME_FORNITORE).indirizzo(UPDATED_INDIRIZZO).tipo(UPDATED_TIPO);

        restFornitoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFornitore.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFornitore))
            )
            .andExpect(status().isOk());

        // Validate the Fornitore in the database
        List<Fornitore> fornitoreList = fornitoreRepository.findAll();
        assertThat(fornitoreList).hasSize(databaseSizeBeforeUpdate);
        Fornitore testFornitore = fornitoreList.get(fornitoreList.size() - 1);
        assertThat(testFornitore.getNomeFornitore()).isEqualTo(UPDATED_NOME_FORNITORE);
        assertThat(testFornitore.getIndirizzo()).isEqualTo(UPDATED_INDIRIZZO);
        assertThat(testFornitore.getTipo()).isEqualTo(UPDATED_TIPO);
    }

    @Test
    @Transactional
    void patchNonExistingFornitore() throws Exception {
        int databaseSizeBeforeUpdate = fornitoreRepository.findAll().size();
        fornitore.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFornitoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, fornitore.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(fornitore))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fornitore in the database
        List<Fornitore> fornitoreList = fornitoreRepository.findAll();
        assertThat(fornitoreList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Fornitore in Elasticsearch
        verify(mockFornitoreSearchRepository, times(0)).save(fornitore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFornitore() throws Exception {
        int databaseSizeBeforeUpdate = fornitoreRepository.findAll().size();
        fornitore.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFornitoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(fornitore))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fornitore in the database
        List<Fornitore> fornitoreList = fornitoreRepository.findAll();
        assertThat(fornitoreList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Fornitore in Elasticsearch
        verify(mockFornitoreSearchRepository, times(0)).save(fornitore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFornitore() throws Exception {
        int databaseSizeBeforeUpdate = fornitoreRepository.findAll().size();
        fornitore.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFornitoreMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(fornitore))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Fornitore in the database
        List<Fornitore> fornitoreList = fornitoreRepository.findAll();
        assertThat(fornitoreList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Fornitore in Elasticsearch
        verify(mockFornitoreSearchRepository, times(0)).save(fornitore);
    }

    @Test
    @Transactional
    void deleteFornitore() throws Exception {
        // Initialize the database
        fornitoreRepository.saveAndFlush(fornitore);

        int databaseSizeBeforeDelete = fornitoreRepository.findAll().size();

        // Delete the fornitore
        restFornitoreMockMvc
            .perform(delete(ENTITY_API_URL_ID, fornitore.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Fornitore> fornitoreList = fornitoreRepository.findAll();
        assertThat(fornitoreList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Fornitore in Elasticsearch
        verify(mockFornitoreSearchRepository, times(1)).deleteById(fornitore.getId());
    }

    @Test
    @Transactional
    void searchFornitore() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        fornitoreRepository.saveAndFlush(fornitore);
        when(mockFornitoreSearchRepository.search("id:" + fornitore.getId())).thenReturn(Stream.of(fornitore));

        // Search the fornitore
        restFornitoreMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + fornitore.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fornitore.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomeFornitore").value(hasItem(DEFAULT_NOME_FORNITORE)))
            .andExpect(jsonPath("$.[*].indirizzo").value(hasItem(DEFAULT_INDIRIZZO)))
            .andExpect(jsonPath("$.[*].tipo").value(hasItem(DEFAULT_TIPO)));
    }
}
