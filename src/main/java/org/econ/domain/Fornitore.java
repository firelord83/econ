package org.econ.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Fornitore.
 */
@Entity
@Table(name = "fornitore")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "fornitore")
public class Fornitore implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nome_fornitore", nullable = false)
    private String nomeFornitore;

    @Column(name = "indirizzo")
    private String indirizzo;

    @Column(name = "tipo")
    private String tipo;

    @OneToMany(mappedBy = "fornitore")
    @JsonIgnoreProperties(value = { "fornitore", "cantiere" }, allowSetters = true)
    private Set<FatturePassivo> fatturas = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Fornitore id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeFornitore() {
        return this.nomeFornitore;
    }

    public Fornitore nomeFornitore(String nomeFornitore) {
        this.setNomeFornitore(nomeFornitore);
        return this;
    }

    public void setNomeFornitore(String nomeFornitore) {
        this.nomeFornitore = nomeFornitore;
    }

    public String getIndirizzo() {
        return this.indirizzo;
    }

    public Fornitore indirizzo(String indirizzo) {
        this.setIndirizzo(indirizzo);
        return this;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getTipo() {
        return this.tipo;
    }

    public Fornitore tipo(String tipo) {
        this.setTipo(tipo);
        return this;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Set<FatturePassivo> getFatturas() {
        return this.fatturas;
    }

    public void setFatturas(Set<FatturePassivo> fatturePassivos) {
        if (this.fatturas != null) {
            this.fatturas.forEach(i -> i.setFornitore(null));
        }
        if (fatturePassivos != null) {
            fatturePassivos.forEach(i -> i.setFornitore(this));
        }
        this.fatturas = fatturePassivos;
    }

    public Fornitore fatturas(Set<FatturePassivo> fatturePassivos) {
        this.setFatturas(fatturePassivos);
        return this;
    }

    public Fornitore addFattura(FatturePassivo fatturePassivo) {
        this.fatturas.add(fatturePassivo);
        fatturePassivo.setFornitore(this);
        return this;
    }

    public Fornitore removeFattura(FatturePassivo fatturePassivo) {
        this.fatturas.remove(fatturePassivo);
        fatturePassivo.setFornitore(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Fornitore)) {
            return false;
        }
        return id != null && id.equals(((Fornitore) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Fornitore{" +
            "id=" + getId() +
            ", nomeFornitore='" + getNomeFornitore() + "'" +
            ", indirizzo='" + getIndirizzo() + "'" +
            ", tipo='" + getTipo() + "'" +
            "}";
    }
}
