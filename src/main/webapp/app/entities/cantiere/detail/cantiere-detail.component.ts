import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICantiere } from '../cantiere.model';

@Component({
  selector: 'jhi-cantiere-detail',
  templateUrl: './cantiere-detail.component.html',
})
export class CantiereDetailComponent implements OnInit {
  cantiere: ICantiere | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cantiere }) => {
      this.cantiere = cantiere;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
