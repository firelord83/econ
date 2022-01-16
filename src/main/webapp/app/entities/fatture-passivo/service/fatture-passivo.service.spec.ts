import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { Stato } from 'app/entities/enumerations/stato.model';
import { IFatturePassivo, FatturePassivo } from '../fatture-passivo.model';

import { FatturePassivoService } from './fatture-passivo.service';

describe('FatturePassivo Service', () => {
  let service: FatturePassivoService;
  let httpMock: HttpTestingController;
  let elemDefault: IFatturePassivo;
  let expectedResult: IFatturePassivo | IFatturePassivo[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(FatturePassivoService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      numeroFattura: 0,
      ragSociale: 'AAAAAAA',
      nomeFornitore: 'AAAAAAA',
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

    it('should create a FatturePassivo', () => {
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

      service.create(new FatturePassivo()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a FatturePassivo', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          numeroFattura: 1,
          ragSociale: 'BBBBBB',
          nomeFornitore: 'BBBBBB',
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

    it('should partial update a FatturePassivo', () => {
      const patchObject = Object.assign(
        {
          ragSociale: 'BBBBBB',
          nomeFornitore: 'BBBBBB',
          stato: 'BBBBBB',
          dataEmissione: currentDate.format(DATE_TIME_FORMAT),
        },
        new FatturePassivo()
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

    it('should return a list of FatturePassivo', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          numeroFattura: 1,
          ragSociale: 'BBBBBB',
          nomeFornitore: 'BBBBBB',
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

    it('should delete a FatturePassivo', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addFatturePassivoToCollectionIfMissing', () => {
      it('should add a FatturePassivo to an empty array', () => {
        const fatturePassivo: IFatturePassivo = { id: 123 };
        expectedResult = service.addFatturePassivoToCollectionIfMissing([], fatturePassivo);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(fatturePassivo);
      });

      it('should not add a FatturePassivo to an array that contains it', () => {
        const fatturePassivo: IFatturePassivo = { id: 123 };
        const fatturePassivoCollection: IFatturePassivo[] = [
          {
            ...fatturePassivo,
          },
          { id: 456 },
        ];
        expectedResult = service.addFatturePassivoToCollectionIfMissing(fatturePassivoCollection, fatturePassivo);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a FatturePassivo to an array that doesn't contain it", () => {
        const fatturePassivo: IFatturePassivo = { id: 123 };
        const fatturePassivoCollection: IFatturePassivo[] = [{ id: 456 }];
        expectedResult = service.addFatturePassivoToCollectionIfMissing(fatturePassivoCollection, fatturePassivo);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(fatturePassivo);
      });

      it('should add only unique FatturePassivo to an array', () => {
        const fatturePassivoArray: IFatturePassivo[] = [{ id: 123 }, { id: 456 }, { id: 97395 }];
        const fatturePassivoCollection: IFatturePassivo[] = [{ id: 123 }];
        expectedResult = service.addFatturePassivoToCollectionIfMissing(fatturePassivoCollection, ...fatturePassivoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const fatturePassivo: IFatturePassivo = { id: 123 };
        const fatturePassivo2: IFatturePassivo = { id: 456 };
        expectedResult = service.addFatturePassivoToCollectionIfMissing([], fatturePassivo, fatturePassivo2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(fatturePassivo);
        expect(expectedResult).toContain(fatturePassivo2);
      });

      it('should accept null and undefined values', () => {
        const fatturePassivo: IFatturePassivo = { id: 123 };
        expectedResult = service.addFatturePassivoToCollectionIfMissing([], null, fatturePassivo, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(fatturePassivo);
      });

      it('should return initial array if no FatturePassivo is added', () => {
        const fatturePassivoCollection: IFatturePassivo[] = [{ id: 123 }];
        expectedResult = service.addFatturePassivoToCollectionIfMissing(fatturePassivoCollection, undefined, null);
        expect(expectedResult).toEqual(fatturePassivoCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
