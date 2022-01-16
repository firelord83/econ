import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IFornitore } from '../fornitore.model';
import { FornitoreService } from '../service/fornitore.service';

@Component({
  templateUrl: './fornitore-delete-dialog.component.html',
})
export class FornitoreDeleteDialogComponent {
  fornitore?: IFornitore;

  constructor(protected fornitoreService: FornitoreService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.fornitoreService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
