import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IFatturePassivo, getFatturePassivoIdentifier } from '../fatture-passivo.model';

export type EntityResponseType = HttpResponse<IFatturePassivo>;
export type EntityArrayResponseType = HttpResponse<IFatturePassivo[]>;

@Injectable({ providedIn: 'root' })
export class FatturePassivoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/fatture-passivos');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/fatture-passivos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(fatturePassivo: IFatturePassivo): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(fatturePassivo);
    return this.http
      .post<IFatturePassivo>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(fatturePassivo: IFatturePassivo): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(fatturePassivo);
    return this.http
      .put<IFatturePassivo>(`${this.resourceUrl}/${getFatturePassivoIdentifier(fatturePassivo) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(fatturePassivo: IFatturePassivo): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(fatturePassivo);
    return this.http
      .patch<IFatturePassivo>(`${this.resourceUrl}/${getFatturePassivoIdentifier(fatturePassivo) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IFatturePassivo>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IFatturePassivo[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IFatturePassivo[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  addFatturePassivoToCollectionIfMissing(
    fatturePassivoCollection: IFatturePassivo[],
    ...fatturePassivosToCheck: (IFatturePassivo | null | undefined)[]
  ): IFatturePassivo[] {
    const fatturePassivos: IFatturePassivo[] = fatturePassivosToCheck.filter(isPresent);
    if (fatturePassivos.length > 0) {
      const fatturePassivoCollectionIdentifiers = fatturePassivoCollection.map(
        fatturePassivoItem => getFatturePassivoIdentifier(fatturePassivoItem)!
      );
      const fatturePassivosToAdd = fatturePassivos.filter(fatturePassivoItem => {
        const fatturePassivoIdentifier = getFatturePassivoIdentifier(fatturePassivoItem);
        if (fatturePassivoIdentifier == null || fatturePassivoCollectionIdentifiers.includes(fatturePassivoIdentifier)) {
          return false;
        }
        fatturePassivoCollectionIdentifiers.push(fatturePassivoIdentifier);
        return true;
      });
      return [...fatturePassivosToAdd, ...fatturePassivoCollection];
    }
    return fatturePassivoCollection;
  }

  protected convertDateFromClient(fatturePassivo: IFatturePassivo): IFatturePassivo {
    return Object.assign({}, fatturePassivo, {
      dataEmissione: fatturePassivo.dataEmissione?.isValid() ? fatturePassivo.dataEmissione.toJSON() : undefined,
      dataPagamento: fatturePassivo.dataPagamento?.isValid() ? fatturePassivo.dataPagamento.toJSON() : undefined,
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
      res.body.forEach((fatturePassivo: IFatturePassivo) => {
        fatturePassivo.dataEmissione = fatturePassivo.dataEmissione ? dayjs(fatturePassivo.dataEmissione) : undefined;
        fatturePassivo.dataPagamento = fatturePassivo.dataPagamento ? dayjs(fatturePassivo.dataPagamento) : undefined;
      });
    }
    return res;
  }
}
