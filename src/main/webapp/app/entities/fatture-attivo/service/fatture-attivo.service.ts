import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IFattureAttivo, getFattureAttivoIdentifier } from '../fatture-attivo.model';

export type EntityResponseType = HttpResponse<IFattureAttivo>;
export type EntityArrayResponseType = HttpResponse<IFattureAttivo[]>;

@Injectable({ providedIn: 'root' })
export class FattureAttivoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/fatture-attivos');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/fatture-attivos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(fattureAttivo: IFattureAttivo): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(fattureAttivo);
    return this.http
      .post<IFattureAttivo>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(fattureAttivo: IFattureAttivo): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(fattureAttivo);
    return this.http
      .put<IFattureAttivo>(`${this.resourceUrl}/${getFattureAttivoIdentifier(fattureAttivo) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(fattureAttivo: IFattureAttivo): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(fattureAttivo);
    return this.http
      .patch<IFattureAttivo>(`${this.resourceUrl}/${getFattureAttivoIdentifier(fattureAttivo) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IFattureAttivo>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IFattureAttivo[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IFattureAttivo[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  addFattureAttivoToCollectionIfMissing(
    fattureAttivoCollection: IFattureAttivo[],
    ...fattureAttivosToCheck: (IFattureAttivo | null | undefined)[]
  ): IFattureAttivo[] {
    const fattureAttivos: IFattureAttivo[] = fattureAttivosToCheck.filter(isPresent);
    if (fattureAttivos.length > 0) {
      const fattureAttivoCollectionIdentifiers = fattureAttivoCollection.map(
        fattureAttivoItem => getFattureAttivoIdentifier(fattureAttivoItem)!
      );
      const fattureAttivosToAdd = fattureAttivos.filter(fattureAttivoItem => {
        const fattureAttivoIdentifier = getFattureAttivoIdentifier(fattureAttivoItem);
        if (fattureAttivoIdentifier == null || fattureAttivoCollectionIdentifiers.includes(fattureAttivoIdentifier)) {
          return false;
        }
        fattureAttivoCollectionIdentifiers.push(fattureAttivoIdentifier);
        return true;
      });
      return [...fattureAttivosToAdd, ...fattureAttivoCollection];
    }
    return fattureAttivoCollection;
  }

  protected convertDateFromClient(fattureAttivo: IFattureAttivo): IFattureAttivo {
    return Object.assign({}, fattureAttivo, {
      dataEmissione: fattureAttivo.dataEmissione?.isValid() ? fattureAttivo.dataEmissione.toJSON() : undefined,
      dataPagamento: fattureAttivo.dataPagamento?.isValid() ? fattureAttivo.dataPagamento.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dataEmissione = res.body.dataEmissione ? dayjs(res.body.dataEmissione) : undefined;
      res.body.dataPagamento = res.body.dataPagamento ? dayjs(res.body.dataPagamento) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((fattureAttivo: IFattureAttivo) => {
        fattureAttivo.dataEmissione = fattureAttivo.dataEmissione ? dayjs(fattureAttivo.dataEmissione) : undefined;
        fattureAttivo.dataPagamento = fattureAttivo.dataPagamento ? dayjs(fattureAttivo.dataPagamento) : undefined;
      });
    }
    return res;
  }
}
