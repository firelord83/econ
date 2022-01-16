import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FatturePassivoService } from '../service/fatture-passivo.service';
import { IFatturePassivo, FatturePassivo } from '../fatture-passivo.model';
import { IFornitore } from 'app/entities/fornitore/fornitore.model';
import { FornitoreService } from 'app/entities/fornitore/service/fornitore.service';
import { ICantiere } from 'app/entities/cantiere/cantiere.model';
import { CantiereService } from 'app/entities/cantiere/service/cantiere.service';

import { FatturePassivoUpdateComponent } from './fatture-passivo-update.component';

describe('FatturePassivo Management Update Component', () => {
  let comp: FatturePassivoUpdateComponent;
  let fixture: ComponentFixture<FatturePassivoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let fatturePassivoService: FatturePassivoService;
  let fornitoreService: FornitoreService;
  let cantiereService: CantiereService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [FatturePassivoUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(FatturePassivoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FatturePassivoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fatturePassivoService = TestBed.inject(FatturePassivoService);
    fornitoreService = TestBed.inject(FornitoreService);
    cantiereService = TestBed.inject(CantiereService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Fornitore query and add missing value', () => {
      const fatturePassivo: IFatturePassivo = { id: 456 };
      const fornitore: IFornitore = { id: 39699 };
      fatturePassivo.fornitore = fornitore;

      const fornitoreCollection: IFornitore[] = [{ id: 38956 }];
      jest.spyOn(fornitoreService, 'query').mockReturnValue(of(new HttpResponse({ body: fornitoreCollection })));
      const additionalFornitores = [fornitore];
      const expectedCollection: IFornitore[] = [...additionalFornitores, ...fornitoreCollection];
      jest.spyOn(fornitoreService, 'addFornitoreToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ fatturePassivo });
      comp.ngOnInit();

      expect(fornitoreService.query).toHaveBeenCalled();
      expect(fornitoreService.addFornitoreToCollectionIfMissing).toHaveBeenCalledWith(fornitoreCollection, ...additionalFornitores);
      expect(comp.fornitoresSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Cantiere query and add missing value', () => {
      const fatturePassivo: IFatturePassivo = { id: 456 };
      const cantiere: ICantiere = { id: 25637 };
      fatturePassivo.cantiere = cantiere;

      const cantiereCollection: ICantiere[] = [{ id: 41011 }];
      jest.spyOn(cantiereService, 'query').mockReturnValue(of(new HttpResponse({ body: cantiereCollection })));
      const additionalCantieres = [cantiere];
      const expectedCollection: ICantiere[] = [...additionalCantieres, ...cantiereCollection];
      jest.spyOn(cantiereService, 'addCantiereToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ fatturePassivo });
      comp.ngOnInit();

      expect(cantiereService.query).toHaveBeenCalled();
      expect(cantiereService.addCantiereToCollectionIfMissing).toHaveBeenCalledWith(cantiereCollection, ...additionalCantieres);
      expect(comp.cantieresSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const fatturePassivo: IFatturePassivo = { id: 456 };
      const fornitore: IFornitore = { id: 74952 };
      fatturePassivo.fornitore = fornitore;
      const cantiere: ICantiere = { id: 77885 };
      fatturePassivo.cantiere = cantiere;

      activatedRoute.data = of({ fatturePassivo });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(fatturePassivo));
      expect(comp.fornitoresSharedCollection).toContain(fornitore);
      expect(comp.cantieresSharedCollection).toContain(cantiere);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<FatturePassivo>>();
      const fatturePassivo = { id: 123 };
      jest.spyOn(fatturePassivoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fatturePassivo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: fatturePassivo }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(fatturePassivoService.update).toHaveBeenCalledWith(fatturePassivo);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<FatturePassivo>>();
      const fatturePassivo = new FatturePassivo();
      jest.spyOn(fatturePassivoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fatturePassivo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: fatturePassivo }));
      saveSubject.complete();

      // THEN
      expect(fatturePassivoService.create).toHaveBeenCalledWith(fatturePassivo);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<FatturePassivo>>();
      const fatturePassivo = { id: 123 };
      jest.spyOn(fatturePassivoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fatturePassivo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(fatturePassivoService.update).toHaveBeenCalledWith(fatturePassivo);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackFornitoreById', () => {
      it('Should return tracked Fornitore primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackFornitoreById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackCantiereById', () => {
      it('Should return tracked Cantiere primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackCantiereById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
