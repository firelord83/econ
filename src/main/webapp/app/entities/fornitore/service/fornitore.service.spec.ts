import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IFornitore, Fornitore } from '../fornitore.model';

import { FornitoreService } from './fornitore.service';

describe('Fornitore Service', () => {
  let service: FornitoreService;
  let httpMock: HttpTestingController;
  let elemDefault: IFornitore;
  let expectedResult: IFornitore | IFornitore[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(FornitoreService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nomeFornitore: 'AAAAAAA',
      indirizzo: 'AAAAAAA',
      tipo: 'AAAAAAA',
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

    it('should create a Fornitore', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Fornitore()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Fornitore', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nomeFornitore: 'BBBBBB',
          indirizzo: 'BBBBBB',
          tipo: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Fornitore', () => {
      const patchObject = Object.assign(
        {
          nomeFornitore: 'BBBBBB',
          tipo: 'BBBBBB',
        },
        new Fornitore()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Fornitore', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nomeFornitore: 'BBBBBB',
          indirizzo: 'BBBBBB',
          tipo: 'BBBBBB',
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

    it('should delete a Fornitore', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addFornitoreToCollectionIfMissing', () => {
      it('should add a Fornitore to an empty array', () => {
        const fornitore: IFornitore = { id: 123 };
        expectedResult = service.addFornitoreToCollectionIfMissing([], fornitore);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(fornitore);
      });

      it('should not add a Fornitore to an array that contains it', () => {
        const fornitore: IFornitore = { id: 123 };
        const fornitoreCollection: IFornitore[] = [
          {
            ...fornitore,
          },
          { id: 456 },
        ];
        expectedResult = service.addFornitoreToCollectionIfMissing(fornitoreCollection, fornitore);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Fornitore to an array that doesn't contain it", () => {
        const fornitore: IFornitore = { id: 123 };
        const fornitoreCollection: IFornitore[] = [{ id: 456 }];
        expectedResult = service.addFornitoreToCollectionIfMissing(fornitoreCollection, fornitore);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(fornitore);
      });

      it('should add only unique Fornitore to an array', () => {
        const fornitoreArray: IFornitore[] = [{ id: 123 }, { id: 456 }, { id: 48986 }];
        const fornitoreCollection: IFornitore[] = [{ id: 123 }];
        expectedResult = service.addFornitoreToCollectionIfMissing(fornitoreCollection, ...fornitoreArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const fornitore: IFornitore = { id: 123 };
        const fornitore2: IFornitore = { id: 456 };
        expectedResult = service.addFornitoreToCollectionIfMissing([], fornitore, fornitore2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(fornitore);
        expect(expectedResult).toContain(fornitore2);
      });

      it('should accept null and undefined values', () => {
        const fornitore: IFornitore = { id: 123 };
        expectedResult = service.addFornitoreToCollectionIfMissing([], null, fornitore, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(fornitore);
      });

      it('should return initial array if no Fornitore is added', () => {
        const fornitoreCollection: IFornitore[] = [{ id: 123 }];
        expectedResult = service.addFornitoreToCollectionIfMissing(fornitoreCollection, undefined, null);
        expect(expectedResult).toEqual(fornitoreCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
