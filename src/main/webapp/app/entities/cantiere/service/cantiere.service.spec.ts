import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICantiere, Cantiere } from '../cantiere.model';

import { CantiereService } from './cantiere.service';

describe('Cantiere Service', () => {
  let service: CantiereService;
  let httpMock: HttpTestingController;
  let elemDefault: ICantiere;
  let expectedResult: ICantiere | ICantiere[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CantiereService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nomeCantiere: 'AAAAAAA',
      indirizzo: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Cantiere', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Cantiere()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Cantiere', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nomeCantiere: 'BBBBBB',
          indirizzo: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Cantiere', () => {
      const patchObject = Object.assign(
        {
          nomeCantiere: 'BBBBBB',
          indirizzo: 'BBBBBB',
        },
        new Cantiere()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Cantiere', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nomeCantiere: 'BBBBBB',
          indirizzo: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Cantiere', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCantiereToCollectionIfMissing', () => {
      it('should add a Cantiere to an empty array', () => {
        const cantiere: ICantiere = { id: 123 };
        expectedResult = service.addCantiereToCollectionIfMissing([], cantiere);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cantiere);
      });

      it('should not add a Cantiere to an array that contains it', () => {
        const cantiere: ICantiere = { id: 123 };
        const cantiereCollection: ICantiere[] = [
          {
            ...cantiere,
          },
          { id: 456 },
        ];
        expectedResult = service.addCantiereToCollectionIfMissing(cantiereCollection, cantiere);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Cantiere to an array that doesn't contain it", () => {
        const cantiere: ICantiere = { id: 123 };
        const cantiereCollection: ICantiere[] = [{ id: 456 }];
        expectedResult = service.addCantiereToCollectionIfMissing(cantiereCollection, cantiere);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cantiere);
      });

      it('should add only unique Cantiere to an array', () => {
        const cantiereArray: ICantiere[] = [{ id: 123 }, { id: 456 }, { id: 28580 }];
        const cantiereCollection: ICantiere[] = [{ id: 123 }];
        expectedResult = service.addCantiereToCollectionIfMissing(cantiereCollection, ...cantiereArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const cantiere: ICantiere = { id: 123 };
        const cantiere2: ICantiere = { id: 456 };
        expectedResult = service.addCantiereToCollectionIfMissing([], cantiere, cantiere2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cantiere);
        expect(expectedResult).toContain(cantiere2);
      });

      it('should accept null and undefined values', () => {
        const cantiere: ICantiere = { id: 123 };
        expectedResult = service.addCantiereToCollectionIfMissing([], null, cantiere, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cantiere);
      });

      it('should return initial array if no Cantiere is added', () => {
        const cantiereCollection: ICantiere[] = [{ id: 123 }];
        expectedResult = service.addCantiereToCollectionIfMissing(cantiereCollection, undefined, null);
        expect(expectedResult).toEqual(cantiereCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
