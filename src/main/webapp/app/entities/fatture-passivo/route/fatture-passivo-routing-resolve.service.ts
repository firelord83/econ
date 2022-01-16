import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFatturePassivo, FatturePassivo } from '../fatture-passivo.model';
import { FatturePassivoService } from '../service/fatture-passivo.service';

@Injectable({ providedIn: 'root' })
export class FatturePassivoRoutingResolveService implements Resolve<IFatturePassivo> {
  constructor(protected service: FatturePassivoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IFatturePassivo> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((fatturePassivo: HttpResponse<FatturePassivo>) => {
          if (fatturePassivo.body) {
            return of(fatturePassivo.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new FatturePassivo());
  }
}
