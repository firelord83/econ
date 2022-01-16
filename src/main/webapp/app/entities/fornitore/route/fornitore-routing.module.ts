import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FornitoreComponent } from '../list/fornitore.component';
import { FornitoreDetailComponent } from '../detail/fornitore-detail.component';
import { FornitoreUpdateComponent } from '../update/fornitore-update.component';
import { FornitoreRoutingResolveService } from './fornitore-routing-resolve.service';

const fornitoreRoute: Routes = [
  {
    path: '',
    component: FornitoreComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FornitoreDetailComponent,
    resolve: {
      fornitore: FornitoreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FornitoreUpdateComponent,
    resolve: {
      fornitore: FornitoreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FornitoreUpdateComponent,
    resolve: {
      fornitore: FornitoreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(fornitoreRoute)],
  exports: [RouterModule],
})
export class FornitoreRoutingModule {}
