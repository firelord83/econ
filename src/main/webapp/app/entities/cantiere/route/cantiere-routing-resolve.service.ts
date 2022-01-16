import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICantiere, Cantiere } from '../cantiere.model';
import { CantiereService } from '../service/cantiere.service';

@Injectable({ providedIn: 'root' })
export class CantiereRoutingResolveService implements Resolve<ICantiere> {
  constructor(protected service: CantiereService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICantiere> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((cantiere: HttpResponse<Cantiere>) => {
          if (cantiere.body) {
            return of(cantiere.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Cantiere());
  }
}
