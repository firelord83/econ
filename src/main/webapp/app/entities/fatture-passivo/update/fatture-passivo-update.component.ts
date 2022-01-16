import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IFatturePassivo, FatturePassivo } from '../fatture-passivo.model';
import { FatturePassivoService } from '../service/fatture-passivo.service';
import { IFornitore } from 'app/entities/fornitore/fornitore.model';
import { FornitoreService } from 'app/entities/fornitore/service/fornitore.service';
import { ICantiere } from 'app/entities/cantiere/cantiere.model';
import { CantiereService } from 'app/entities/cantiere/service/cantiere.service';
import { Stato } from 'app/entities/enumerations/stato.model';

@Component({
  selector: 'jhi-fatture-passivo-update',
  templateUrl: './fatture-passivo-update.component.html',
})
export class FatturePassivoUpdateComponent implements OnInit {
  isSaving = false;
  statoValues = Object.keys(Stato);

  fornitoresSharedCollection: IFornitore[] = [];
  cantieresSharedCollection: ICantiere[] = [];

  editForm = this.fb.group({
    id: [],
    numeroFattura: [null, [Validators.required]],
    ragSociale: [],
    nomeFornitore: [],
    imponibile: [],
    iva: [],
    stato: [],
    dataEmissione: [],
    dataPagamento: [],
    fornitore: [],
    cantiere: [],
  });

  constructor(
    protected fatturePassivoService: FatturePassivoService,
    protected fornitoreService: FornitoreService,
    protected cantiereService: CantiereService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ fatturePassivo }) => {
      if (fatturePassivo.id === undefined) {
        const today = dayjs().startOf('day');
        fatturePassivo.dataEmissione = today;
        fatturePassivo.dataPagamento = today;
      }

      this.updateForm(fatturePassivo);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const fatturePassivo = this.createFromForm();
    if (fatturePassivo.id !== undefined) {
      this.subscribeToSaveResponse(this.fatturePassivoService.update(fatturePassivo));
    } else {
      this.subscribeToSaveResponse(this.fatturePassivoService.create(fatturePassivo));
    }
  }

  trackFornitoreById(index: number, item: IFornitore): number {
    return item.id!;
  }

  trackCantiereById(index: number, item: ICantiere): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFatturePassivo>>): void {
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

  protected updateForm(fatturePassivo: IFatturePassivo): void {
    this.editForm.patchValue({
      id: fatturePassivo.id,
      numeroFattura: fatturePassivo.numeroFattura,
      ragSociale: fatturePassivo.ragSociale,
      nomeFornitore: fatturePassivo.nomeFornitore,
      imponibile: fatturePassivo.imponibile,
      iva: fatturePassivo.iva,
      stato: fatturePassivo.stato,
      dataEmissione: fatturePassivo.dataEmissione ? fatturePassivo.dataEmissione.format(DATE_TIME_FORMAT) : null,
      dataPagamento: fatturePassivo.dataPagamento ? fatturePassivo.dataPagamento.format(DATE_TIME_FORMAT) : null,
      fornitore: fatturePassivo.fornitore,
      cantiere: fatturePassivo.cantiere,
    });

    this.fornitoresSharedCollection = this.fornitoreService.addFornitoreToCollectionIfMissing(
      this.fornitoresSharedCollection,
      fatturePassivo.fornitore
    );
    this.cantieresSharedCollection = this.cantiereService.addCantiereToCollectionIfMissing(
      this.cantieresSharedCollection,
      fatturePassivo.cantiere
    );
  }

  protected loadRelationshipsOptions(): void {
    this.fornitoreService
      .query()
      .pipe(map((res: HttpResponse<IFornitore[]>) => res.body ?? []))
      .pipe(
        map((fornitores: IFornitore[]) =>
          this.fornitoreService.addFornitoreToCollectionIfMissing(fornitores, this.editForm.get('fornitore')!.value)
        )
      )
      .subscribe((fornitores: IFornitore[]) => (this.fornitoresSharedCollection = fornitores));

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

  protected createFromForm(): IFatturePassivo {
    return {
      ...new FatturePassivo(),
      id: this.editForm.get(['id'])!.value,
      numeroFattura: this.editForm.get(['numeroFattura'])!.value,
      ragSociale: this.editForm.get(['ragSociale'])!.value,
      nomeFornitore: this.editForm.get(['nomeFornitore'])!.value,
      imponibile: this.editForm.get(['imponibile'])!.value,
      iva: this.editForm.get(['iva'])!.value,
      stato: this.editForm.get(['stato'])!.value,
      dataEmissione: this.editForm.get(['dataEmissione'])!.value
        ? dayjs(this.editForm.get(['dataEmissione'])!.value, DATE_TIME_FORMAT)
        : undefined,
      dataPagamento: this.editForm.get(['dataPagamento'])!.value
        ? dayjs(this.editForm.get(['dataPagamento'])!.value, DATE_TIME_FORMAT)
        : undefined,
      fornitore: this.editForm.get(['fornitore'])!.value,
      cantiere: this.editForm.get(['cantiere'])!.value,
    };
  }
}
