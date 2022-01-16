import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IFattureAttivo } from '../fatture-attivo.model';
import { FattureAttivoService } from '../service/fatture-attivo.service';

@Component({
  templateUrl: './fatture-attivo-delete-dialog.component.html',
})
export class FattureAttivoDeleteDialogComponent {
  fattureAttivo?: IFattureAttivo;

  constructor(protected fattureAttivoService: FattureAttivoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.fattureAttivoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
