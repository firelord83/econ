import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IFattureAttivo } from '../fatture-attivo.model';
import { FattureAttivoService } from '../service/fatture-attivo.service';
import { FattureAttivoDeleteDialogComponent } from '../delete/fatture-attivo-delete-dialog.component';

@Component({
  selector: 'jhi-fatture-attivo',
  templateUrl: './fatture-attivo.component.html',
})
export class FattureAttivoComponent implements OnInit {
  fattureAttivos?: IFattureAttivo[];
  isLoading = false;
  currentSearch: string;

  constructor(
    protected fattureAttivoService: FattureAttivoService,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.fattureAttivoService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<IFattureAttivo[]>) => {
            this.isLoading = false;
            this.fattureAttivos = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.fattureAttivoService.query().subscribe({
      next: (res: HttpResponse<IFattureAttivo[]>) => {
        this.isLoading = false;
        this.fattureAttivos = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IFattureAttivo): number {
    return item.id!;
  }

  delete(fattureAttivo: IFattureAttivo): void {
    const modalRef = this.modalService.open(FattureAttivoDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.fattureAttivo = fattureAttivo;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
