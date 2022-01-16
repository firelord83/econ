import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IFatturePassivo, FatturePassivo } from '../fatture-passivo.model';
import { FatturePassivoService } from '../service/fatture-passivo.service';

import { FatturePassivoRoutingResolveService } from './fatture-passivo-routing-resolve.service';

describe('FatturePassivo routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: FatturePassivoRoutingResolveService;
  let service: FatturePassivoService;
  let resultFatturePassivo: IFatturePassivo | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(FatturePassivoRoutingResolveService);
    service = TestBed.inject(FatturePassivoService);
    resultFatturePassivo = undefined;
  });

  describe('resolve', () => {
    it('should return IFatturePassivo returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultFatturePassivo = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultFatturePassivo).toEqual({ id: 123 });
    });

    it('should return new IFatturePassivo if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultFatturePassivo = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultFatturePassivo).toEqual(new FatturePassivo());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as FatturePassivo })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultFatturePassivo = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultFatturePassivo).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
