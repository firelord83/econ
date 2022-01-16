import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CantiereComponent } from './list/cantiere.component';
import { CantiereDetailComponent } from './detail/cantiere-detail.component';
import { CantiereUpdateComponent } from './update/cantiere-update.component';
import { CantiereDeleteDialogComponent } from './delete/cantiere-delete-dialog.component';
import { CantiereRoutingModule } from './route/cantiere-routing.module';

@NgModule({
  imports: [SharedModule, CantiereRoutingModule],
  declarations: [CantiereComponent, CantiereDetailComponent, CantiereUpdateComponent, CantiereDeleteDialogComponent],
  entryComponents: [CantiereDeleteDialogComponent],
})
export class CantiereModule {}
