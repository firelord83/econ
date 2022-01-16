import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'fatture-attivo',
        data: { pageTitle: 'econApp.fattureAttivo.home.title' },
        loadChildren: () => import('./fatture-attivo/fatture-attivo.module').then(m => m.FattureAttivoModule),
      },
      {
        path: 'fatture-passivo',
        data: { pageTitle: 'econApp.fatturePassivo.home.title' },
        loadChildren: () => import('./fatture-passivo/fatture-passivo.module').then(m => m.FatturePassivoModule),
      },
      {
        path: 'cliente',
        data: { pageTitle: 'econApp.cliente.home.title' },
        loadChildren: () => import('./cliente/cliente.module').then(m => m.ClienteModule),
      },
      {
        path: 'fornitore',
        data: { pageTitle: 'econApp.fornitore.home.title' },
        loadChildren: () => import('./fornitore/fornitore.module').then(m => m.FornitoreModule),
      },
      {
        path: 'cantiere',
        data: { pageTitle: 'econApp.cantiere.home.title' },
        loadChildren: () => import('./cantiere/cantiere.module').then(m => m.CantiereModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
