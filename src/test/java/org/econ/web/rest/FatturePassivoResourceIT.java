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
import org.econ.domain.Cantiere;
import org.econ.domain.FatturePassivo;
import org.econ.domain.Fornitore;
import org.econ.domain.enumeration.Stato;
import org.econ.repository.FatturePassivoRepository;
import org.econ.repository.search.FatturePassivoSearchRepository;
import org.econ.service.criteria.FatturePassivoCriteria;
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
    private static final Long SMALLER_NUMERO_FATTURA = 1L - 1L;

    private static final String DEFAULT_RAG_SOCIALE = "AAAAAAAAAA";
    private static final String UPDATED_RAG_SOCIALE = "BBBBBBBBBB";

    private static final String DEFAULT_NOME_FORNITORE = "AAAAAAAAAA";
    private static final String UPDATED_NOME_FORNITORE = "BBBBBBBBBB";

    private static final Long DEFAULT_IMPONIBILE = 1L;
    private static final Long UPDATED_IMPONIBILE = 2L;
    private static final Long SMALLER_IMPONIBILE = 1L - 1L;

    private static final Long DEFAULT_IVA = 1L;
    private static final Long UPDATED_IVA = 2L;
    private static final Long SMALLER_IVA = 1L - 1L;

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
    void getFatturePassivosByIdFiltering() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        Long id = fatturePassivo.getId();

        defaultFatturePassivoShouldBeFound("id.equals=" + id);
        defaultFatturePassivoShouldNotBeFound("id.notEquals=" + id);

        defaultFatturePassivoShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultFatturePassivoShouldNotBeFound("id.greaterThan=" + id);

        defaultFatturePassivoShouldBeFound("id.lessThanOrEqual=" + id);
        defaultFatturePassivoShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByNumeroFatturaIsEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where numeroFattura equals to DEFAULT_NUMERO_FATTURA
        defaultFatturePassivoShouldBeFound("numeroFattura.equals=" + DEFAULT_NUMERO_FATTURA);

        // Get all the fatturePassivoList where numeroFattura equals to UPDATED_NUMERO_FATTURA
        defaultFatturePassivoShouldNotBeFound("numeroFattura.equals=" + UPDATED_NUMERO_FATTURA);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByNumeroFatturaIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where numeroFattura not equals to DEFAULT_NUMERO_FATTURA
        defaultFatturePassivoShouldNotBeFound("numeroFattura.notEquals=" + DEFAULT_NUMERO_FATTURA);

        // Get all the fatturePassivoList where numeroFattura not equals to UPDATED_NUMERO_FATTURA
        defaultFatturePassivoShouldBeFound("numeroFattura.notEquals=" + UPDATED_NUMERO_FATTURA);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByNumeroFatturaIsInShouldWork() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where numeroFattura in DEFAULT_NUMERO_FATTURA or UPDATED_NUMERO_FATTURA
        defaultFatturePassivoShouldBeFound("numeroFattura.in=" + DEFAULT_NUMERO_FATTURA + "," + UPDATED_NUMERO_FATTURA);

        // Get all the fatturePassivoList where numeroFattura equals to UPDATED_NUMERO_FATTURA
        defaultFatturePassivoShouldNotBeFound("numeroFattura.in=" + UPDATED_NUMERO_FATTURA);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByNumeroFatturaIsNullOrNotNull() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where numeroFattura is not null
        defaultFatturePassivoShouldBeFound("numeroFattura.specified=true");

        // Get all the fatturePassivoList where numeroFattura is null
        defaultFatturePassivoShouldNotBeFound("numeroFattura.specified=false");
    }

    @Test
    @Transactional
    void getAllFatturePassivosByNumeroFatturaIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where numeroFattura is greater than or equal to DEFAULT_NUMERO_FATTURA
        defaultFatturePassivoShouldBeFound("numeroFattura.greaterThanOrEqual=" + DEFAULT_NUMERO_FATTURA);

        // Get all the fatturePassivoList where numeroFattura is greater than or equal to UPDATED_NUMERO_FATTURA
        defaultFatturePassivoShouldNotBeFound("numeroFattura.greaterThanOrEqual=" + UPDATED_NUMERO_FATTURA);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByNumeroFatturaIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where numeroFattura is less than or equal to DEFAULT_NUMERO_FATTURA
        defaultFatturePassivoShouldBeFound("numeroFattura.lessThanOrEqual=" + DEFAULT_NUMERO_FATTURA);

        // Get all the fatturePassivoList where numeroFattura is less than or equal to SMALLER_NUMERO_FATTURA
        defaultFatturePassivoShouldNotBeFound("numeroFattura.lessThanOrEqual=" + SMALLER_NUMERO_FATTURA);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByNumeroFatturaIsLessThanSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where numeroFattura is less than DEFAULT_NUMERO_FATTURA
        defaultFatturePassivoShouldNotBeFound("numeroFattura.lessThan=" + DEFAULT_NUMERO_FATTURA);

        // Get all the fatturePassivoList where numeroFattura is less than UPDATED_NUMERO_FATTURA
        defaultFatturePassivoShouldBeFound("numeroFattura.lessThan=" + UPDATED_NUMERO_FATTURA);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByNumeroFatturaIsGreaterThanSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where numeroFattura is greater than DEFAULT_NUMERO_FATTURA
        defaultFatturePassivoShouldNotBeFound("numeroFattura.greaterThan=" + DEFAULT_NUMERO_FATTURA);

        // Get all the fatturePassivoList where numeroFattura is greater than SMALLER_NUMERO_FATTURA
        defaultFatturePassivoShouldBeFound("numeroFattura.greaterThan=" + SMALLER_NUMERO_FATTURA);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByRagSocialeIsEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where ragSociale equals to DEFAULT_RAG_SOCIALE
        defaultFatturePassivoShouldBeFound("ragSociale.equals=" + DEFAULT_RAG_SOCIALE);

        // Get all the fatturePassivoList where ragSociale equals to UPDATED_RAG_SOCIALE
        defaultFatturePassivoShouldNotBeFound("ragSociale.equals=" + UPDATED_RAG_SOCIALE);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByRagSocialeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where ragSociale not equals to DEFAULT_RAG_SOCIALE
        defaultFatturePassivoShouldNotBeFound("ragSociale.notEquals=" + DEFAULT_RAG_SOCIALE);

        // Get all the fatturePassivoList where ragSociale not equals to UPDATED_RAG_SOCIALE
        defaultFatturePassivoShouldBeFound("ragSociale.notEquals=" + UPDATED_RAG_SOCIALE);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByRagSocialeIsInShouldWork() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where ragSociale in DEFAULT_RAG_SOCIALE or UPDATED_RAG_SOCIALE
        defaultFatturePassivoShouldBeFound("ragSociale.in=" + DEFAULT_RAG_SOCIALE + "," + UPDATED_RAG_SOCIALE);

        // Get all the fatturePassivoList where ragSociale equals to UPDATED_RAG_SOCIALE
        defaultFatturePassivoShouldNotBeFound("ragSociale.in=" + UPDATED_RAG_SOCIALE);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByRagSocialeIsNullOrNotNull() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where ragSociale is not null
        defaultFatturePassivoShouldBeFound("ragSociale.specified=true");

        // Get all the fatturePassivoList where ragSociale is null
        defaultFatturePassivoShouldNotBeFound("ragSociale.specified=false");
    }

    @Test
    @Transactional
    void getAllFatturePassivosByRagSocialeContainsSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where ragSociale contains DEFAULT_RAG_SOCIALE
        defaultFatturePassivoShouldBeFound("ragSociale.contains=" + DEFAULT_RAG_SOCIALE);

        // Get all the fatturePassivoList where ragSociale contains UPDATED_RAG_SOCIALE
        defaultFatturePassivoShouldNotBeFound("ragSociale.contains=" + UPDATED_RAG_SOCIALE);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByRagSocialeNotContainsSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where ragSociale does not contain DEFAULT_RAG_SOCIALE
        defaultFatturePassivoShouldNotBeFound("ragSociale.doesNotContain=" + DEFAULT_RAG_SOCIALE);

        // Get all the fatturePassivoList where ragSociale does not contain UPDATED_RAG_SOCIALE
        defaultFatturePassivoShouldBeFound("ragSociale.doesNotContain=" + UPDATED_RAG_SOCIALE);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByNomeFornitoreIsEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where nomeFornitore equals to DEFAULT_NOME_FORNITORE
        defaultFatturePassivoShouldBeFound("nomeFornitore.equals=" + DEFAULT_NOME_FORNITORE);

        // Get all the fatturePassivoList where nomeFornitore equals to UPDATED_NOME_FORNITORE
        defaultFatturePassivoShouldNotBeFound("nomeFornitore.equals=" + UPDATED_NOME_FORNITORE);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByNomeFornitoreIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where nomeFornitore not equals to DEFAULT_NOME_FORNITORE
        defaultFatturePassivoShouldNotBeFound("nomeFornitore.notEquals=" + DEFAULT_NOME_FORNITORE);

        // Get all the fatturePassivoList where nomeFornitore not equals to UPDATED_NOME_FORNITORE
        defaultFatturePassivoShouldBeFound("nomeFornitore.notEquals=" + UPDATED_NOME_FORNITORE);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByNomeFornitoreIsInShouldWork() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where nomeFornitore in DEFAULT_NOME_FORNITORE or UPDATED_NOME_FORNITORE
        defaultFatturePassivoShouldBeFound("nomeFornitore.in=" + DEFAULT_NOME_FORNITORE + "," + UPDATED_NOME_FORNITORE);

        // Get all the fatturePassivoList where nomeFornitore equals to UPDATED_NOME_FORNITORE
        defaultFatturePassivoShouldNotBeFound("nomeFornitore.in=" + UPDATED_NOME_FORNITORE);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByNomeFornitoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where nomeFornitore is not null
        defaultFatturePassivoShouldBeFound("nomeFornitore.specified=true");

        // Get all the fatturePassivoList where nomeFornitore is null
        defaultFatturePassivoShouldNotBeFound("nomeFornitore.specified=false");
    }

    @Test
    @Transactional
    void getAllFatturePassivosByNomeFornitoreContainsSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where nomeFornitore contains DEFAULT_NOME_FORNITORE
        defaultFatturePassivoShouldBeFound("nomeFornitore.contains=" + DEFAULT_NOME_FORNITORE);

        // Get all the fatturePassivoList where nomeFornitore contains UPDATED_NOME_FORNITORE
        defaultFatturePassivoShouldNotBeFound("nomeFornitore.contains=" + UPDATED_NOME_FORNITORE);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByNomeFornitoreNotContainsSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where nomeFornitore does not contain DEFAULT_NOME_FORNITORE
        defaultFatturePassivoShouldNotBeFound("nomeFornitore.doesNotContain=" + DEFAULT_NOME_FORNITORE);

        // Get all the fatturePassivoList where nomeFornitore does not contain UPDATED_NOME_FORNITORE
        defaultFatturePassivoShouldBeFound("nomeFornitore.doesNotContain=" + UPDATED_NOME_FORNITORE);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByImponibileIsEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where imponibile equals to DEFAULT_IMPONIBILE
        defaultFatturePassivoShouldBeFound("imponibile.equals=" + DEFAULT_IMPONIBILE);

        // Get all the fatturePassivoList where imponibile equals to UPDATED_IMPONIBILE
        defaultFatturePassivoShouldNotBeFound("imponibile.equals=" + UPDATED_IMPONIBILE);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByImponibileIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where imponibile not equals to DEFAULT_IMPONIBILE
        defaultFatturePassivoShouldNotBeFound("imponibile.notEquals=" + DEFAULT_IMPONIBILE);

        // Get all the fatturePassivoList where imponibile not equals to UPDATED_IMPONIBILE
        defaultFatturePassivoShouldBeFound("imponibile.notEquals=" + UPDATED_IMPONIBILE);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByImponibileIsInShouldWork() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where imponibile in DEFAULT_IMPONIBILE or UPDATED_IMPONIBILE
        defaultFatturePassivoShouldBeFound("imponibile.in=" + DEFAULT_IMPONIBILE + "," + UPDATED_IMPONIBILE);

        // Get all the fatturePassivoList where imponibile equals to UPDATED_IMPONIBILE
        defaultFatturePassivoShouldNotBeFound("imponibile.in=" + UPDATED_IMPONIBILE);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByImponibileIsNullOrNotNull() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where imponibile is not null
        defaultFatturePassivoShouldBeFound("imponibile.specified=true");

        // Get all the fatturePassivoList where imponibile is null
        defaultFatturePassivoShouldNotBeFound("imponibile.specified=false");
    }

    @Test
    @Transactional
    void getAllFatturePassivosByImponibileIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where imponibile is greater than or equal to DEFAULT_IMPONIBILE
        defaultFatturePassivoShouldBeFound("imponibile.greaterThanOrEqual=" + DEFAULT_IMPONIBILE);

        // Get all the fatturePassivoList where imponibile is greater than or equal to UPDATED_IMPONIBILE
        defaultFatturePassivoShouldNotBeFound("imponibile.greaterThanOrEqual=" + UPDATED_IMPONIBILE);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByImponibileIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where imponibile is less than or equal to DEFAULT_IMPONIBILE
        defaultFatturePassivoShouldBeFound("imponibile.lessThanOrEqual=" + DEFAULT_IMPONIBILE);

        // Get all the fatturePassivoList where imponibile is less than or equal to SMALLER_IMPONIBILE
        defaultFatturePassivoShouldNotBeFound("imponibile.lessThanOrEqual=" + SMALLER_IMPONIBILE);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByImponibileIsLessThanSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where imponibile is less than DEFAULT_IMPONIBILE
        defaultFatturePassivoShouldNotBeFound("imponibile.lessThan=" + DEFAULT_IMPONIBILE);

        // Get all the fatturePassivoList where imponibile is less than UPDATED_IMPONIBILE
        defaultFatturePassivoShouldBeFound("imponibile.lessThan=" + UPDATED_IMPONIBILE);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByImponibileIsGreaterThanSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where imponibile is greater than DEFAULT_IMPONIBILE
        defaultFatturePassivoShouldNotBeFound("imponibile.greaterThan=" + DEFAULT_IMPONIBILE);

        // Get all the fatturePassivoList where imponibile is greater than SMALLER_IMPONIBILE
        defaultFatturePassivoShouldBeFound("imponibile.greaterThan=" + SMALLER_IMPONIBILE);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByIvaIsEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where iva equals to DEFAULT_IVA
        defaultFatturePassivoShouldBeFound("iva.equals=" + DEFAULT_IVA);

        // Get all the fatturePassivoList where iva equals to UPDATED_IVA
        defaultFatturePassivoShouldNotBeFound("iva.equals=" + UPDATED_IVA);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByIvaIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where iva not equals to DEFAULT_IVA
        defaultFatturePassivoShouldNotBeFound("iva.notEquals=" + DEFAULT_IVA);

        // Get all the fatturePassivoList where iva not equals to UPDATED_IVA
        defaultFatturePassivoShouldBeFound("iva.notEquals=" + UPDATED_IVA);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByIvaIsInShouldWork() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where iva in DEFAULT_IVA or UPDATED_IVA
        defaultFatturePassivoShouldBeFound("iva.in=" + DEFAULT_IVA + "," + UPDATED_IVA);

        // Get all the fatturePassivoList where iva equals to UPDATED_IVA
        defaultFatturePassivoShouldNotBeFound("iva.in=" + UPDATED_IVA);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByIvaIsNullOrNotNull() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where iva is not null
        defaultFatturePassivoShouldBeFound("iva.specified=true");

        // Get all the fatturePassivoList where iva is null
        defaultFatturePassivoShouldNotBeFound("iva.specified=false");
    }

    @Test
    @Transactional
    void getAllFatturePassivosByIvaIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where iva is greater than or equal to DEFAULT_IVA
        defaultFatturePassivoShouldBeFound("iva.greaterThanOrEqual=" + DEFAULT_IVA);

        // Get all the fatturePassivoList where iva is greater than or equal to UPDATED_IVA
        defaultFatturePassivoShouldNotBeFound("iva.greaterThanOrEqual=" + UPDATED_IVA);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByIvaIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where iva is less than or equal to DEFAULT_IVA
        defaultFatturePassivoShouldBeFound("iva.lessThanOrEqual=" + DEFAULT_IVA);

        // Get all the fatturePassivoList where iva is less than or equal to SMALLER_IVA
        defaultFatturePassivoShouldNotBeFound("iva.lessThanOrEqual=" + SMALLER_IVA);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByIvaIsLessThanSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where iva is less than DEFAULT_IVA
        defaultFatturePassivoShouldNotBeFound("iva.lessThan=" + DEFAULT_IVA);

        // Get all the fatturePassivoList where iva is less than UPDATED_IVA
        defaultFatturePassivoShouldBeFound("iva.lessThan=" + UPDATED_IVA);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByIvaIsGreaterThanSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where iva is greater than DEFAULT_IVA
        defaultFatturePassivoShouldNotBeFound("iva.greaterThan=" + DEFAULT_IVA);

        // Get all the fatturePassivoList where iva is greater than SMALLER_IVA
        defaultFatturePassivoShouldBeFound("iva.greaterThan=" + SMALLER_IVA);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByStatoIsEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where stato equals to DEFAULT_STATO
        defaultFatturePassivoShouldBeFound("stato.equals=" + DEFAULT_STATO);

        // Get all the fatturePassivoList where stato equals to UPDATED_STATO
        defaultFatturePassivoShouldNotBeFound("stato.equals=" + UPDATED_STATO);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByStatoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where stato not equals to DEFAULT_STATO
        defaultFatturePassivoShouldNotBeFound("stato.notEquals=" + DEFAULT_STATO);

        // Get all the fatturePassivoList where stato not equals to UPDATED_STATO
        defaultFatturePassivoShouldBeFound("stato.notEquals=" + UPDATED_STATO);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByStatoIsInShouldWork() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where stato in DEFAULT_STATO or UPDATED_STATO
        defaultFatturePassivoShouldBeFound("stato.in=" + DEFAULT_STATO + "," + UPDATED_STATO);

        // Get all the fatturePassivoList where stato equals to UPDATED_STATO
        defaultFatturePassivoShouldNotBeFound("stato.in=" + UPDATED_STATO);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByStatoIsNullOrNotNull() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where stato is not null
        defaultFatturePassivoShouldBeFound("stato.specified=true");

        // Get all the fatturePassivoList where stato is null
        defaultFatturePassivoShouldNotBeFound("stato.specified=false");
    }

    @Test
    @Transactional
    void getAllFatturePassivosByDataEmissioneIsEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where dataEmissione equals to DEFAULT_DATA_EMISSIONE
        defaultFatturePassivoShouldBeFound("dataEmissione.equals=" + DEFAULT_DATA_EMISSIONE);

        // Get all the fatturePassivoList where dataEmissione equals to UPDATED_DATA_EMISSIONE
        defaultFatturePassivoShouldNotBeFound("dataEmissione.equals=" + UPDATED_DATA_EMISSIONE);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByDataEmissioneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where dataEmissione not equals to DEFAULT_DATA_EMISSIONE
        defaultFatturePassivoShouldNotBeFound("dataEmissione.notEquals=" + DEFAULT_DATA_EMISSIONE);

        // Get all the fatturePassivoList where dataEmissione not equals to UPDATED_DATA_EMISSIONE
        defaultFatturePassivoShouldBeFound("dataEmissione.notEquals=" + UPDATED_DATA_EMISSIONE);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByDataEmissioneIsInShouldWork() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where dataEmissione in DEFAULT_DATA_EMISSIONE or UPDATED_DATA_EMISSIONE
        defaultFatturePassivoShouldBeFound("dataEmissione.in=" + DEFAULT_DATA_EMISSIONE + "," + UPDATED_DATA_EMISSIONE);

        // Get all the fatturePassivoList where dataEmissione equals to UPDATED_DATA_EMISSIONE
        defaultFatturePassivoShouldNotBeFound("dataEmissione.in=" + UPDATED_DATA_EMISSIONE);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByDataEmissioneIsNullOrNotNull() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where dataEmissione is not null
        defaultFatturePassivoShouldBeFound("dataEmissione.specified=true");

        // Get all the fatturePassivoList where dataEmissione is null
        defaultFatturePassivoShouldNotBeFound("dataEmissione.specified=false");
    }

    @Test
    @Transactional
    void getAllFatturePassivosByDataPagamentoIsEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where dataPagamento equals to DEFAULT_DATA_PAGAMENTO
        defaultFatturePassivoShouldBeFound("dataPagamento.equals=" + DEFAULT_DATA_PAGAMENTO);

        // Get all the fatturePassivoList where dataPagamento equals to UPDATED_DATA_PAGAMENTO
        defaultFatturePassivoShouldNotBeFound("dataPagamento.equals=" + UPDATED_DATA_PAGAMENTO);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByDataPagamentoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where dataPagamento not equals to DEFAULT_DATA_PAGAMENTO
        defaultFatturePassivoShouldNotBeFound("dataPagamento.notEquals=" + DEFAULT_DATA_PAGAMENTO);

        // Get all the fatturePassivoList where dataPagamento not equals to UPDATED_DATA_PAGAMENTO
        defaultFatturePassivoShouldBeFound("dataPagamento.notEquals=" + UPDATED_DATA_PAGAMENTO);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByDataPagamentoIsInShouldWork() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where dataPagamento in DEFAULT_DATA_PAGAMENTO or UPDATED_DATA_PAGAMENTO
        defaultFatturePassivoShouldBeFound("dataPagamento.in=" + DEFAULT_DATA_PAGAMENTO + "," + UPDATED_DATA_PAGAMENTO);

        // Get all the fatturePassivoList where dataPagamento equals to UPDATED_DATA_PAGAMENTO
        defaultFatturePassivoShouldNotBeFound("dataPagamento.in=" + UPDATED_DATA_PAGAMENTO);
    }

    @Test
    @Transactional
    void getAllFatturePassivosByDataPagamentoIsNullOrNotNull() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);

        // Get all the fatturePassivoList where dataPagamento is not null
        defaultFatturePassivoShouldBeFound("dataPagamento.specified=true");

        // Get all the fatturePassivoList where dataPagamento is null
        defaultFatturePassivoShouldNotBeFound("dataPagamento.specified=false");
    }

    @Test
    @Transactional
    void getAllFatturePassivosByFornitoreIsEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);
        Fornitore fornitore;
        if (TestUtil.findAll(em, Fornitore.class).isEmpty()) {
            fornitore = FornitoreResourceIT.createEntity(em);
            em.persist(fornitore);
            em.flush();
        } else {
            fornitore = TestUtil.findAll(em, Fornitore.class).get(0);
        }
        em.persist(fornitore);
        em.flush();
        fatturePassivo.setFornitore(fornitore);
        fatturePassivoRepository.saveAndFlush(fatturePassivo);
        Long fornitoreId = fornitore.getId();

        // Get all the fatturePassivoList where fornitore equals to fornitoreId
        defaultFatturePassivoShouldBeFound("fornitoreId.equals=" + fornitoreId);

        // Get all the fatturePassivoList where fornitore equals to (fornitoreId + 1)
        defaultFatturePassivoShouldNotBeFound("fornitoreId.equals=" + (fornitoreId + 1));
    }

    @Test
    @Transactional
    void getAllFatturePassivosByCantiereIsEqualToSomething() throws Exception {
        // Initialize the database
        fatturePassivoRepository.saveAndFlush(fatturePassivo);
        Cantiere cantiere;
        if (TestUtil.findAll(em, Cantiere.class).isEmpty()) {
            cantiere = CantiereResourceIT.createEntity(em);
            em.persist(cantiere);
            em.flush();
        } else {
            cantiere = TestUtil.findAll(em, Cantiere.class).get(0);
        }
        em.persist(cantiere);
        em.flush();
        fatturePassivo.setCantiere(cantiere);
        fatturePassivoRepository.saveAndFlush(fatturePassivo);
        Long cantiereId = cantiere.getId();

        // Get all the fatturePassivoList where cantiere equals to cantiereId
        defaultFatturePassivoShouldBeFound("cantiereId.equals=" + cantiereId);

        // Get all the fatturePassivoList where cantiere equals to (cantiereId + 1)
        defaultFatturePassivoShouldNotBeFound("cantiereId.equals=" + (cantiereId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFatturePassivoShouldBeFound(String filter) throws Exception {
        restFatturePassivoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
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

        // Check, that the count call also returns 1
        restFatturePassivoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFatturePassivoShouldNotBeFound(String filter) throws Exception {
        restFatturePassivoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFatturePassivoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
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
