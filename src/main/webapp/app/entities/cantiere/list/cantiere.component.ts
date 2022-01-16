import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICantiere } from '../cantiere.model';
import { CantiereService } from '../service/cantiere.service';
import { CantiereDeleteDialogComponent } from '../delete/cantiere-delete-dialog.component';

@Component({
  selector: 'jhi-cantiere',
  templateUrl: './cantiere.component.html',
})
export class CantiereComponent implements OnInit {
  cantieres?: ICantiere[];
  isLoading = false;
  currentSearch: string;

  constructor(protected cantiereService: CantiereService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.cantiereService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<ICantiere[]>) => {
            this.isLoading = false;
            this.cantieres = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.cantiereService.query().subscribe({
      next: (res: HttpResponse<ICantiere[]>) => {
        this.isLoading = false;
        this.cantieres = res.body ?? [];
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

  trackId(index: number, item: ICantiere): number {
    return item.id!;
  }

  delete(cantiere: ICantiere): void {
    const modalRef = this.modalService.open(CantiereDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.cantiere = cantiere;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
