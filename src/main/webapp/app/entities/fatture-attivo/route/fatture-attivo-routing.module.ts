import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FattureAttivoComponent } from '../list/fatture-attivo.component';
import { FattureAttivoDetailComponent } from '../detail/fatture-attivo-detail.component';
import { FattureAttivoUpdateComponent } from '../update/fatture-attivo-update.component';
import { FattureAttivoRoutingResolveService } from './fatture-attivo-routing-resolve.service';

const fattureAttivoRoute: Routes = [
  {
    path: '',
    component: FattureAttivoComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FattureAttivoDetailComponent,
    resolve: {
      fattureAttivo: FattureAttivoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FattureAttivoUpdateComponent,
    resolve: {
      fattureAttivo: FattureAttivoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FattureAttivoUpdateComponent,
    resolve: {
      fattureAttivo: FattureAttivoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(fattureAttivoRoute)],
  exports: [RouterModule],
})
export class FattureAttivoRoutingModule {}
