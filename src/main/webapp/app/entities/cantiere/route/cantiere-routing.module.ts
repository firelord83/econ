import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CantiereComponent } from '../list/cantiere.component';
import { CantiereDetailComponent } from '../detail/cantiere-detail.component';
import { CantiereUpdateComponent } from '../update/cantiere-update.component';
import { CantiereRoutingResolveService } from './cantiere-routing-resolve.service';

const cantiereRoute: Routes = [
  {
    path: '',
    component: CantiereComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CantiereDetailComponent,
    resolve: {
      cantiere: CantiereRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CantiereUpdateComponent,
    resolve: {
      cantiere: CantiereRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CantiereUpdateComponent,
    resolve: {
      cantiere: CantiereRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(cantiereRoute)],
  exports: [RouterModule],
})
export class CantiereRoutingModule {}
