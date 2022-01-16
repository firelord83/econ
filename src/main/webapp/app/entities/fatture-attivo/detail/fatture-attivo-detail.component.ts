import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFattureAttivo } from '../fatture-attivo.model';

@Component({
  selector: 'jhi-fatture-attivo-detail',
  templateUrl: './fatture-attivo-detail.component.html',
})
export class FattureAttivoDetailComponent implements OnInit {
  fattureAttivo: IFattureAttivo | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ fattureAttivo }) => {
      this.fattureAttivo = fattureAttivo;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
