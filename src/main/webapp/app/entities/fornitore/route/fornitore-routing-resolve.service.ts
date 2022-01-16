import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFornitore, Fornitore } from '../fornitore.model';
import { FornitoreService } from '../service/fornitore.service';

@Injectable({ providedIn: 'root' })
export class FornitoreRoutingResolveService implements Resolve<IFornitore> {
  constructor(protected service: FornitoreService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IFornitore> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((fornitore: HttpResponse<Fornitore>) => {
          if (fornitore.body) {
            return of(fornitore.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Fornitore());
  }
}
