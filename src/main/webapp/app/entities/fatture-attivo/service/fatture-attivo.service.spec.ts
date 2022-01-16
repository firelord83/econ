import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { Stato } from 'app/entities/enumerations/stato.model';
import { IFattureAttivo, FattureAttivo } from '../fatture-attivo.model';

import { FattureAttivoService } from './fatture-attivo.service';

describe('FattureAttivo Service', () => {
  let service: FattureAttivoService;
  let httpMock: HttpTestingController;
  let elemDefault: IFattureAttivo;
  let expectedResult: IFattureAttivo | IFattureAttivo[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(FattureAttivoService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      numeroFattura: 0,
      ragSociale: 'AAAAAAA',
      nomeCliente: 'AAAAAAA',
      imponibile: 0,
      iva: 0,
      stato: Stato.NON_PAGATA,
      dataEmissione: currentDate,
      dataPagamento: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          dataEmissione: currentDate.format(DATE_TIME_FORMAT),
          dataPagamento: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a FattureAttivo', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          dataEmissione: currentDate.format(DATE_TIME_FORMAT),
          dataPagamento: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dataEmissione: currentDate,
          dataPagamento: currentDate,
        },
        returnedFromService
      );

      service.create(new FattureAttivo()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a FattureAttivo', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          numeroFattura: 1,
          ragSociale: 'BBBBBB',
          nomeCliente: 'BBBBBB',
          imponibile: 1,
          iva: 1,
          stato: 'BBBBBB',
          dataEmissione: currentDate.format(DATE_TIME_FORMAT),
          dataPagamento: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dataEmissione: currentDate,
          dataPagamento: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a FattureAttivo', () => {
      const patchObject = Object.assign(
        {
          ragSociale: 'BBBBBB',
          stato: 'BBBBBB',
          dataPagamento: currentDate.format(DATE_TIME_FORMAT),
        },
        new FattureAttivo()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          dataEmissione: currentDate,
          dataPagamento: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of FattureAttivo', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          numeroFattura: 1,
          ragSociale: 'BBBBBB',
          nomeCliente: 'BBBBBB',
          imponibile: 1,
          iva: 1,
          stato: 'BBBBBB',
          dataEmissione: currentDate.format(DATE_TIME_FORMAT),
          dataPagamento: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dataEmissione: currentDate,
          dataPagamento: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a FattureAttivo', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addFattureAttivoToCollectionIfMissing', () => {
      it('should add a FattureAttivo to an empty array', () => {
        const fattureAttivo: IFattureAttivo = { id: 123 };
        expectedResult = service.addFattureAttivoToCollectionIfMissing([], fattureAttivo);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(fattureAttivo);
      });

      it('should not add a FattureAttivo to an array that contains it', () => {
        const fattureAttivo: IFattureAttivo = { id: 123 };
        const fattureAttivoCollection: IFattureAttivo[] = [
          {
            ...fattureAttivo,
          },
          { id: 456 },
        ];
        expectedResult = service.addFattureAttivoToCollectionIfMissing(fattureAttivoCollection, fattureAttivo);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a FattureAttivo to an array that doesn't contain it", () => {
        const fattureAttivo: IFattureAttivo = { id: 123 };
        const fattureAttivoCollection: IFattureAttivo[] = [{ id: 456 }];
        expectedResult = service.addFattureAttivoToCollectionIfMissing(fattureAttivoCollection, fattureAttivo);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(fattureAttivo);
      });

      it('should add only unique FattureAttivo to an array', () => {
        const fattureAttivoArray: IFattureAttivo[] = [{ id: 123 }, { id: 456 }, { id: 70863 }];
        const fattureAttivoCollection: IFattureAttivo[] = [{ id: 123 }];
        expectedResult = service.addFattureAttivoToCollectionIfMissing(fattureAttivoCollection, ...fattureAttivoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const fattureAttivo: IFattureAttivo = { id: 123 };
        const fattureAttivo2: IFattureAttivo = { id: 456 };
        expectedResult = service.addFattureAttivoToCollectionIfMissing([], fattureAttivo, fattureAttivo2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(fattureAttivo);
        expect(expectedResult).toContain(fattureAttivo2);
      });

      it('should accept null and undefined values', () => {
        const fattureAttivo: IFattureAttivo = { id: 123 };
        expectedResult = service.addFattureAttivoToCollectionIfMissing([], null, fattureAttivo, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(fattureAttivo);
      });

      it('should return initial array if no FattureAttivo is added', () => {
        const fattureAttivoCollection: IFattureAttivo[] = [{ id: 123 }];
        expectedResult = service.addFattureAttivoToCollectionIfMissing(fattureAttivoCollection, undefined, null);
        expect(expectedResult).toEqual(fattureAttivoCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
