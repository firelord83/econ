import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFattureAttivo, FattureAttivo } from '../fatture-attivo.model';
import { FattureAttivoService } from '../service/fatture-attivo.service';

@Injectable({ providedIn: 'root' })
export class FattureAttivoRoutingResolveService implements Resolve<IFattureAttivo> {
  constructor(protected service: FattureAttivoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IFattureAttivo> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((fattureAttivo: HttpResponse<FattureAttivo>) => {
          if (fattureAttivo.body) {
            return of(fattureAttivo.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new FattureAttivo());
  }
}
