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
import org.econ.domain.FattureAttivo;
import org.econ.domain.enumeration.Stato;
import org.econ.repository.FattureAttivoRepository;
import org.econ.repository.search.FattureAttivoSearchRepository;
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
 * Integration tests for the {@link FattureAttivoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FattureAttivoResourceIT {

    private static final Long DEFAULT_NUMERO_FATTURA = 1L;
    private static final Long UPDATED_NUMERO_FATTURA = 2L;

    private static final String DEFAULT_RAG_SOCIALE = "AAAAAAAAAA";
    private static final String UPDATED_RAG_SOCIALE = "BBBBBBBBBB";

    private static final String DEFAULT_NOME_CLIENTE = "AAAAAAAAAA";
    private static final String UPDATED_NOME_CLIENTE = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/fatture-attivos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/fatture-attivos";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FattureAttivoRepository fattureAttivoRepository;

    /**
     * This repository is mocked in the org.econ.repository.search test package.
     *
     * @see org.econ.repository.search.FattureAttivoSearchRepositoryMockConfiguration
     */
    @Autowired
    private FattureAttivoSearchRepository mockFattureAttivoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFattureAttivoMockMvc;

    private FattureAttivo fattureAttivo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FattureAttivo createEntity(EntityManager em) {
        FattureAttivo fattureAttivo = new FattureAttivo()
            .numeroFattura(DEFAULT_NUMERO_FATTURA)
            .ragSociale(DEFAULT_RAG_SOCIALE)
            .nomeCliente(DEFAULT_NOME_CLIENTE)
            .imponibile(DEFAULT_IMPONIBILE)
            .iva(DEFAULT_IVA)
            .stato(DEFAULT_STATO)
            .dataEmissione(DEFAULT_DATA_EMISSIONE)
            .dataPagamento(DEFAULT_DATA_PAGAMENTO);
        return fattureAttivo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FattureAttivo createUpdatedEntity(EntityManager em) {
        FattureAttivo fattureAttivo = new FattureAttivo()
            .numeroFattura(UPDATED_NUMERO_FATTURA)
            .ragSociale(UPDATED_RAG_SOCIALE)
            .nomeCliente(UPDATED_NOME_CLIENTE)
            .imponibile(UPDATED_IMPONIBILE)
            .iva(UPDATED_IVA)
            .stato(UPDATED_STATO)
            .dataEmissione(UPDATED_DATA_EMISSIONE)
            .dataPagamento(UPDATED_DATA_PAGAMENTO);
        return fattureAttivo;
    }

    @BeforeEach
    public void initTest() {
        fattureAttivo = createEntity(em);
    }

    @Test
    @Transactional
    void createFattureAttivo() throws Exception {
        int databaseSizeBeforeCreate = fattureAttivoRepository.findAll().size();
        // Create the FattureAttivo
        restFattureAttivoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fattureAttivo)))
            .andExpect(status().isCreated());

        // Validate the FattureAttivo in the database
        List<FattureAttivo> fattureAttivoList = fattureAttivoRepository.findAll();
        assertThat(fattureAttivoList).hasSize(databaseSizeBeforeCreate + 1);
        FattureAttivo testFattureAttivo = fattureAttivoList.get(fattureAttivoList.size() - 1);
        assertThat(testFattureAttivo.getNumeroFattura()).isEqualTo(DEFAULT_NUMERO_FATTURA);
        assertThat(testFattureAttivo.getRagSociale()).isEqualTo(DEFAULT_RAG_SOCIALE);
        assertThat(testFattureAttivo.getNomeCliente()).isEqualTo(DEFAULT_NOME_CLIENTE);
        assertThat(testFattureAttivo.getImponibile()).isEqualTo(DEFAULT_IMPONIBILE);
        assertThat(testFattureAttivo.getIva()).isEqualTo(DEFAULT_IVA);
        assertThat(testFattureAttivo.getStato()).isEqualTo(DEFAULT_STATO);
        assertThat(testFattureAttivo.getDataEmissione()).isEqualTo(DEFAULT_DATA_EMISSIONE);
        assertThat(testFattureAttivo.getDataPagamento()).isEqualTo(DEFAULT_DATA_PAGAMENTO);

        // Validate the FattureAttivo in Elasticsearch
        verify(mockFattureAttivoSearchRepository, times(1)).save(testFattureAttivo);
    }

    @Test
    @Transactional
    void createFattureAttivoWithExistingId() throws Exception {
        // Create the FattureAttivo with an existing ID
        fattureAttivo.setId(1L);

        int databaseSizeBeforeCreate = fattureAttivoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFattureAttivoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fattureAttivo)))
            .andExpect(status().isBadRequest());

        // Validate the FattureAttivo in the database
        List<FattureAttivo> fattureAttivoList = fattureAttivoRepository.findAll();
        assertThat(fattureAttivoList).hasSize(databaseSizeBeforeCreate);

        // Validate the FattureAttivo in Elasticsearch
        verify(mockFattureAttivoSearchRepository, times(0)).save(fattureAttivo);
    }

    @Test
    @Transactional
    void checkNumeroFatturaIsRequired() throws Exception {
        int databaseSizeBeforeTest = fattureAttivoRepository.findAll().size();
        // set the field null
        fattureAttivo.setNumeroFattura(null);

        // Create the FattureAttivo, which fails.

        restFattureAttivoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fattureAttivo)))
            .andExpect(status().isBadRequest());

        List<FattureAttivo> fattureAttivoList = fattureAttivoRepository.findAll();
        assertThat(fattureAttivoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFattureAttivos() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList
        restFattureAttivoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fattureAttivo.getId().intValue())))
            .andExpect(jsonPath("$.[*].numeroFattura").value(hasItem(DEFAULT_NUMERO_FATTURA.intValue())))
            .andExpect(jsonPath("$.[*].ragSociale").value(hasItem(DEFAULT_RAG_SOCIALE)))
            .andExpect(jsonPath("$.[*].nomeCliente").value(hasItem(DEFAULT_NOME_CLIENTE)))
            .andExpect(jsonPath("$.[*].imponibile").value(hasItem(DEFAULT_IMPONIBILE.intValue())))
            .andExpect(jsonPath("$.[*].iva").value(hasItem(DEFAULT_IVA.intValue())))
            .andExpect(jsonPath("$.[*].stato").value(hasItem(DEFAULT_STATO.toString())))
            .andExpect(jsonPath("$.[*].dataEmissione").value(hasItem(DEFAULT_DATA_EMISSIONE.toString())))
            .andExpect(jsonPath("$.[*].dataPagamento").value(hasItem(DEFAULT_DATA_PAGAMENTO.toString())));
    }

    @Test
    @Transactional
    void getFattureAttivo() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get the fattureAttivo
        restFattureAttivoMockMvc
            .perform(get(ENTITY_API_URL_ID, fattureAttivo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(fattureAttivo.getId().intValue()))
            .andExpect(jsonPath("$.numeroFattura").value(DEFAULT_NUMERO_FATTURA.intValue()))
            .andExpect(jsonPath("$.ragSociale").value(DEFAULT_RAG_SOCIALE))
            .andExpect(jsonPath("$.nomeCliente").value(DEFAULT_NOME_CLIENTE))
            .andExpect(jsonPath("$.imponibile").value(DEFAULT_IMPONIBILE.intValue()))
            .andExpect(jsonPath("$.iva").value(DEFAULT_IVA.intValue()))
            .andExpect(jsonPath("$.stato").value(DEFAULT_STATO.toString()))
            .andExpect(jsonPath("$.dataEmissione").value(DEFAULT_DATA_EMISSIONE.toString()))
            .andExpect(jsonPath("$.dataPagamento").value(DEFAULT_DATA_PAGAMENTO.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFattureAttivo() throws Exception {
        // Get the fattureAttivo
        restFattureAttivoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFattureAttivo() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        int databaseSizeBeforeUpdate = fattureAttivoRepository.findAll().size();

        // Update the fattureAttivo
        FattureAttivo updatedFattureAttivo = fattureAttivoRepository.findById(fattureAttivo.getId()).get();
        // Disconnect from session so that the updates on updatedFattureAttivo are not directly saved in db
        em.detach(updatedFattureAttivo);
        updatedFattureAttivo
            .numeroFattura(UPDATED_NUMERO_FATTURA)
            .ragSociale(UPDATED_RAG_SOCIALE)
            .nomeCliente(UPDATED_NOME_CLIENTE)
            .imponibile(UPDATED_IMPONIBILE)
            .iva(UPDATED_IVA)
            .stato(UPDATED_STATO)
            .dataEmissione(UPDATED_DATA_EMISSIONE)
            .dataPagamento(UPDATED_DATA_PAGAMENTO);

        restFattureAttivoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFattureAttivo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFattureAttivo))
            )
            .andExpect(status().isOk());

        // Validate the FattureAttivo in the database
        List<FattureAttivo> fattureAttivoList = fattureAttivoRepository.findAll();
        assertThat(fattureAttivoList).hasSize(databaseSizeBeforeUpdate);
        FattureAttivo testFattureAttivo = fattureAttivoList.get(fattureAttivoList.size() - 1);
        assertThat(testFattureAttivo.getNumeroFattura()).isEqualTo(UPDATED_NUMERO_FATTURA);
        assertThat(testFattureAttivo.getRagSociale()).isEqualTo(UPDATED_RAG_SOCIALE);
        assertThat(testFattureAttivo.getNomeCliente()).isEqualTo(UPDATED_NOME_CLIENTE);
        assertThat(testFattureAttivo.getImponibile()).isEqualTo(UPDATED_IMPONIBILE);
        assertThat(testFattureAttivo.getIva()).isEqualTo(UPDATED_IVA);
        assertThat(testFattureAttivo.getStato()).isEqualTo(UPDATED_STATO);
        assertThat(testFattureAttivo.getDataEmissione()).isEqualTo(UPDATED_DATA_EMISSIONE);
        assertThat(testFattureAttivo.getDataPagamento()).isEqualTo(UPDATED_DATA_PAGAMENTO);

        // Validate the FattureAttivo in Elasticsearch
        verify(mockFattureAttivoSearchRepository).save(testFattureAttivo);
    }

    @Test
    @Transactional
    void putNonExistingFattureAttivo() throws Exception {
        int databaseSizeBeforeUpdate = fattureAttivoRepository.findAll().size();
        fattureAttivo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFattureAttivoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fattureAttivo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fattureAttivo))
            )
            .andExpect(status().isBadRequest());

        // Validate the FattureAttivo in the database
        List<FattureAttivo> fattureAttivoList = fattureAttivoRepository.findAll();
        assertThat(fattureAttivoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FattureAttivo in Elasticsearch
        verify(mockFattureAttivoSearchRepository, times(0)).save(fattureAttivo);
    }

    @Test
    @Transactional
    void putWithIdMismatchFattureAttivo() throws Exception {
        int databaseSizeBeforeUpdate = fattureAttivoRepository.findAll().size();
        fattureAttivo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFattureAttivoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fattureAttivo))
            )
            .andExpect(status().isBadRequest());

        // Validate the FattureAttivo in the database
        List<FattureAttivo> fattureAttivoList = fattureAttivoRepository.findAll();
        assertThat(fattureAttivoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FattureAttivo in Elasticsearch
        verify(mockFattureAttivoSearchRepository, times(0)).save(fattureAttivo);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFattureAttivo() throws Exception {
        int databaseSizeBeforeUpdate = fattureAttivoRepository.findAll().size();
        fattureAttivo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFattureAttivoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fattureAttivo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FattureAttivo in the database
        List<FattureAttivo> fattureAttivoList = fattureAttivoRepository.findAll();
        assertThat(fattureAttivoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FattureAttivo in Elasticsearch
        verify(mockFattureAttivoSearchRepository, times(0)).save(fattureAttivo);
    }

    @Test
    @Transactional
    void partialUpdateFattureAttivoWithPatch() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        int databaseSizeBeforeUpdate = fattureAttivoRepository.findAll().size();

        // Update the fattureAttivo using partial update
        FattureAttivo partialUpdatedFattureAttivo = new FattureAttivo();
        partialUpdatedFattureAttivo.setId(fattureAttivo.getId());

        partialUpdatedFattureAttivo.numeroFattura(UPDATED_NUMERO_FATTURA).dataPagamento(UPDATED_DATA_PAGAMENTO);

        restFattureAttivoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFattureAttivo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFattureAttivo))
            )
            .andExpect(status().isOk());

        // Validate the FattureAttivo in the database
        List<FattureAttivo> fattureAttivoList = fattureAttivoRepository.findAll();
        assertThat(fattureAttivoList).hasSize(databaseSizeBeforeUpdate);
        FattureAttivo testFattureAttivo = fattureAttivoList.get(fattureAttivoList.size() - 1);
        assertThat(testFattureAttivo.getNumeroFattura()).isEqualTo(UPDATED_NUMERO_FATTURA);
        assertThat(testFattureAttivo.getRagSociale()).isEqualTo(DEFAULT_RAG_SOCIALE);
        assertThat(testFattureAttivo.getNomeCliente()).isEqualTo(DEFAULT_NOME_CLIENTE);
        assertThat(testFattureAttivo.getImponibile()).isEqualTo(DEFAULT_IMPONIBILE);
        assertThat(testFattureAttivo.getIva()).isEqualTo(DEFAULT_IVA);
        assertThat(testFattureAttivo.getStato()).isEqualTo(DEFAULT_STATO);
        assertThat(testFattureAttivo.getDataEmissione()).isEqualTo(DEFAULT_DATA_EMISSIONE);
        assertThat(testFattureAttivo.getDataPagamento()).isEqualTo(UPDATED_DATA_PAGAMENTO);
    }

    @Test
    @Transactional
    void fullUpdateFattureAttivoWithPatch() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        int databaseSizeBeforeUpdate = fattureAttivoRepository.findAll().size();

        // Update the fattureAttivo using partial update
        FattureAttivo partialUpdatedFattureAttivo = new FattureAttivo();
        partialUpdatedFattureAttivo.setId(fattureAttivo.getId());

        partialUpdatedFattureAttivo
            .numeroFattura(UPDATED_NUMERO_FATTURA)
            .ragSociale(UPDATED_RAG_SOCIALE)
            .nomeCliente(UPDATED_NOME_CLIENTE)
            .imponibile(UPDATED_IMPONIBILE)
            .iva(UPDATED_IVA)
            .stato(UPDATED_STATO)
            .dataEmissione(UPDATED_DATA_EMISSIONE)
            .dataPagamento(UPDATED_DATA_PAGAMENTO);

        restFattureAttivoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFattureAttivo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFattureAttivo))
            )
            .andExpect(status().isOk());

        // Validate the FattureAttivo in the database
        List<FattureAttivo> fattureAttivoList = fattureAttivoRepository.findAll();
        assertThat(fattureAttivoList).hasSize(databaseSizeBeforeUpdate);
        FattureAttivo testFattureAttivo = fattureAttivoList.get(fattureAttivoList.size() - 1);
        assertThat(testFattureAttivo.getNumeroFattura()).isEqualTo(UPDATED_NUMERO_FATTURA);
        assertThat(testFattureAttivo.getRagSociale()).isEqualTo(UPDATED_RAG_SOCIALE);
        assertThat(testFattureAttivo.getNomeCliente()).isEqualTo(UPDATED_NOME_CLIENTE);
        assertThat(testFattureAttivo.getImponibile()).isEqualTo(UPDATED_IMPONIBILE);
        assertThat(testFattureAttivo.getIva()).isEqualTo(UPDATED_IVA);
        assertThat(testFattureAttivo.getStato()).isEqualTo(UPDATED_STATO);
        assertThat(testFattureAttivo.getDataEmissione()).isEqualTo(UPDATED_DATA_EMISSIONE);
        assertThat(testFattureAttivo.getDataPagamento()).isEqualTo(UPDATED_DATA_PAGAMENTO);
    }

    @Test
    @Transactional
    void patchNonExistingFattureAttivo() throws Exception {
        int databaseSizeBeforeUpdate = fattureAttivoRepository.findAll().size();
        fattureAttivo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFattureAttivoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, fattureAttivo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(fattureAttivo))
            )
            .andExpect(status().isBadRequest());

        // Validate the FattureAttivo in the database
        List<FattureAttivo> fattureAttivoList = fattureAttivoRepository.findAll();
        assertThat(fattureAttivoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FattureAttivo in Elasticsearch
        verify(mockFattureAttivoSearchRepository, times(0)).save(fattureAttivo);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFattureAttivo() throws Exception {
        int databaseSizeBeforeUpdate = fattureAttivoRepository.findAll().size();
        fattureAttivo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFattureAttivoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(fattureAttivo))
            )
            .andExpect(status().isBadRequest());

        // Validate the FattureAttivo in the database
        List<FattureAttivo> fattureAttivoList = fattureAttivoRepository.findAll();
        assertThat(fattureAttivoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FattureAttivo in Elasticsearch
        verify(mockFattureAttivoSearchRepository, times(0)).save(fattureAttivo);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFattureAttivo() throws Exception {
        int databaseSizeBeforeUpdate = fattureAttivoRepository.findAll().size();
        fattureAttivo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFattureAttivoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(fattureAttivo))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FattureAttivo in the database
        List<FattureAttivo> fattureAttivoList = fattureAttivoRepository.findAll();
        assertThat(fattureAttivoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FattureAttivo in Elasticsearch
        verify(mockFattureAttivoSearchRepository, times(0)).save(fattureAttivo);
    }

    @Test
    @Transactional
    void deleteFattureAttivo() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        int databaseSizeBeforeDelete = fattureAttivoRepository.findAll().size();

        // Delete the fattureAttivo
        restFattureAttivoMockMvc
            .perform(delete(ENTITY_API_URL_ID, fattureAttivo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FattureAttivo> fattureAttivoList = fattureAttivoRepository.findAll();
        assertThat(fattureAttivoList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the FattureAttivo in Elasticsearch
        verify(mockFattureAttivoSearchRepository, times(1)).deleteById(fattureAttivo.getId());
    }

    @Test
    @Transactional
    void searchFattureAttivo() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);
        when(mockFattureAttivoSearchRepository.search("id:" + fattureAttivo.getId())).thenReturn(Stream.of(fattureAttivo));

        // Search the fattureAttivo
        restFattureAttivoMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + fattureAttivo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fattureAttivo.getId().intValue())))
            .andExpect(jsonPath("$.[*].numeroFattura").value(hasItem(DEFAULT_NUMERO_FATTURA.intValue())))
            .andExpect(jsonPath("$.[*].ragSociale").value(hasItem(DEFAULT_RAG_SOCIALE)))
            .andExpect(jsonPath("$.[*].nomeCliente").value(hasItem(DEFAULT_NOME_CLIENTE)))
            .andExpect(jsonPath("$.[*].imponibile").value(hasItem(DEFAULT_IMPONIBILE.intValue())))
            .andExpect(jsonPath("$.[*].iva").value(hasItem(DEFAULT_IVA.intValue())))
            .andExpect(jsonPath("$.[*].stato").value(hasItem(DEFAULT_STATO.toString())))
            .andExpect(jsonPath("$.[*].dataEmissione").value(hasItem(DEFAULT_DATA_EMISSIONE.toString())))
            .andExpect(jsonPath("$.[*].dataPagamento").value(hasItem(DEFAULT_DATA_PAGAMENTO.toString())));
    }
}
