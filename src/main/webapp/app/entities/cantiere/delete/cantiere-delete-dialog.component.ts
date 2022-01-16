import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICantiere } from '../cantiere.model';
import { CantiereService } from '../service/cantiere.service';

@Component({
  templateUrl: './cantiere-delete-dialog.component.html',
})
export class CantiereDeleteDialogComponent {
  cantiere?: ICantiere;

  constructor(protected cantiereService: CantiereService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.cantiereService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
