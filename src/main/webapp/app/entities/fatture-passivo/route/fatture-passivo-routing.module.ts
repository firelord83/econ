import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FatturePassivoComponent } from '../list/fatture-passivo.component';
import { FatturePassivoDetailComponent } from '../detail/fatture-passivo-detail.component';
import { FatturePassivoUpdateComponent } from '../update/fatture-passivo-update.component';
import { FatturePassivoRoutingResolveService } from './fatture-passivo-routing-resolve.service';

const fatturePassivoRoute: Routes = [
  {
    path: '',
    component: FatturePassivoComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FatturePassivoDetailComponent,
    resolve: {
      fatturePassivo: FatturePassivoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FatturePassivoUpdateComponent,
    resolve: {
      fatturePassivo: FatturePassivoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FatturePassivoUpdateComponent,
    resolve: {
      fatturePassivo: FatturePassivoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(fatturePassivoRoute)],
  exports: [RouterModule],
})
export class FatturePassivoRoutingModule {}
