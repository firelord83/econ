import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFornitore } from '../fornitore.model';

@Component({
  selector: 'jhi-fornitore-detail',
  templateUrl: './fornitore-detail.component.html',
})
export class FornitoreDetailComponent implements OnInit {
  fornitore: IFornitore | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ fornitore }) => {
      this.fornitore = fornitore;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
