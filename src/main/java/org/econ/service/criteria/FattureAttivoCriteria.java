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
 * Criteria class for the {@link org.econ.domain.FattureAttivo} entity. This class is used
 * in {@link org.econ.web.rest.FattureAttivoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /fatture-attivos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class FattureAttivoCriteria implements Serializable, Criteria {

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

    private StringFilter nomeCliente;

    private LongFilter imponibile;

    private LongFilter iva;

    private StatoFilter stato;

    private InstantFilter dataEmissione;

    private InstantFilter dataPagamento;

    private LongFilter clienteId;

    private LongFilter cantiereId;

    private Boolean distinct;

    public FattureAttivoCriteria() {}

    public FattureAttivoCriteria(FattureAttivoCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.numeroFattura = other.numeroFattura == null ? null : other.numeroFattura.copy();
        this.ragSociale = other.ragSociale == null ? null : other.ragSociale.copy();
        this.nomeCliente = other.nomeCliente == null ? null : other.nomeCliente.copy();
        this.imponibile = other.imponibile == null ? null : other.imponibile.copy();
        this.iva = other.iva == null ? null : other.iva.copy();
        this.stato = other.stato == null ? null : other.stato.copy();
        this.dataEmissione = other.dataEmissione == null ? null : other.dataEmissione.copy();
        this.dataPagamento = other.dataPagamento == null ? null : other.dataPagamento.copy();
        this.clienteId = other.clienteId == null ? null : other.clienteId.copy();
        this.cantiereId = other.cantiereId == null ? null : other.cantiereId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public FattureAttivoCriteria copy() {
        return new FattureAttivoCriteria(this);
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

    public StringFilter getNomeCliente() {
        return nomeCliente;
    }

    public StringFilter nomeCliente() {
        if (nomeCliente == null) {
            nomeCliente = new StringFilter();
        }
        return nomeCliente;
    }

    public void setNomeCliente(StringFilter nomeCliente) {
        this.nomeCliente = nomeCliente;
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

    public LongFilter getClienteId() {
        return clienteId;
    }

    public LongFilter clienteId() {
        if (clienteId == null) {
            clienteId = new LongFilter();
        }
        return clienteId;
    }

    public void setClienteId(LongFilter clienteId) {
        this.clienteId = clienteId;
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
        final FattureAttivoCriteria that = (FattureAttivoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(numeroFattura, that.numeroFattura) &&
            Objects.equals(ragSociale, that.ragSociale) &&
            Objects.equals(nomeCliente, that.nomeCliente) &&
            Objects.equals(imponibile, that.imponibile) &&
            Objects.equals(iva, that.iva) &&
            Objects.equals(stato, that.stato) &&
            Objects.equals(dataEmissione, that.dataEmissione) &&
            Objects.equals(dataPagamento, that.dataPagamento) &&
            Objects.equals(clienteId, that.clienteId) &&
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
            nomeCliente,
            imponibile,
            iva,
            stato,
            dataEmissione,
            dataPagamento,
            clienteId,
            cantiereId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FattureAttivoCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (numeroFattura != null ? "numeroFattura=" + numeroFattura + ", " : "") +
            (ragSociale != null ? "ragSociale=" + ragSociale + ", " : "") +
            (nomeCliente != null ? "nomeCliente=" + nomeCliente + ", " : "") +
            (imponibile != null ? "imponibile=" + imponibile + ", " : "") +
            (iva != null ? "iva=" + iva + ", " : "") +
            (stato != null ? "stato=" + stato + ", " : "") +
            (dataEmissione != null ? "dataEmissione=" + dataEmissione + ", " : "") +
            (dataPagamento != null ? "dataPagamento=" + dataPagamento + ", " : "") +
            (clienteId != null ? "clienteId=" + clienteId + ", " : "") +
            (cantiereId != null ? "cantiereId=" + cantiereId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
