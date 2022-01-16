import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFatturePassivo } from '../fatture-passivo.model';

@Component({
  selector: 'jhi-fatture-passivo-detail',
  templateUrl: './fatture-passivo-detail.component.html',
})
export class FatturePassivoDetailComponent implements OnInit {
  fatturePassivo: IFatturePassivo | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ fatturePassivo }) => {
      this.fatturePassivo = fatturePassivo;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
