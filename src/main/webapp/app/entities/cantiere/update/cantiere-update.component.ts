import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ICantiere, Cantiere } from '../cantiere.model';
import { CantiereService } from '../service/cantiere.service';

@Component({
  selector: 'jhi-cantiere-update',
  templateUrl: './cantiere-update.component.html',
})
export class CantiereUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    nomeCantiere: [null, [Validators.required]],
    indirizzo: [],
  });

  constructor(protected cantiereService: CantiereService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cantiere }) => {
      this.updateForm(cantiere);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cantiere = this.createFromForm();
    if (cantiere.id !== undefined) {
      this.subscribeToSaveResponse(this.cantiereService.update(cantiere));
    } else {
      this.subscribeToSaveResponse(this.cantiereService.create(cantiere));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICantiere>>): void {
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

  protected updateForm(cantiere: ICantiere): void {
    this.editForm.patchValue({
      id: cantiere.id,
      nomeCantiere: cantiere.nomeCantiere,
      indirizzo: cantiere.indirizzo,
    });
  }

  protected createFromForm(): ICantiere {
    return {
      ...new Cantiere(),
      id: this.editForm.get(['id'])!.value,
      nomeCantiere: this.editForm.get(['nomeCantiere'])!.value,
      indirizzo: this.editForm.get(['indirizzo'])!.value,
    };
  }
}
