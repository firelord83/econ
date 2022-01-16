import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IFatturePassivo } from '../fatture-passivo.model';
import { FatturePassivoService } from '../service/fatture-passivo.service';

@Component({
  templateUrl: './fatture-passivo-delete-dialog.component.html',
})
export class FatturePassivoDeleteDialogComponent {
  fatturePassivo?: IFatturePassivo;

  constructor(protected fatturePassivoService: FatturePassivoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.fatturePassivoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
