import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FornitoreComponent } from './list/fornitore.component';
import { FornitoreDetailComponent } from './detail/fornitore-detail.component';
import { FornitoreUpdateComponent } from './update/fornitore-update.component';
import { FornitoreDeleteDialogComponent } from './delete/fornitore-delete-dialog.component';
import { FornitoreRoutingModule } from './route/fornitore-routing.module';

@NgModule({
  imports: [SharedModule, FornitoreRoutingModule],
  declarations: [FornitoreComponent, FornitoreDetailComponent, FornitoreUpdateComponent, FornitoreDeleteDialogComponent],
  entryComponents: [FornitoreDeleteDialogComponent],
})
export class FornitoreModule {}
