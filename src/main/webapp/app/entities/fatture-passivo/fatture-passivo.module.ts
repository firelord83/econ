import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FatturePassivoComponent } from './list/fatture-passivo.component';
import { FatturePassivoDetailComponent } from './detail/fatture-passivo-detail.component';
import { FatturePassivoUpdateComponent } from './update/fatture-passivo-update.component';
import { FatturePassivoDeleteDialogComponent } from './delete/fatture-passivo-delete-dialog.component';
import { FatturePassivoRoutingModule } from './route/fatture-passivo-routing.module';

@NgModule({
  imports: [SharedModule, FatturePassivoRoutingModule],
  declarations: [
    FatturePassivoComponent,
    FatturePassivoDetailComponent,
    FatturePassivoUpdateComponent,
    FatturePassivoDeleteDialogComponent,
  ],
  entryComponents: [FatturePassivoDeleteDialogComponent],
})
export class FatturePassivoModule {}
