package org.econ.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.econ.IntegrationTest;
import org.econ.domain.FatturePassivo;
import org.econ.domain.enumeration.Stato;
import org.econ.repository.FatturePassivoRepository;
import org.econ.repository.search.FatturePassivoSearchRepository;
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
 * Integration tests for the {@link FatturePassivoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FatturePassivoResourceIT {

    private static final Long DEFAULT_NUMERO_FATTURA = 1L;
    private static final Long UPDATED_NUMERO_FATTURA = 2L;

    private static final String DEFAULT_RAG_SOCIALE = "AAAAAAAAAA";
    private static final String UPDATED_RAG_SOCIALE = "BBBBBBBBBB";

    private static final String DEFAULT_NOME_FORNITORE = "AAAAAAAAAA";
    private static final String UPDATED_NOME_FORNITORE = "BBBBBBBBBB";

    private static final Long DEFAULT_IMPONIBILE = 1L;
    private static final Long UPDATED_IMPONIBILE = 2L;

    private static final Long DEFAULT_IVA = 1L;
    private static final Long UPDATED_IVA = 2L;

    private static final Stato DEFAULT_STATO = Stato.NON_PAGATA;
    private static final Stato UPDATED_STATO = Stato.PAGATA;

    private static final Instant DEFAULT_DATA_EMISSIONE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATA_EMISSIONE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DATA_PAGAMENTO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATA_PAGAMENTO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/fatture-passivos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/fatture-passivos";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FatturePassivoRepository fatturePassivoRepository;

    /**
     * This repository is mocked in the org.econ.repository.search test package.
     *
     * @see org.econ.repository.search.FatturePassivoSearchRepositoryMockConfiguration
     */
    @Autowired
    private FatturePassivoSearchRepository mockFatturePassivoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFatturePassivoMockMvc;

    private FatturePassivo fatturePassivo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FatturePassivo createEntity(EntityManager em) {
        FatturePassivo fatturePassivo = new FatturePassivo()
            .numeroFattura(DEFAULT_NUMERO_FATTURA)
            .ragSociale(DEFAULT_RAG_SOCIALE)
            .nomeFornitore(DEFAULT_NOME_FORNITORE)
            .imponibile(DEFAULT_IMPONIBILE)
            .iva(DEFAULT_IVA)
            .stato(DEFAULT_STATO)
            .dataEmissione(DEFAULT_DATA_EMISSIONE)
            .dataPagamento(DEFAULT_DATA_PAGAMENTO);
        return fatturePassivo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FatturePassivo createUpdatedEntity(EntityManager em) {
        FatturePassivo fatturePassivo = new FatturePassivo()
            .numeroFattura(UPDATED_NUMERO_FATTURA)
            .ragSociale(UPDATED_RAG_SOCIALE)
            .nomeFornitore(UPDATED_NOME_FORNITORE)
            .imponibile(UPDATED_IMPONIBILE)
            .iva(UPDATED_IVA)
            .stato(UPDATED_STATO)
            .dataEmissione(UPDATED_DATA_EMISSIONE)
            .dataPagamento(UPDATED_DATA_PAGAMENTO);
        return fatturePassivo;
    }

    @BeforeEach
    public void initTest() {
        fatturePassivo = createEntity(em);
    }

    @Test
    @Transactional
    void createFatturePassivo() throws Exception {
        int databaseSizeBeforeCreate = fatturePassivoRepository.findAll().size();
        // Create the FatturePassivo
        restFatturePassivoMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fatturePassivo))
            )
            .andExpect(status().isCreated());

        // Validate the FatturePassivo in the database
        List<FatturePassivo> fatturePassivoList = fatturePassivoRepository.findAll();
        assertThat(fatturePassivoList).hasSize(databaseSizeBeforeCreate + 1);
        FatturePassivo testFatturePassivo = fatturePassivoList.get(fatturePassivoList.size() - 1);
        assertThat(testFatturePassivo.getNumeroFattura()).isEqualTo(DEFAULT_NUMERO_FATTURA);
        assertThat(testFatturePassivo.getRagSociale()).isEqualTo(DEFAULT_RAG_SOCIALE);
        assertThat(testFatturePassivo.getNomeFornitore()).isEqualTo(DEFAULT_NOME_FORNITORE);
        assertThat(testFatturePassivo.getImponibile()).isEqualTo(DEFAULT_IMPONIBILE);
        assertThat(testFatturePassivo.getIva()).isEqualTo(DEFAULT_IVA);
        assertThat(testFatturePassivo.getStato()).isEqualTo(DEFAULT_STATO);
        assertThat(testFatturePassivo.getDataEmissione()).isEqualTo(DEFAULT_DATA_EMISSIONE);
        assertThat(testFatturePassivo.getDataPagamento()).isEqualTo(DEFAULT_DATA_PAGAMENTO);

        // Validate the FatturePassivo in Elasticsearch
        verify(mockFatturePassivoSearchRepository, times(1)).save(testFatturePassivo);
    }

    @Test
    @Transactional
    void createFatturePassivoWithExistingId() throws Exception {
        // Create the FatturePassivo with an existing ID
        fatturePassivo.setId(1L);

        int databaseSizeBeforeCreate = fatturePassivoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFatturePassivoMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fatturePassivo))
            )
            .andExpect(status().isBadRequest());

        // Validate the FatturePassivo in the database
        List<FatturePassivo> fatturePassivoList = fatturePassivoRepository.findAll();
        assertThat(fatturePassivoList).hasSize(databaseSizeBeforeCreate);

        // Validate the FatturePassivo in Elasticsearch
        verify(mockFatturePassivoSearchRepository, times(0)).save(fatturePassivo);
    }

    @Test
    @Transactional
    void checkNumeroFatturaIsRequired() throws Exception {
        int databaseSizeBeforeTest = fatturePassivoRepository.findAll().size();
        // set the field null
        fatturePassivo.setNumeroFattura(null);

        // Create the FatturePassivo, which fails.

        restFatturePassivoMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fatturePassivo))
            )
            .andExpect(status().isBadRequest());

        List<FatturePassivo> fatturePassivoList = fatturePassivoRepository.findAll();
        assertThat(fatturePassivoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFatturePassivos() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList
        restFatturePassivoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fatturePassivo.getId().intValue())))
            .andExpect(jsonPath("$.[*].numeroFattura").value(hasItem(DEFAULT_NUMERO_FATTURA.intValue())))
            .andExpect(jsonPath("$.[*].ragSociale").value(hasItem(DEFAULT_RAG_SOCIALE)))
            .andExpect(jsonPath("$.[*].nomeFornitore").value(hasItem(DEFAULT_NOME_FORNITORE)))
            .andExpect(jsonPath("$.[*].imponibile").value(hasItem(DEFAULT_IMPONIBILE.intValue())))
            .andExpect(jsonPath("$.[*].iva").value(hasItem(DEFAULT_IVA.intValue())))
            .andExpect(jsonPath("$.[*].stato").value(hasItem(DEFAULT_STATO.toString())))
            .andExpect(jsonPath("$.[*].dataEmissione").value(hasItem(DEFAULT_DATA_EMISSIONE.toString())))
            .andExpect(jsonPath("$.[*].dataPagamento").value(hasItem(DEFAULT_DATA_PAGAMENTO.toString())));
    }

    @Test
    @Transactional
    void getFatturePassivo() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get the fatturePassivo
        restFatturePassivoMockMvc
            .perform(get(ENTITY_API_URL_ID, fatturePassivo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(fatturePassivo.getId().intValue()))
            .andExpect(jsonPath("$.numeroFattura").value(DEFAULT_NUMERO_FATTURA.intValue()))
            .andExpect(jsonPath("$.ragSociale").value(DEFAULT_RAG_SOCIALE))
            .andExpect(jsonPath("$.nomeFornitore").value(DEFAULT_NOME_FORNITORE))
            .andExpect(jsonPath("$.imponibile").value(DEFAULT_IMPONIBILE.intValue()))
            .andExpect(jsonPath("$.iva").value(DEFAULT_IVA.intValue()))
            .andExpect(jsonPath("$.stato").value(DEFAULT_STATO.toString()))
            .andExpect(jsonPath("$.dataEmissione").value(DEFAULT_DATA_EMISSIONE.toString()))
            .andExpect(jsonPath("$.dataPagamento").value(DEFAULT_DATA_PAGAMENTO.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFatturePassivo() throws Exception {
        // Get the fatturePassivo
        restFatturePassivoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFatturePassivo() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        int databaseSizeBeforeUpdate = fatturePassivoRepository.findAll().size();

        // Update the fatturePassivo
        FatturePassivo updatedFatturePassivo = fatturePassivoRepository.findById(fatturePassivo.getId()).get();
        // Disconnect from session so that the updates on updatedFatturePassivo are not directly saved in db
        em.detach(updatedFatturePassivo);
        updatedFatturePassivo
            .numeroFattura(UPDATED_NUMERO_FATTURA)
            .ragSociale(UPDATED_RAG_SOCIALE)
            .nomeFornitore(UPDATED_NOME_FORNITORE)
            .imponibile(UPDATED_IMPONIBILE)
            .iva(UPDATED_IVA)
            .stato(UPDATED_STATO)
            .dataEmissione(UPDATED_DATA_EMISSIONE)
            .dataPagamento(UPDATED_DATA_PAGAMENTO);

        restFatturePassivoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFatturePassivo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFatturePassivo))
            )
            .andExpect(status().isOk());

        // Validate the FatturePassivo in the database
        List<FatturePassivo> fatturePassivoList = fatturePassivoRepository.findAll();
        assertThat(fatturePassivoList).hasSize(databaseSizeBeforeUpdate);
        FatturePassivo testFatturePassivo = fatturePassivoList.get(fatturePassivoList.size() - 1);
        assertThat(testFatturePassivo.getNumeroFattura()).isEqualTo(UPDATED_NUMERO_FATTURA);
        assertThat(testFatturePassivo.getRagSociale()).isEqualTo(UPDATED_RAG_SOCIALE);
        assertThat(testFatturePassivo.getNomeFornitore()).isEqualTo(UPDATED_NOME_FORNITORE);
        assertThat(testFatturePassivo.getImponibile()).isEqualTo(UPDATED_IMPONIBILE);
        assertThat(testFatturePassivo.getIva()).isEqualTo(UPDATED_IVA);
        assertThat(testFatturePassivo.getStato()).isEqualTo(UPDATED_STATO);
        assertThat(testFatturePassivo.getDataEmissione()).isEqualTo(UPDATED_DATA_EMISSIONE);
        assertThat(testFatturePassivo.getDataPagamento()).isEqualTo(UPDATED_DATA_PAGAMENTO);

        // Validate the FatturePassivo in Elasticsearch
        verify(mockFatturePassivoSearchRepository).save(testFatturePassivo);
    }

    @Test
    @Transactional
    void putNonExistingFatturePassivo() throws Exception {
        int databaseSizeBeforeUpdate = fatturePassivoRepository.findAll().size();
        fatturePassivo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFatturePassivoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fatturePassivo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fatturePassivo))
            )
            .andExpect(status().isBadRequest());

        // Validate the FatturePassivo in the database
        List<FatturePassivo> fatturePassivoList = fatturePassivoRepository.findAll();
        assertThat(fatturePassivoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FatturePassivo in Elasticsearch
        verify(mockFatturePassivoSearchRepository, times(0)).save(fatturePassivo);
    }

    @Test
    @Transactional
    void putWithIdMismatchFatturePassivo() throws Exception {
        int databaseSizeBeforeUpdate = fatturePassivoRepository.findAll().size();
        fatturePassivo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFatturePassivoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fatturePassivo))
            )
            .andExpect(status().isBadRequest());

        // Validate the FatturePassivo in the database
        List<FatturePassivo> fatturePassivoList = fatturePassivoRepository.findAll();
        assertThat(fatturePassivoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FatturePassivo in Elasticsearch
        verify(mockFatturePassivoSearchRepository, times(0)).save(fatturePassivo);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFatturePassivo() throws Exception {
        int databaseSizeBeforeUpdate = fatturePassivoRepository.findAll().size();
        fatturePassivo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFatturePassivoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fatturePassivo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FatturePassivo in the database
        List<FatturePassivo> fatturePassivoList = fatturePassivoRepository.findAll();
        assertThat(fatturePassivoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FatturePassivo in Elasticsearch
        verify(mockFatturePassivoSearchRepository, times(0)).save(fatturePassivo);
    }

    @Test
    @Transactional
    void partialUpdateFatturePassivoWithPatch() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        int databaseSizeBeforeUpdate = fatturePassivoRepository.findAll().size();

        // Update the fatturePassivo using partial update
        FatturePassivo partialUpdatedFatturePassivo = new FatturePassivo();
        partialUpdatedFatturePassivo.setId(fatturePassivo.getId());

        partialUpdatedFatturePassivo
            .ragSociale(UPDATED_RAG_SOCIALE)
            .imponibile(UPDATED_IMPONIBILE)
            .iva(UPDATED_IVA)
            .dataPagamento(UPDATED_DATA_PAGAMENTO);

        restFatturePassivoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFatturePassivo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFatturePassivo))
            )
            .andExpect(status().isOk());

        // Validate the FatturePassivo in the database
        List<FatturePassivo> fatturePassivoList = fatturePassivoRepository.findAll();
        assertThat(fatturePassivoList).hasSize(databaseSizeBeforeUpdate);
        FatturePassivo testFatturePassivo = fatturePassivoList.get(fatturePassivoList.size() - 1);
        assertThat(testFatturePassivo.getNumeroFattura()).isEqualTo(DEFAULT_NUMERO_FATTURA);
        assertThat(testFatturePassivo.getRagSociale()).isEqualTo(UPDATED_RAG_SOCIALE);
        assertThat(testFatturePassivo.getNomeFornitore()).isEqualTo(DEFAULT_NOME_FORNITORE);
        assertThat(testFatturePassivo.getImponibile()).isEqualTo(UPDATED_IMPONIBILE);
        assertThat(testFatturePassivo.getIva()).isEqualTo(UPDATED_IVA);
        assertThat(testFatturePassivo.getStato()).isEqualTo(DEFAULT_STATO);
        assertThat(testFatturePassivo.getDataEmissione()).isEqualTo(DEFAULT_DATA_EMISSIONE);
        assertThat(testFatturePassivo.getDataPagamento()).isEqualTo(UPDATED_DATA_PAGAMENTO);
    }

    @Test
    @Transactional
    void fullUpdateFatturePassivoWithPatch() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        int databaseSizeBeforeUpdate = fatturePassivoRepository.findAll().size();

        // Update the fatturePassivo using partial update
        FatturePassivo partialUpdatedFatturePassivo = new FatturePassivo();
        partialUpdatedFatturePassivo.setId(fatturePassivo.getId());

        partialUpdatedFatturePassivo
            .numeroFattura(UPDATED_NUMERO_FATTURA)
            .ragSociale(UPDATED_RAG_SOCIALE)
            .nomeFornitore(UPDATED_NOME_FORNITORE)
            .imponibile(UPDATED_IMPONIBILE)
            .iva(UPDATED_IVA)
            .stato(UPDATED_STATO)
            .dataEmissione(UPDATED_DATA_EMISSIONE)
            .dataPagamento(UPDATED_DATA_PAGAMENTO);

        restFatturePassivoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFatturePassivo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFatturePassivo))
            )
            .andExpect(status().isOk());

        // Validate the FatturePassivo in the database
        List<FatturePassivo> fatturePassivoList = fatturePassivoRepository.findAll();
        assertThat(fatturePassivoList).hasSize(databaseSizeBeforeUpdate);
        FatturePassivo testFatturePassivo = fatturePassivoList.get(fatturePassivoList.size() - 1);
        assertThat(testFatturePassivo.getNumeroFattura()).isEqualTo(UPDATED_NUMERO_FATTURA);
        assertThat(testFatturePassivo.getRagSociale()).isEqualTo(UPDATED_RAG_SOCIALE);
        assertThat(testFatturePassivo.getNomeFornitore()).isEqualTo(UPDATED_NOME_FORNITORE);
        assertThat(testFatturePassivo.getImponibile()).isEqualTo(UPDATED_IMPONIBILE);
        assertThat(testFatturePassivo.getIva()).isEqualTo(UPDATED_IVA);
        assertThat(testFatturePassivo.getStato()).isEqualTo(UPDATED_STATO);
        assertThat(testFatturePassivo.getDataEmissione()).isEqualTo(UPDATED_DATA_EMISSIONE);
        assertThat(testFatturePassivo.getDataPagamento()).isEqualTo(UPDATED_DATA_PAGAMENTO);
    }

    @Test
    @Transactional
    void patchNonExistingFatturePassivo() throws Exception {
        int databaseSizeBeforeUpdate = fatturePassivoRepository.findAll().size();
        fatturePassivo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFatturePassivoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, fatturePassivo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(fatturePassivo))
            )
            .andExpect(status().isBadRequest());

        // Validate the FatturePassivo in the database
        List<FatturePassivo> fatturePassivoList = fatturePassivoRepository.findAll();
        assertThat(fatturePassivoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FatturePassivo in Elasticsearch
        verify(mockFatturePassivoSearchRepository, times(0)).save(fatturePassivo);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFatturePassivo() throws Exception {
        int databaseSizeBeforeUpdate = fatturePassivoRepository.findAll().size();
        fatturePassivo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFatturePassivoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(fatturePassivo))
            )
            .andExpect(status().isBadRequest());

        // Validate the FatturePassivo in the database
        List<FatturePassivo> fatturePassivoList = fatturePassivoRepository.findAll();
        assertThat(fatturePassivoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FatturePassivo in Elasticsearch
        verify(mockFatturePassivoSearchRepository, times(0)).save(fatturePassivo);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFatturePassivo() throws Exception {
        int databaseSizeBeforeUpdate = fatturePassivoRepository.findAll().size();
        fatturePassivo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFatturePassivoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(fatturePassivo))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FatturePassivo in the database
        List<FatturePassivo> fatturePassivoList = fatturePassivoRepository.findAll();
        assertThat(fatturePassivoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FatturePassivo in Elasticsearch
        verify(mockFatturePassivoSearchRepository, times(0)).save(fatturePassivo);
    }

    @Test
    @Transactional
    void deleteFatturePassivo() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        int databaseSizeBeforeDelete = fatturePassivoRepository.findAll().size();

        // Delete the fatturePassivo
        restFatturePassivoMockMvc
            .perform(delete(ENTITY_API_URL_ID, fatturePassivo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FatturePassivo> fatturePassivoList = fatturePassivoRepository.findAll();
        assertThat(fatturePassivoList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the FatturePassivo in Elasticsearch
        verify(mockFatturePassivoSearchRepository, times(1)).deleteById(fatturePassivo.getId());
    }

    @Test
    @Transactional
    void searchFatturePassivo() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);
        when(mockFatturePassivoSearchRepository.search("id:" + fatturePassivo.getId())).thenReturn(Stream.of(fatturePassivo));

        // Search the fatturePassivo
        restFatturePassivoMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + fatturePassivo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fatturePassivo.getId().intValue())))
            .andExpect(jsonPath("$.[*].numeroFattura").value(hasItem(DEFAULT_NUMERO_FATTURA.intValue())))
            .andExpect(jsonPath("$.[*].ragSociale").value(hasItem(DEFAULT_RAG_SOCIALE)))
            .andExpect(jsonPath("$.[*].nomeFornitore").value(hasItem(DEFAULT_NOME_FORNITORE)))
            .andExpect(jsonPath("$.[*].imponibile").value(hasItem(DEFAULT_IMPONIBILE.intValue())))
            .andExpect(jsonPath("$.[*].iva").value(hasItem(DEFAULT_IVA.intValue())))
            .andExpect(jsonPath("$.[*].stato").value(hasItem(DEFAULT_STATO.toString())))
            .andExpect(jsonPath("$.[*].dataEmissione").value(hasItem(DEFAULT_DATA_EMISSIONE.toString())))
            .andExpect(jsonPath("$.[*].dataPagamento").value(hasItem(DEFAULT_DATA_PAGAMENTO.toString())));
    }
}
