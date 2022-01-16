package org.econ.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Cliente.
 */
@Entity
@Table(name = "cliente")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "cliente")
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nome_cliente", nullable = false)
    private String nomeCliente;

    @Column(name = "indirizzo")
    private String indirizzo;

    @Column(name = "tipo")
    private String tipo;

    @OneToMany(mappedBy = "cliente")
    @JsonIgnoreProperties(value = { "cliente", "cantiere" }, allowSetters = true)
    private Set<FattureAttivo> fatturas = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Cliente id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCliente() {
        return this.nomeCliente;
    }

    public Cliente nomeCliente(String nomeCliente) {
        this.setNomeCliente(nomeCliente);
        return this;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getIndirizzo() {
        return this.indirizzo;
    }

    public Cliente indirizzo(String indirizzo) {
        this.setIndirizzo(indirizzo);
        return this;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getTipo() {
        return this.tipo;
    }

    public Cliente tipo(String tipo) {
        this.setTipo(tipo);
        return this;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Set<FattureAttivo> getFatturas() {
        return this.fatturas;
    }

    public void setFatturas(Set<FattureAttivo> fattureAttivos) {
        if (this.fatturas != null) {
            this.fatturas.forEach(i -> i.setCliente(null));
        }
        if (fattureAttivos != null) {
            fattureAttivos.forEach(i -> i.setCliente(this));
        }
        this.fatturas = fattureAttivos;
    }

    public Cliente fatturas(Set<FattureAttivo> fattureAttivos) {
        this.setFatturas(fattureAttivos);
        return this;
    }

    public Cliente addFattura(FattureAttivo fattureAttivo) {
        this.fatturas.add(fattureAttivo);
        fattureAttivo.setCliente(this);
        return this;
    }

    public Cliente removeFattura(FattureAttivo fattureAttivo) {
        this.fatturas.remove(fattureAttivo);
        fattureAttivo.setCliente(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cliente)) {
            return false;
        }
        return id != null && id.equals(((Cliente) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cliente{" +
            "id=" + getId() +
            ", nomeCliente='" + getNomeCliente() + "'" +
            ", indirizzo='" + getIndirizzo() + "'" +
            ", tipo='" + getTipo() + "'" +
            "}";
    }
}
