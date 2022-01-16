package org.econ.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Cantiere.
 */
@Entity
@Table(name = "cantiere")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "cantiere")
public class Cantiere implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nome_cantiere", nullable = false)
    private String nomeCantiere;

    @Column(name = "indirizzo")
    private String indirizzo;

    @OneToMany(mappedBy = "cantiere")
    @JsonIgnoreProperties(value = { "cliente", "cantiere" }, allowSetters = true)
    private Set<FattureAttivo> afatturas = new HashSet<>();

    @OneToMany(mappedBy = "cantiere")
    @JsonIgnoreProperties(value = { "fornitore", "cantiere" }, allowSetters = true)
    private Set<FatturePassivo> pfatturas = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Cantiere id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCantiere() {
        return this.nomeCantiere;
    }

    public Cantiere nomeCantiere(String nomeCantiere) {
        this.setNomeCantiere(nomeCantiere);
        return this;
    }

    public void setNomeCantiere(String nomeCantiere) {
        this.nomeCantiere = nomeCantiere;
    }

    public String getIndirizzo() {
        return this.indirizzo;
    }

    public Cantiere indirizzo(String indirizzo) {
        this.setIndirizzo(indirizzo);
        return this;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public Set<FattureAttivo> getAfatturas() {
        return this.afatturas;
    }

    public void setAfatturas(Set<FattureAttivo> fattureAttivos) {
        if (this.afatturas != null) {
            this.afatturas.forEach(i -> i.setCantiere(null));
        }
        if (fattureAttivos != null) {
            fattureAttivos.forEach(i -> i.setCantiere(this));
        }
        this.afatturas = fattureAttivos;
    }

    public Cantiere afatturas(Set<FattureAttivo> fattureAttivos) {
        this.setAfatturas(fattureAttivos);
        return this;
    }

    public Cantiere addAfattura(FattureAttivo fattureAttivo) {
        this.afatturas.add(fattureAttivo);
        fattureAttivo.setCantiere(this);
        return this;
    }

    public Cantiere removeAfattura(FattureAttivo fattureAttivo) {
        this.afatturas.remove(fattureAttivo);
        fattureAttivo.setCantiere(null);
        return this;
    }

    public Set<FatturePassivo> getPfatturas() {
        return this.pfatturas;
    }

    public void setPfatturas(Set<FatturePassivo> fatturePassivos) {
        if (this.pfatturas != null) {
            this.pfatturas.forEach(i -> i.setCantiere(null));
        }
        if (fatturePassivos != null) {
            fatturePassivos.forEach(i -> i.setCantiere(this));
        }
        this.pfatturas = fatturePassivos;
    }

    public Cantiere pfatturas(Set<FatturePassivo> fatturePassivos) {
        this.setPfatturas(fatturePassivos);
        return this;
    }

    public Cantiere addPfattura(FatturePassivo fatturePassivo) {
        this.pfatturas.add(fatturePassivo);
        fatturePassivo.setCantiere(this);
        return this;
    }

    public Cantiere removePfattura(FatturePassivo fatturePassivo) {
        this.pfatturas.remove(fatturePassivo);
        fatturePassivo.setCantiere(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cantiere)) {
            return false;
        }
        return id != null && id.equals(((Cantiere) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cantiere{" +
            "id=" + getId() +
            ", nomeCantiere='" + getNomeCantiere() + "'" +
            ", indirizzo='" + getIndirizzo() + "'" +
            "}";
    }
}
