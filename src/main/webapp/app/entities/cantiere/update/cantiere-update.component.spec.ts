import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CantiereService } from '../service/cantiere.service';
import { ICantiere, Cantiere } from '../cantiere.model';

import { CantiereUpdateComponent } from './cantiere-update.component';

describe('Cantiere Management Update Component', () => {
  let comp: CantiereUpdateComponent;
  let fixture: ComponentFixture<CantiereUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let cantiereService: CantiereService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CantiereUpdateComponent],
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
      .overrideTemplate(CantiereUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CantiereUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    cantiereService = TestBed.inject(CantiereService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const cantiere: ICantiere = { id: 456 };

      activatedRoute.data = of({ cantiere });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(cantiere));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Cantiere>>();
      const cantiere = { id: 123 };
      jest.spyOn(cantiereService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cantiere });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cantiere }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(cantiereService.update).toHaveBeenCalledWith(cantiere);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Cantiere>>();
      const cantiere = new Cantiere();
      jest.spyOn(cantiereService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cantiere });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cantiere }));
      saveSubject.complete();

      // THEN
      expect(cantiereService.create).toHaveBeenCalledWith(cantiere);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Cantiere>>();
      const cantiere = { id: 123 };
      jest.spyOn(cantiereService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cantiere });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(cantiereService.update).toHaveBeenCalledWith(cantiere);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
