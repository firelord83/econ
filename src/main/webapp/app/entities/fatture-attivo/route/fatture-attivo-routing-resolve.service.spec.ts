import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IFattureAttivo, FattureAttivo } from '../fatture-attivo.model';
import { FattureAttivoService } from '../service/fatture-attivo.service';

import { FattureAttivoRoutingResolveService } from './fatture-attivo-routing-resolve.service';

describe('FattureAttivo routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: FattureAttivoRoutingResolveService;
  let service: FattureAttivoService;
  let resultFattureAttivo: IFattureAttivo | undefined;

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
    routingResolveService = TestBed.inject(FattureAttivoRoutingResolveService);
    service = TestBed.inject(FattureAttivoService);
    resultFattureAttivo = undefined;
  });

  describe('resolve', () => {
    it('should return IFattureAttivo returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultFattureAttivo = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultFattureAttivo).toEqual({ id: 123 });
    });

    it('should return new IFattureAttivo if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultFattureAttivo = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultFattureAttivo).toEqual(new FattureAttivo());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as FattureAttivo })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultFattureAttivo = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultFattureAttivo).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
