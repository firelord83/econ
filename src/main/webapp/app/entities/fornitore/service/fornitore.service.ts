import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IFornitore, getFornitoreIdentifier } from '../fornitore.model';

export type EntityResponseType = HttpResponse<IFornitore>;
export type EntityArrayResponseType = HttpResponse<IFornitore[]>;

@Injectable({ providedIn: 'root' })
export class FornitoreService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/fornitores');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/fornitores');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(fornitore: IFornitore): Observable<EntityResponseType> {
    return this.http.post<IFornitore>(this.resourceUrl, fornitore, { observe: 'response' });
  }

  update(fornitore: IFornitore): Observable<EntityResponseType> {
    return this.http.put<IFornitore>(`${this.resourceUrl}/${getFornitoreIdentifier(fornitore) as number}`, fornitore, {
      observe: 'response',
    });
  }

  partialUpdate(fornitore: IFornitore): Observable<EntityResponseType> {
    return this.http.patch<IFornitore>(`${this.resourceUrl}/${getFornitoreIdentifier(fornitore) as number}`, fornitore, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IFornitore>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFornitore[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFornitore[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addFornitoreToCollectionIfMissing(
    fornitoreCollection: IFornitore[],
    ...fornitoresToCheck: (IFornitore | null | undefined)[]
  ): IFornitore[] {
    const fornitores: IFornitore[] = fornitoresToCheck.filter(isPresent);
    if (fornitores.length > 0) {
      const fornitoreCollectionIdentifiers = fornitoreCollection.map(fornitoreItem => getFornitoreIdentifier(fornitoreItem)!);
      const fornitoresToAdd = fornitores.filter(fornitoreItem => {
        const fornitoreIdentifier = getFornitoreIdentifier(fornitoreItem);
        if (fornitoreIdentifier == null || fornitoreCollectionIdentifiers.includes(fornitoreIdentifier)) {
          return false;
        }
        fornitoreCollectionIdentifiers.push(fornitoreIdentifier);
        return true;
      });
      return [...fornitoresToAdd, ...fornitoreCollection];
    }
    return fornitoreCollection;
  }
}
