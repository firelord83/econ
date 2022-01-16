package org.econ.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.econ.domain.enumeration.Stato;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link org.econ.domain.FatturePassivo} entity. This class is used
 * in {@link org.econ.web.rest.FatturePassivoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /fatture-passivos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class FatturePassivoCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Stato
     */
    public static class StatoFilter extends Filter<Stato> {

        public StatoFilter() {}

        public StatoFilter(StatoFilter filter) {
            super(filter);
        }

        @Override
        public StatoFilter copy() {
            return new StatoFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter numeroFattura;

    private StringFilter ragSociale;

    private StringFilter nomeFornitore;

    private LongFilter imponibile;

    private LongFilter iva;

    private StatoFilter stato;

    private InstantFilter dataEmissione;

    private InstantFilter dataPagamento;

    private LongFilter fornitoreId;

    private LongFilter cantiereId;

    private Boolean distinct;

    public FatturePassivoCriteria() {}

    public FatturePassivoCriteria(FatturePassivoCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.numeroFattura = other.numeroFattura == null ? null : other.numeroFattura.copy();
        this.ragSociale = other.ragSociale == null ? null : other.ragSociale.copy();
        this.nomeFornitore = other.nomeFornitore == null ? null : other.nomeFornitore.copy();
        this.imponibile = other.imponibile == null ? null : other.imponibile.copy();
        this.iva = other.iva == null ? null : other.iva.copy();
        this.stato = other.stato == null ? null : other.stato.copy();
        this.dataEmissione = other.dataEmissione == null ? null : other.dataEmissione.copy();
        this.dataPagamento = other.dataPagamento == null ? null : other.dataPagamento.copy();
        this.fornitoreId = other.fornitoreId == null ? null : other.fornitoreId.copy();
        this.cantiereId = other.cantiereId == null ? null : other.cantiereId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public FatturePassivoCriteria copy() {
        return new FatturePassivoCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getNumeroFattura() {
        return numeroFattura;
    }

    public LongFilter numeroFattura() {
        if (numeroFattura == null) {
            numeroFattura = new LongFilter();
        }
        return numeroFattura;
    }

    public void setNumeroFattura(LongFilter numeroFattura) {
        this.numeroFattura = numeroFattura;
    }

    public StringFilter getRagSociale() {
        return ragSociale;
    }

    public StringFilter ragSociale() {
        if (ragSociale == null) {
            ragSociale = new StringFilter();
        }
        return ragSociale;
    }

    public void setRagSociale(StringFilter ragSociale) {
        this.ragSociale = ragSociale;
    }

    public StringFilter getNomeFornitore() {
        return nomeFornitore;
    }

    public StringFilter nomeFornitore() {
        if (nomeFornitore == null) {
            nomeFornitore = new StringFilter();
        }
        return nomeFornitore;
    }

    public void setNomeFornitore(StringFilter nomeFornitore) {
        this.nomeFornitore = nomeFornitore;
    }

    public LongFilter getImponibile() {
        return imponibile;
    }

    public LongFilter imponibile() {
        if (imponibile == null) {
            imponibile = new LongFilter();
        }
        return imponibile;
    }

    public void setImponibile(LongFilter imponibile) {
        this.imponibile = imponibile;
    }

    public LongFilter getIva() {
        return iva;
    }

    public LongFilter iva() {
        if (iva == null) {
            iva = new LongFilter();
        }
        return iva;
    }

    public void setIva(LongFilter iva) {
        this.iva = iva;
    }

    public StatoFilter getStato() {
        return stato;
    }

    public StatoFilter stato() {
        if (stato == null) {
            stato = new StatoFilter();
        }
        return stato;
    }

    public void setStato(StatoFilter stato) {
        this.stato = stato;
    }

    public InstantFilter getDataEmissione() {
        return dataEmissione;
    }

    public InstantFilter dataEmissione() {
        if (dataEmissione == null) {
            dataEmissione = new InstantFilter();
        }
        return dataEmissione;
    }

    public void setDataEmissione(InstantFilter dataEmissione) {
        this.dataEmissione = dataEmissione;
    }

    public InstantFilter getDataPagamento() {
        return dataPagamento;
    }

    public InstantFilter dataPagamento() {
        if (dataPagamento == null) {
            dataPagamento = new InstantFilter();
        }
        return dataPagamento;
    }

    public void setDataPagamento(InstantFilter dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public LongFilter getFornitoreId() {
        return fornitoreId;
    }

    public LongFilter fornitoreId() {
        if (fornitoreId == null) {
            fornitoreId = new LongFilter();
        }
        return fornitoreId;
    }

    public void setFornitoreId(LongFilter fornitoreId) {
        this.fornitoreId = fornitoreId;
    }

    public LongFilter getCantiereId() {
        return cantiereId;
    }

    public LongFilter cantiereId() {
        if (cantiereId == null) {
            cantiereId = new LongFilter();
        }
        return cantiereId;
    }

    public void setCantiereId(LongFilter cantiereId) {
        this.cantiereId = cantiereId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FatturePassivoCriteria that = (FatturePassivoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(numeroFattura, that.numeroFattura) &&
            Objects.equals(ragSociale, that.ragSociale) &&
            Objects.equals(nomeFornitore, that.nomeFornitore) &&
            Objects.equals(imponibile, that.imponibile) &&
            Objects.equals(iva, that.iva) &&
            Objects.equals(stato, that.stato) &&
            Objects.equals(dataEmissione, that.dataEmissione) &&
            Objects.equals(dataPagamento, that.dataPagamento) &&
            Objects.equals(fornitoreId, that.fornitoreId) &&
            Objects.equals(cantiereId, that.cantiereId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            numeroFattura,
            ragSociale,
            nomeFornitore,
            imponibile,
            iva,
            stato,
            dataEmissione,
            dataPagamento,
            fornitoreId,
            cantiereId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FatturePassivoCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (numeroFattura != null ? "numeroFattura=" + numeroFattura + ", " : "") +
            (ragSociale != null ? "ragSociale=" + ragSociale + ", " : "") +
            (nomeFornitore != null ? "nomeFornitore=" + nomeFornitore + ", " : "") +
            (imponibile != null ? "imponibile=" + imponibile + ", " : "") +
            (iva != null ? "iva=" + iva + ", " : "") +
            (stato != null ? "stato=" + stato + ", " : "") +
            (dataEmissione != null ? "dataEmissione=" + dataEmissione + ", " : "") +
            (dataPagamento != null ? "dataPagamento=" + dataPagamento + ", " : "") +
            (fornitoreId != null ? "fornitoreId=" + fornitoreId + ", " : "") +
            (cantiereId != null ? "cantiereId=" + cantiereId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
