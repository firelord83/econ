import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FattureAttivoService } from '../service/fatture-attivo.service';
import { IFattureAttivo, FattureAttivo } from '../fatture-attivo.model';
import { ICliente } from 'app/entities/cliente/cliente.model';
import { ClienteService } from 'app/entities/cliente/service/cliente.service';
import { ICantiere } from 'app/entities/cantiere/cantiere.model';
import { CantiereService } from 'app/entities/cantiere/service/cantiere.service';

import { FattureAttivoUpdateComponent } from './fatture-attivo-update.component';

describe('FattureAttivo Management Update Component', () => {
  let comp: FattureAttivoUpdateComponent;
  let fixture: ComponentFixture<FattureAttivoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let fattureAttivoService: FattureAttivoService;
  let clienteService: ClienteService;
  let cantiereService: CantiereService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [FattureAttivoUpdateComponent],
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
      .overrideTemplate(FattureAttivoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FattureAttivoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fattureAttivoService = TestBed.inject(FattureAttivoService);
    clienteService = TestBed.inject(ClienteService);
    cantiereService = TestBed.inject(CantiereService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Cliente query and add missing value', () => {
      const fattureAttivo: IFattureAttivo = { id: 456 };
      const cliente: ICliente = { id: 16949 };
      fattureAttivo.cliente = cliente;

      const clienteCollection: ICliente[] = [{ id: 97502 }];
      jest.spyOn(clienteService, 'query').mockReturnValue(of(new HttpResponse({ body: clienteCollection })));
      const additionalClientes = [cliente];
      const expectedCollection: ICliente[] = [...additionalClientes, ...clienteCollection];
      jest.spyOn(clienteService, 'addClienteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ fattureAttivo });
      comp.ngOnInit();

      expect(clienteService.query).toHaveBeenCalled();
      expect(clienteService.addClienteToCollectionIfMissing).toHaveBeenCalledWith(clienteCollection, ...additionalClientes);
      expect(comp.clientesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Cantiere query and add missing value', () => {
      const fattureAttivo: IFattureAttivo = { id: 456 };
      const cantiere: ICantiere = { id: 64625 };
      fattureAttivo.cantiere = cantiere;

      const cantiereCollection: ICantiere[] = [{ id: 24294 }];
      jest.spyOn(cantiereService, 'query').mockReturnValue(of(new HttpResponse({ body: cantiereCollection })));
      const additionalCantieres = [cantiere];
      const expectedCollection: ICantiere[] = [...additionalCantieres, ...cantiereCollection];
      jest.spyOn(cantiereService, 'addCantiereToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ fattureAttivo });
      comp.ngOnInit();

      expect(cantiereService.query).toHaveBeenCalled();
      expect(cantiereService.addCantiereToCollectionIfMissing).toHaveBeenCalledWith(cantiereCollection, ...additionalCantieres);
      expect(comp.cantieresSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const fattureAttivo: IFattureAttivo = { id: 456 };
      const cliente: ICliente = { id: 93667 };
      fattureAttivo.cliente = cliente;
      const cantiere: ICantiere = { id: 32895 };
      fattureAttivo.cantiere = cantiere;

      activatedRoute.data = of({ fattureAttivo });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(fattureAttivo));
      expect(comp.clientesSharedCollection).toContain(cliente);
      expect(comp.cantieresSharedCollection).toContain(cantiere);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<FattureAttivo>>();
      const fattureAttivo = { id: 123 };
      jest.spyOn(fattureAttivoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fattureAttivo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: fattureAttivo }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(fattureAttivoService.update).toHaveBeenCalledWith(fattureAttivo);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<FattureAttivo>>();
      const fattureAttivo = new FattureAttivo();
      jest.spyOn(fattureAttivoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fattureAttivo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: fattureAttivo }));
      saveSubject.complete();

      // THEN
      expect(fattureAttivoService.create).toHaveBeenCalledWith(fattureAttivo);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<FattureAttivo>>();
      const fattureAttivo = { id: 123 };
      jest.spyOn(fattureAttivoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fattureAttivo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(fattureAttivoService.update).toHaveBeenCalledWith(fattureAttivo);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackClienteById', () => {
      it('Should return tracked Cliente primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackClienteById(0, entity);
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
