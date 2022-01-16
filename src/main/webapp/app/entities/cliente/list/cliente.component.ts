import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICliente } from '../cliente.model';
import { ClienteService } from '../service/cliente.service';
import { ClienteDeleteDialogComponent } from '../delete/cliente-delete-dialog.component';

@Component({
  selector: 'jhi-cliente',
  templateUrl: './cliente.component.html',
})
export class ClienteComponent implements OnInit {
  clientes?: ICliente[];
  isLoading = false;
  currentSearch: string;

  constructor(protected clienteService: ClienteService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.clienteService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<ICliente[]>) => {
            this.isLoading = false;
            this.clientes = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.clienteService.query().subscribe({
      next: (res: HttpResponse<ICliente[]>) => {
        this.isLoading = false;
        this.clientes = res.body ?? [];
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

  trackId(index: number, item: ICliente): number {
    return item.id!;
  }

  delete(cliente: ICliente): void {
    const modalRef = this.modalService.open(ClienteDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.cliente = cliente;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
