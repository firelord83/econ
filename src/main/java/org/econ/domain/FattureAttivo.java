package org.econ.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.econ.domain.enumeration.Stato;

/**
 * A FattureAttivo.
 */
@Entity
@Table(name = "fatture_attivo")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "fattureattivo")
public class FattureAttivo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "numero_fattura", nullable = false)
    private Long numeroFattura;

    @Column(name = "rag_sociale")
    private String ragSociale;

    @Column(name = "nome_cliente")
    private String nomeCliente;

    @Column(name = "imponibile")
    private Long imponibile;

    @Column(name = "iva")
    private Long iva;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato")
    private Stato stato;

    @Column(name = "data_emissione")
    private Instant dataEmissione;

    @Column(name = "data_pagamento")
    private Instant dataPagamento;

    @ManyToOne
    @JsonIgnoreProperties(value = { "fatturas" }, allowSetters = true)
    private Cliente cliente;

    @ManyToOne
    @JsonIgnoreProperties(value = { "afatturas", "pfatturas" }, allowSetters = true)
    private Cantiere cantiere;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FattureAttivo id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumeroFattura() {
        return this.numeroFattura;
    }

    public FattureAttivo numeroFattura(Long numeroFattura) {
        this.setNumeroFattura(numeroFattura);
        return this;
    }

    public void setNumeroFattura(Long numeroFattura) {
        this.numeroFattura = numeroFattura;
    }

    public String getRagSociale() {
        return this.ragSociale;
    }

    public FattureAttivo ragSociale(String ragSociale) {
        this.setRagSociale(ragSociale);
        return this;
    }

    public void setRagSociale(String ragSociale) {
        this.ragSociale = ragSociale;
    }

    public String getNomeCliente() {
        return this.nomeCliente;
    }

    public FattureAttivo nomeCliente(String nomeCliente) {
        this.setNomeCliente(nomeCliente);
        return this;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public Long getImponibile() {
        return this.imponibile;
    }

    public FattureAttivo imponibile(Long imponibile) {
        this.setImponibile(imponibile);
        return this;
    }

    public void setImponibile(Long imponibile) {
        this.imponibile = imponibile;
    }

    public Long getIva() {
        return this.iva;
    }

    public FattureAttivo iva(Long iva) {
        this.setIva(iva);
        return this;
    }

    public void setIva(Long iva) {
        this.iva = iva;
    }

    public Stato getStato() {
        return this.stato;
    }

    public FattureAttivo stato(Stato stato) {
        this.setStato(stato);
        return this;
    }

    public void setStato(Stato stato) {
        this.stato = stato;
    }

    public Instant getDataEmissione() {
        return this.dataEmissione;
    }

    public FattureAttivo dataEmissione(Instant dataEmissione) {
        this.setDataEmissione(dataEmissione);
        return this;
    }

    public void setDataEmissione(Instant dataEmissione) {
        this.dataEmissione = dataEmissione;
    }

    public Instant getDataPagamento() {
        return this.dataPagamento;
    }

    public FattureAttivo dataPagamento(Instant dataPagamento) {
        this.setDataPagamento(dataPagamento);
        return this;
    }

    public void setDataPagamento(Instant dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public Cliente getCliente() {
        return this.cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public FattureAttivo cliente(Cliente cliente) {
        this.setCliente(cliente);
        return this;
    }

    public Cantiere getCantiere() {
        return this.cantiere;
    }

    public void setCantiere(Cantiere cantiere) {
        this.cantiere = cantiere;
    }

    public FattureAttivo cantiere(Cantiere cantiere) {
        this.setCantiere(cantiere);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FattureAttivo)) {
            return false;
        }
        return id != null && id.equals(((FattureAttivo) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FattureAttivo{" +
            "id=" + getId() +
            ", numeroFattura=" + getNumeroFattura() +
            ", ragSociale='" + getRagSociale() + "'" +
            ", nomeCliente='" + getNomeCliente() + "'" +
            ", imponibile=" + getImponibile() +
            ", iva=" + getIva() +
            ", stato='" + getStato() + "'" +
            ", dataEmissione='" + getDataEmissione() + "'" +
            ", dataPagamento='" + getDataPagamento() + "'" +
            "}";
    }
}
