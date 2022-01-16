import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ICantiere, getCantiereIdentifier } from '../cantiere.model';

export type EntityResponseType = HttpResponse<ICantiere>;
export type EntityArrayResponseType = HttpResponse<ICantiere[]>;

@Injectable({ providedIn: 'root' })
export class CantiereService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/cantieres');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/cantieres');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(cantiere: ICantiere): Observable<EntityResponseType> {
    return this.http.post<ICantiere>(this.resourceUrl, cantiere, { observe: 'response' });
  }

  update(cantiere: ICantiere): Observable<EntityResponseType> {
    return this.http.put<ICantiere>(`${this.resourceUrl}/${getCantiereIdentifier(cantiere) as number}`, cantiere, { observe: 'response' });
  }

  partialUpdate(cantiere: ICantiere): Observable<EntityResponseType> {
    return this.http.patch<ICantiere>(`${this.resourceUrl}/${getCantiereIdentifier(cantiere) as number}`, cantiere, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICantiere>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICantiere[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICantiere[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addCantiereToCollectionIfMissing(cantiereCollection: ICantiere[], ...cantieresToCheck: (ICantiere | null | undefined)[]): ICantiere[] {
    const cantieres: ICantiere[] = cantieresToCheck.filter(isPresent);
    if (cantieres.length > 0) {
      const cantiereCollectionIdentifiers = cantiereCollection.map(cantiereItem => getCantiereIdentifier(cantiereItem)!);
      const cantieresToAdd = cantieres.filter(cantiereItem => {
        const cantiereIdentifier = getCantiereIdentifier(cantiereItem);
        if (cantiereIdentifier == null || cantiereCollectionIdentifiers.includes(cantiereIdentifier)) {
          return false;
        }
        cantiereCollectionIdentifiers.push(cantiereIdentifier);
        return true;
      });
      return [...cantieresToAdd, ...cantiereCollection];
    }
    return cantiereCollection;
  }
}
