import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IFornitore, Fornitore } from '../fornitore.model';
import { FornitoreService } from '../service/fornitore.service';

@Component({
  selector: 'jhi-fornitore-update',
  templateUrl: './fornitore-update.component.html',
})
export class FornitoreUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    nomeFornitore: [null, [Validators.required]],
    indirizzo: [],
    tipo: [],
  });

  constructor(protected fornitoreService: FornitoreService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ fornitore }) => {
      this.updateForm(fornitore);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const fornitore = this.createFromForm();
    if (fornitore.id !== undefined) {
      this.subscribeToSaveResponse(this.fornitoreService.update(fornitore));
    } else {
      this.subscribeToSaveResponse(this.fornitoreService.create(fornitore));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFornitore>>): void {
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

  protected updateForm(fornitore: IFornitore): void {
    this.editForm.patchValue({
      id: fornitore.id,
      nomeFornitore: fornitore.nomeFornitore,
      indirizzo: fornitore.indirizzo,
      tipo: fornitore.tipo,
    });
  }

  protected createFromForm(): IFornitore {
    return {
      ...new Fornitore(),
      id: this.editForm.get(['id'])!.value,
      nomeFornitore: this.editForm.get(['nomeFornitore'])!.value,
      indirizzo: this.editForm.get(['indirizzo'])!.value,
      tipo: this.editForm.get(['tipo'])!.value,
    };
  }
}
