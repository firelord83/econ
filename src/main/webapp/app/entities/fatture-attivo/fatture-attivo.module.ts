import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FattureAttivoComponent } from './list/fatture-attivo.component';
import { FattureAttivoDetailComponent } from './detail/fatture-attivo-detail.component';
import { FattureAttivoUpdateComponent } from './update/fatture-attivo-update.component';
import { FattureAttivoDeleteDialogComponent } from './delete/fatture-attivo-delete-dialog.component';
import { FattureAttivoRoutingModule } from './route/fatture-attivo-routing.module';

@NgModule({
  imports: [SharedModule, FattureAttivoRoutingModule],
  declarations: [FattureAttivoComponent, FattureAttivoDetailComponent, FattureAttivoUpdateComponent, FattureAttivoDeleteDialogComponent],
  entryComponents: [FattureAttivoDeleteDialogComponent],
})
export class FattureAttivoModule {}
