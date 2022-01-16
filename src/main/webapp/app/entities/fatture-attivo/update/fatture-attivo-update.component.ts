import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IFattureAttivo, FattureAttivo } from '../fatture-attivo.model';
import { FattureAttivoService } from '../service/fatture-attivo.service';
import { ICliente } from 'app/entities/cliente/cliente.model';
import { ClienteService } from 'app/entities/cliente/service/cliente.service';
import { ICantiere } from 'app/entities/cantiere/cantiere.model';
import { CantiereService } from 'app/entities/cantiere/service/cantiere.service';
import { Stato } from 'app/entities/enumerations/stato.model';

@Component({
  selector: 'jhi-fatture-attivo-update',
  templateUrl: './fatture-attivo-update.component.html',
})
export class FattureAttivoUpdateComponent implements OnInit {
  isSaving = false;
  statoValues = Object.keys(Stato);

  clientesSharedCollection: ICliente[] = [];
  cantieresSharedCollection: ICantiere[] = [];

  editForm = this.fb.group({
    id: [],
    numeroFattura: [null, [Validators.required]],
    ragSociale: [],
    nomeCliente: [],
    imponibile: [],
    iva: [],
    stato: [],
    dataEmissione: [],
    dataPagamento: [],
    cliente: [],
    cantiere: [],
  });

  constructor(
    protected fattureAttivoService: FattureAttivoService,
    protected clienteService: ClienteService,
    protected cantiereService: CantiereService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ fattureAttivo }) => {
      if (fattureAttivo.id === undefined) {
        const today = dayjs().startOf('day');
        fattureAttivo.dataEmissione = today;
        fattureAttivo.dataPagamento = today;
      }

      this.updateForm(fattureAttivo);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const fattureAttivo = this.createFromForm();
    if (fattureAttivo.id !== undefined) {
      this.subscribeToSaveResponse(this.fattureAttivoService.update(fattureAttivo));
    } else {
      this.subscribeToSaveResponse(this.fattureAttivoService.create(fattureAttivo));
    }
  }

  trackClienteById(index: number, item: ICliente): number {
    return item.id!;
  }

  trackCantiereById(index: number, item: ICantiere): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFattureAttivo>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(fattureAttivo: IFattureAttivo): void {
    this.editForm.patchValue({
      id: fattureAttivo.id,
      numeroFattura: fattureAttivo.numeroFattura,
      ragSociale: fattureAttivo.ragSociale,
      nomeCliente: fattureAttivo.nomeCliente,
      imponibile: fattureAttivo.imponibile,
      iva: fattureAttivo.iva,
      stato: fattureAttivo.stato,
      dataEmissione: fattureAttivo.dataEmissione ? fattureAttivo.dataEmissione.format(DATE_TIME_FORMAT) : null,
      dataPagamento: fattureAttivo.dataPagamento ? fattureAttivo.dataPagamento.format(DATE_TIME_FORMAT) : null,
      cliente: fattureAttivo.cliente,
      cantiere: fattureAttivo.cantiere,
    });

    this.clientesSharedCollection = this.clienteService.addClienteToCollectionIfMissing(
      this.clientesSharedCollection,
      fattureAttivo.cliente
    );
    this.cantieresSharedCollection = this.cantiereService.addCantiereToCollectionIfMissing(
      this.cantieresSharedCollection,
      fattureAttivo.cantiere
    );
  }

  protected loadRelationshipsOptions(): void {
    this.clienteService
      .query()
      .pipe(map((res: HttpResponse<ICliente[]>) => res.body ?? []))
      .pipe(
        map((clientes: ICliente[]) => this.clienteService.addClienteToCollectionIfMissing(clientes, this.editForm.get('cliente')!.value))
      )
      .subscribe((clientes: ICliente[]) => (this.clientesSharedCollection = clientes));

    this.cantiereService
      .query()
      .pipe(map((res: HttpResponse<ICantiere[]>) => res.body ?? []))
      .pipe(
        map((cantieres: ICantiere[]) =>
          this.cantiereService.addCantiereToCollectionIfMissing(cantieres, this.editForm.get('cantiere')!.value)
        )
      )
      .subscribe((cantieres: ICantiere[]) => (this.cantieresSharedCollection = cantieres));
  }

  protected createFromForm(): IFattureAttivo {
    return {
      ...new FattureAttivo(),
      id: this.editForm.get(['id'])!.value,
      numeroFattura: this.editForm.get(['numeroFattura'])!.value,
      ragSociale: this.editForm.get(['ragSociale'])!.value,
      nomeCliente: this.editForm.get(['nomeCliente'])!.value,
      imponibile: this.editForm.get(['imponibile'])!.value,
      iva: this.editForm.get(['iva'])!.value,
      stato: this.editForm.get(['stato'])!.value,
      dataEmissione: this.editForm.get(['dataEmissione'])!.value
        ? dayjs(this.editForm.get(['dataEmissione'])!.value, DATE_TIME_FORMAT)
        : undefined,
      dataPagamento: this.editForm.get(['dataPagamento'])!.value
        ? dayjs(this.editForm.get(['dataPagamento'])!.value, DATE_TIME_FORMAT)
        : undefined,
      cliente: this.editForm.get(['cliente'])!.value,
      cantiere: this.editForm.get(['cantiere'])!.value,
    };
  }
}
