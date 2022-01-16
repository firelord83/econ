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
import org.econ.domain.Cliente;
import org.econ.domain.FattureAttivo;
import org.econ.domain.enumeration.Stato;
import org.econ.repository.FattureAttivoRepository;
import org.econ.repository.search.FattureAttivoSearchRepository;
import org.econ.service.criteria.FattureAttivoCriteria;
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
    private static final Long SMALLER_NUMERO_FATTURA = 1L - 1L;

    private static final String DEFAULT_RAG_SOCIALE = "AAAAAAAAAA";
    private static final String UPDATED_RAG_SOCIALE = "BBBBBBBBBB";

    private static final String DEFAULT_NOME_CLIENTE = "AAAAAAAAAA";
    private static final String UPDATED_NOME_CLIENTE = "BBBBBBBBBB";

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
    void getFattureAttivosByIdFiltering() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        Long id = fattureAttivo.getId();

        defaultFattureAttivoShouldBeFound("id.equals=" + id);
        defaultFattureAttivoShouldNotBeFound("id.notEquals=" + id);

        defaultFattureAttivoShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultFattureAttivoShouldNotBeFound("id.greaterThan=" + id);

        defaultFattureAttivoShouldBeFound("id.lessThanOrEqual=" + id);
        defaultFattureAttivoShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByNumeroFatturaIsEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where numeroFattura equals to DEFAULT_NUMERO_FATTURA
        defaultFattureAttivoShouldBeFound("numeroFattura.equals=" + DEFAULT_NUMERO_FATTURA);

        // Get all the fattureAttivoList where numeroFattura equals to UPDATED_NUMERO_FATTURA
        defaultFattureAttivoShouldNotBeFound("numeroFattura.equals=" + UPDATED_NUMERO_FATTURA);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByNumeroFatturaIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where numeroFattura not equals to DEFAULT_NUMERO_FATTURA
        defaultFattureAttivoShouldNotBeFound("numeroFattura.notEquals=" + DEFAULT_NUMERO_FATTURA);

        // Get all the fattureAttivoList where numeroFattura not equals to UPDATED_NUMERO_FATTURA
        defaultFattureAttivoShouldBeFound("numeroFattura.notEquals=" + UPDATED_NUMERO_FATTURA);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByNumeroFatturaIsInShouldWork() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where numeroFattura in DEFAULT_NUMERO_FATTURA or UPDATED_NUMERO_FATTURA
        defaultFattureAttivoShouldBeFound("numeroFattura.in=" + DEFAULT_NUMERO_FATTURA + "," + UPDATED_NUMERO_FATTURA);

        // Get all the fattureAttivoList where numeroFattura equals to UPDATED_NUMERO_FATTURA
        defaultFattureAttivoShouldNotBeFound("numeroFattura.in=" + UPDATED_NUMERO_FATTURA);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByNumeroFatturaIsNullOrNotNull() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where numeroFattura is not null
        defaultFattureAttivoShouldBeFound("numeroFattura.specified=true");

        // Get all the fattureAttivoList where numeroFattura is null
        defaultFattureAttivoShouldNotBeFound("numeroFattura.specified=false");
    }

    @Test
    @Transactional
    void getAllFattureAttivosByNumeroFatturaIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where numeroFattura is greater than or equal to DEFAULT_NUMERO_FATTURA
        defaultFattureAttivoShouldBeFound("numeroFattura.greaterThanOrEqual=" + DEFAULT_NUMERO_FATTURA);

        // Get all the fattureAttivoList where numeroFattura is greater than or equal to UPDATED_NUMERO_FATTURA
        defaultFattureAttivoShouldNotBeFound("numeroFattura.greaterThanOrEqual=" + UPDATED_NUMERO_FATTURA);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByNumeroFatturaIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where numeroFattura is less than or equal to DEFAULT_NUMERO_FATTURA
        defaultFattureAttivoShouldBeFound("numeroFattura.lessThanOrEqual=" + DEFAULT_NUMERO_FATTURA);

        // Get all the fattureAttivoList where numeroFattura is less than or equal to SMALLER_NUMERO_FATTURA
        defaultFattureAttivoShouldNotBeFound("numeroFattura.lessThanOrEqual=" + SMALLER_NUMERO_FATTURA);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByNumeroFatturaIsLessThanSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where numeroFattura is less than DEFAULT_NUMERO_FATTURA
        defaultFattureAttivoShouldNotBeFound("numeroFattura.lessThan=" + DEFAULT_NUMERO_FATTURA);

        // Get all the fattureAttivoList where numeroFattura is less than UPDATED_NUMERO_FATTURA
        defaultFattureAttivoShouldBeFound("numeroFattura.lessThan=" + UPDATED_NUMERO_FATTURA);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByNumeroFatturaIsGreaterThanSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where numeroFattura is greater than DEFAULT_NUMERO_FATTURA
        defaultFattureAttivoShouldNotBeFound("numeroFattura.greaterThan=" + DEFAULT_NUMERO_FATTURA);

        // Get all the fattureAttivoList where numeroFattura is greater than SMALLER_NUMERO_FATTURA
        defaultFattureAttivoShouldBeFound("numeroFattura.greaterThan=" + SMALLER_NUMERO_FATTURA);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByRagSocialeIsEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where ragSociale equals to DEFAULT_RAG_SOCIALE
        defaultFattureAttivoShouldBeFound("ragSociale.equals=" + DEFAULT_RAG_SOCIALE);

        // Get all the fattureAttivoList where ragSociale equals to UPDATED_RAG_SOCIALE
        defaultFattureAttivoShouldNotBeFound("ragSociale.equals=" + UPDATED_RAG_SOCIALE);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByRagSocialeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where ragSociale not equals to DEFAULT_RAG_SOCIALE
        defaultFattureAttivoShouldNotBeFound("ragSociale.notEquals=" + DEFAULT_RAG_SOCIALE);

        // Get all the fattureAttivoList where ragSociale not equals to UPDATED_RAG_SOCIALE
        defaultFattureAttivoShouldBeFound("ragSociale.notEquals=" + UPDATED_RAG_SOCIALE);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByRagSocialeIsInShouldWork() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where ragSociale in DEFAULT_RAG_SOCIALE or UPDATED_RAG_SOCIALE
        defaultFattureAttivoShouldBeFound("ragSociale.in=" + DEFAULT_RAG_SOCIALE + "," + UPDATED_RAG_SOCIALE);

        // Get all the fattureAttivoList where ragSociale equals to UPDATED_RAG_SOCIALE
        defaultFattureAttivoShouldNotBeFound("ragSociale.in=" + UPDATED_RAG_SOCIALE);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByRagSocialeIsNullOrNotNull() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where ragSociale is not null
        defaultFattureAttivoShouldBeFound("ragSociale.specified=true");

        // Get all the fattureAttivoList where ragSociale is null
        defaultFattureAttivoShouldNotBeFound("ragSociale.specified=false");
    }

    @Test
    @Transactional
    void getAllFattureAttivosByRagSocialeContainsSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where ragSociale contains DEFAULT_RAG_SOCIALE
        defaultFattureAttivoShouldBeFound("ragSociale.contains=" + DEFAULT_RAG_SOCIALE);

        // Get all the fattureAttivoList where ragSociale contains UPDATED_RAG_SOCIALE
        defaultFattureAttivoShouldNotBeFound("ragSociale.contains=" + UPDATED_RAG_SOCIALE);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByRagSocialeNotContainsSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where ragSociale does not contain DEFAULT_RAG_SOCIALE
        defaultFattureAttivoShouldNotBeFound("ragSociale.doesNotContain=" + DEFAULT_RAG_SOCIALE);

        // Get all the fattureAttivoList where ragSociale does not contain UPDATED_RAG_SOCIALE
        defaultFattureAttivoShouldBeFound("ragSociale.doesNotContain=" + UPDATED_RAG_SOCIALE);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByNomeClienteIsEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where nomeCliente equals to DEFAULT_NOME_CLIENTE
        defaultFattureAttivoShouldBeFound("nomeCliente.equals=" + DEFAULT_NOME_CLIENTE);

        // Get all the fattureAttivoList where nomeCliente equals to UPDATED_NOME_CLIENTE
        defaultFattureAttivoShouldNotBeFound("nomeCliente.equals=" + UPDATED_NOME_CLIENTE);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByNomeClienteIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where nomeCliente not equals to DEFAULT_NOME_CLIENTE
        defaultFattureAttivoShouldNotBeFound("nomeCliente.notEquals=" + DEFAULT_NOME_CLIENTE);

        // Get all the fattureAttivoList where nomeCliente not equals to UPDATED_NOME_CLIENTE
        defaultFattureAttivoShouldBeFound("nomeCliente.notEquals=" + UPDATED_NOME_CLIENTE);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByNomeClienteIsInShouldWork() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where nomeCliente in DEFAULT_NOME_CLIENTE or UPDATED_NOME_CLIENTE
        defaultFattureAttivoShouldBeFound("nomeCliente.in=" + DEFAULT_NOME_CLIENTE + "," + UPDATED_NOME_CLIENTE);

        // Get all the fattureAttivoList where nomeCliente equals to UPDATED_NOME_CLIENTE
        defaultFattureAttivoShouldNotBeFound("nomeCliente.in=" + UPDATED_NOME_CLIENTE);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByNomeClienteIsNullOrNotNull() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where nomeCliente is not null
        defaultFattureAttivoShouldBeFound("nomeCliente.specified=true");

        // Get all the fattureAttivoList where nomeCliente is null
        defaultFattureAttivoShouldNotBeFound("nomeCliente.specified=false");
    }

    @Test
    @Transactional
    void getAllFattureAttivosByNomeClienteContainsSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where nomeCliente contains DEFAULT_NOME_CLIENTE
        defaultFattureAttivoShouldBeFound("nomeCliente.contains=" + DEFAULT_NOME_CLIENTE);

        // Get all the fattureAttivoList where nomeCliente contains UPDATED_NOME_CLIENTE
        defaultFattureAttivoShouldNotBeFound("nomeCliente.contains=" + UPDATED_NOME_CLIENTE);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByNomeClienteNotContainsSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where nomeCliente does not contain DEFAULT_NOME_CLIENTE
        defaultFattureAttivoShouldNotBeFound("nomeCliente.doesNotContain=" + DEFAULT_NOME_CLIENTE);

        // Get all the fattureAttivoList where nomeCliente does not contain UPDATED_NOME_CLIENTE
        defaultFattureAttivoShouldBeFound("nomeCliente.doesNotContain=" + UPDATED_NOME_CLIENTE);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByImponibileIsEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where imponibile equals to DEFAULT_IMPONIBILE
        defaultFattureAttivoShouldBeFound("imponibile.equals=" + DEFAULT_IMPONIBILE);

        // Get all the fattureAttivoList where imponibile equals to UPDATED_IMPONIBILE
        defaultFattureAttivoShouldNotBeFound("imponibile.equals=" + UPDATED_IMPONIBILE);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByImponibileIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where imponibile not equals to DEFAULT_IMPONIBILE
        defaultFattureAttivoShouldNotBeFound("imponibile.notEquals=" + DEFAULT_IMPONIBILE);

        // Get all the fattureAttivoList where imponibile not equals to UPDATED_IMPONIBILE
        defaultFattureAttivoShouldBeFound("imponibile.notEquals=" + UPDATED_IMPONIBILE);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByImponibileIsInShouldWork() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where imponibile in DEFAULT_IMPONIBILE or UPDATED_IMPONIBILE
        defaultFattureAttivoShouldBeFound("imponibile.in=" + DEFAULT_IMPONIBILE + "," + UPDATED_IMPONIBILE);

        // Get all the fattureAttivoList where imponibile equals to UPDATED_IMPONIBILE
        defaultFattureAttivoShouldNotBeFound("imponibile.in=" + UPDATED_IMPONIBILE);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByImponibileIsNullOrNotNull() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where imponibile is not null
        defaultFattureAttivoShouldBeFound("imponibile.specified=true");

        // Get all the fattureAttivoList where imponibile is null
        defaultFattureAttivoShouldNotBeFound("imponibile.specified=false");
    }

    @Test
    @Transactional
    void getAllFattureAttivosByImponibileIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where imponibile is greater than or equal to DEFAULT_IMPONIBILE
        defaultFattureAttivoShouldBeFound("imponibile.greaterThanOrEqual=" + DEFAULT_IMPONIBILE);

        // Get all the fattureAttivoList where imponibile is greater than or equal to UPDATED_IMPONIBILE
        defaultFattureAttivoShouldNotBeFound("imponibile.greaterThanOrEqual=" + UPDATED_IMPONIBILE);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByImponibileIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where imponibile is less than or equal to DEFAULT_IMPONIBILE
        defaultFattureAttivoShouldBeFound("imponibile.lessThanOrEqual=" + DEFAULT_IMPONIBILE);

        // Get all the fattureAttivoList where imponibile is less than or equal to SMALLER_IMPONIBILE
        defaultFattureAttivoShouldNotBeFound("imponibile.lessThanOrEqual=" + SMALLER_IMPONIBILE);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByImponibileIsLessThanSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where imponibile is less than DEFAULT_IMPONIBILE
        defaultFattureAttivoShouldNotBeFound("imponibile.lessThan=" + DEFAULT_IMPONIBILE);

        // Get all the fattureAttivoList where imponibile is less than UPDATED_IMPONIBILE
        defaultFattureAttivoShouldBeFound("imponibile.lessThan=" + UPDATED_IMPONIBILE);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByImponibileIsGreaterThanSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where imponibile is greater than DEFAULT_IMPONIBILE
        defaultFattureAttivoShouldNotBeFound("imponibile.greaterThan=" + DEFAULT_IMPONIBILE);

        // Get all the fattureAttivoList where imponibile is greater than SMALLER_IMPONIBILE
        defaultFattureAttivoShouldBeFound("imponibile.greaterThan=" + SMALLER_IMPONIBILE);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByIvaIsEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where iva equals to DEFAULT_IVA
        defaultFattureAttivoShouldBeFound("iva.equals=" + DEFAULT_IVA);

        // Get all the fattureAttivoList where iva equals to UPDATED_IVA
        defaultFattureAttivoShouldNotBeFound("iva.equals=" + UPDATED_IVA);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByIvaIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where iva not equals to DEFAULT_IVA
        defaultFattureAttivoShouldNotBeFound("iva.notEquals=" + DEFAULT_IVA);

        // Get all the fattureAttivoList where iva not equals to UPDATED_IVA
        defaultFattureAttivoShouldBeFound("iva.notEquals=" + UPDATED_IVA);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByIvaIsInShouldWork() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where iva in DEFAULT_IVA or UPDATED_IVA
        defaultFattureAttivoShouldBeFound("iva.in=" + DEFAULT_IVA + "," + UPDATED_IVA);

        // Get all the fattureAttivoList where iva equals to UPDATED_IVA
        defaultFattureAttivoShouldNotBeFound("iva.in=" + UPDATED_IVA);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByIvaIsNullOrNotNull() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where iva is not null
        defaultFattureAttivoShouldBeFound("iva.specified=true");

        // Get all the fattureAttivoList where iva is null
        defaultFattureAttivoShouldNotBeFound("iva.specified=false");
    }

    @Test
    @Transactional
    void getAllFattureAttivosByIvaIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where iva is greater than or equal to DEFAULT_IVA
        defaultFattureAttivoShouldBeFound("iva.greaterThanOrEqual=" + DEFAULT_IVA);

        // Get all the fattureAttivoList where iva is greater than or equal to UPDATED_IVA
        defaultFattureAttivoShouldNotBeFound("iva.greaterThanOrEqual=" + UPDATED_IVA);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByIvaIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where iva is less than or equal to DEFAULT_IVA
        defaultFattureAttivoShouldBeFound("iva.lessThanOrEqual=" + DEFAULT_IVA);

        // Get all the fattureAttivoList where iva is less than or equal to SMALLER_IVA
        defaultFattureAttivoShouldNotBeFound("iva.lessThanOrEqual=" + SMALLER_IVA);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByIvaIsLessThanSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where iva is less than DEFAULT_IVA
        defaultFattureAttivoShouldNotBeFound("iva.lessThan=" + DEFAULT_IVA);

        // Get all the fattureAttivoList where iva is less than UPDATED_IVA
        defaultFattureAttivoShouldBeFound("iva.lessThan=" + UPDATED_IVA);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByIvaIsGreaterThanSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where iva is greater than DEFAULT_IVA
        defaultFattureAttivoShouldNotBeFound("iva.greaterThan=" + DEFAULT_IVA);

        // Get all the fattureAttivoList where iva is greater than SMALLER_IVA
        defaultFattureAttivoShouldBeFound("iva.greaterThan=" + SMALLER_IVA);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByStatoIsEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where stato equals to DEFAULT_STATO
        defaultFattureAttivoShouldBeFound("stato.equals=" + DEFAULT_STATO);

        // Get all the fattureAttivoList where stato equals to UPDATED_STATO
        defaultFattureAttivoShouldNotBeFound("stato.equals=" + UPDATED_STATO);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByStatoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where stato not equals to DEFAULT_STATO
        defaultFattureAttivoShouldNotBeFound("stato.notEquals=" + DEFAULT_STATO);

        // Get all the fattureAttivoList where stato not equals to UPDATED_STATO
        defaultFattureAttivoShouldBeFound("stato.notEquals=" + UPDATED_STATO);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByStatoIsInShouldWork() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where stato in DEFAULT_STATO or UPDATED_STATO
        defaultFattureAttivoShouldBeFound("stato.in=" + DEFAULT_STATO + "," + UPDATED_STATO);

        // Get all the fattureAttivoList where stato equals to UPDATED_STATO
        defaultFattureAttivoShouldNotBeFound("stato.in=" + UPDATED_STATO);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByStatoIsNullOrNotNull() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where stato is not null
        defaultFattureAttivoShouldBeFound("stato.specified=true");

        // Get all the fattureAttivoList where stato is null
        defaultFattureAttivoShouldNotBeFound("stato.specified=false");
    }

    @Test
    @Transactional
    void getAllFattureAttivosByDataEmissioneIsEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where dataEmissione equals to DEFAULT_DATA_EMISSIONE
        defaultFattureAttivoShouldBeFound("dataEmissione.equals=" + DEFAULT_DATA_EMISSIONE);

        // Get all the fattureAttivoList where dataEmissione equals to UPDATED_DATA_EMISSIONE
        defaultFattureAttivoShouldNotBeFound("dataEmissione.equals=" + UPDATED_DATA_EMISSIONE);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByDataEmissioneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where dataEmissione not equals to DEFAULT_DATA_EMISSIONE
        defaultFattureAttivoShouldNotBeFound("dataEmissione.notEquals=" + DEFAULT_DATA_EMISSIONE);

        // Get all the fattureAttivoList where dataEmissione not equals to UPDATED_DATA_EMISSIONE
        defaultFattureAttivoShouldBeFound("dataEmissione.notEquals=" + UPDATED_DATA_EMISSIONE);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByDataEmissioneIsInShouldWork() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where dataEmissione in DEFAULT_DATA_EMISSIONE or UPDATED_DATA_EMISSIONE
        defaultFattureAttivoShouldBeFound("dataEmissione.in=" + DEFAULT_DATA_EMISSIONE + "," + UPDATED_DATA_EMISSIONE);

        // Get all the fattureAttivoList where dataEmissione equals to UPDATED_DATA_EMISSIONE
        defaultFattureAttivoShouldNotBeFound("dataEmissione.in=" + UPDATED_DATA_EMISSIONE);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByDataEmissioneIsNullOrNotNull() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where dataEmissione is not null
        defaultFattureAttivoShouldBeFound("dataEmissione.specified=true");

        // Get all the fattureAttivoList where dataEmissione is null
        defaultFattureAttivoShouldNotBeFound("dataEmissione.specified=false");
    }

    @Test
    @Transactional
    void getAllFattureAttivosByDataPagamentoIsEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where dataPagamento equals to DEFAULT_DATA_PAGAMENTO
        defaultFattureAttivoShouldBeFound("dataPagamento.equals=" + DEFAULT_DATA_PAGAMENTO);

        // Get all the fattureAttivoList where dataPagamento equals to UPDATED_DATA_PAGAMENTO
        defaultFattureAttivoShouldNotBeFound("dataPagamento.equals=" + UPDATED_DATA_PAGAMENTO);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByDataPagamentoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where dataPagamento not equals to DEFAULT_DATA_PAGAMENTO
        defaultFattureAttivoShouldNotBeFound("dataPagamento.notEquals=" + DEFAULT_DATA_PAGAMENTO);

        // Get all the fattureAttivoList where dataPagamento not equals to UPDATED_DATA_PAGAMENTO
        defaultFattureAttivoShouldBeFound("dataPagamento.notEquals=" + UPDATED_DATA_PAGAMENTO);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByDataPagamentoIsInShouldWork() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where dataPagamento in DEFAULT_DATA_PAGAMENTO or UPDATED_DATA_PAGAMENTO
        defaultFattureAttivoShouldBeFound("dataPagamento.in=" + DEFAULT_DATA_PAGAMENTO + "," + UPDATED_DATA_PAGAMENTO);

        // Get all the fattureAttivoList where dataPagamento equals to UPDATED_DATA_PAGAMENTO
        defaultFattureAttivoShouldNotBeFound("dataPagamento.in=" + UPDATED_DATA_PAGAMENTO);
    }

    @Test
    @Transactional
    void getAllFattureAttivosByDataPagamentoIsNullOrNotNull() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);

        // Get all the fattureAttivoList where dataPagamento is not null
        defaultFattureAttivoShouldBeFound("dataPagamento.specified=true");

        // Get all the fattureAttivoList where dataPagamento is null
        defaultFattureAttivoShouldNotBeFound("dataPagamento.specified=false");
    }

    @Test
    @Transactional
    void getAllFattureAttivosByClienteIsEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);
        Cliente cliente;
        if (TestUtil.findAll(em, Cliente.class).isEmpty()) {
            cliente = ClienteResourceIT.createEntity(em);
            em.persist(cliente);
            em.flush();
        } else {
            cliente = TestUtil.findAll(em, Cliente.class).get(0);
        }
        em.persist(cliente);
        em.flush();
        fattureAttivo.setCliente(cliente);
        fattureAttivoRepository.saveAndFlush(fattureAttivo);
        Long clienteId = cliente.getId();

        // Get all the fattureAttivoList where cliente equals to clienteId
        defaultFattureAttivoShouldBeFound("clienteId.equals=" + clienteId);

        // Get all the fattureAttivoList where cliente equals to (clienteId + 1)
        defaultFattureAttivoShouldNotBeFound("clienteId.equals=" + (clienteId + 1));
    }

    @Test
    @Transactional
    void getAllFattureAttivosByCantiereIsEqualToSomething() throws Exception {
        // Initialize the database
        fattureAttivoRepository.saveAndFlush(fattureAttivo);
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
        fattureAttivo.setCantiere(cantiere);
        fattureAttivoRepository.saveAndFlush(fattureAttivo);
        Long cantiereId = cantiere.getId();

        // Get all the fattureAttivoList where cantiere equals to cantiereId
        defaultFattureAttivoShouldBeFound("cantiereId.equals=" + cantiereId);

        // Get all the fattureAttivoList where cantiere equals to (cantiereId + 1)
        defaultFattureAttivoShouldNotBeFound("cantiereId.equals=" + (cantiereId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFattureAttivoShouldBeFound(String filter) throws Exception {
        restFattureAttivoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
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

        // Check, that the count call also returns 1
        restFattureAttivoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFattureAttivoShouldNotBeFound(String filter) throws Exception {
        restFattureAttivoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFattureAttivoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
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
