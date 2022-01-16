import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FornitoreService } from '../service/fornitore.service';
import { IFornitore, Fornitore } from '../fornitore.model';

import { FornitoreUpdateComponent } from './fornitore-update.component';

describe('Fornitore Management Update Component', () => {
  let comp: FornitoreUpdateComponent;
  let fixture: ComponentFixture<FornitoreUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let fornitoreService: FornitoreService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [FornitoreUpdateComponent],
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
      .overrideTemplate(FornitoreUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FornitoreUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fornitoreService = TestBed.inject(FornitoreService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const fornitore: IFornitore = { id: 456 };

      activatedRoute.data = of({ fornitore });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(fornitore));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Fornitore>>();
      const fornitore = { id: 123 };
      jest.spyOn(fornitoreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fornitore });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: fornitore }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(fornitoreService.update).toHaveBeenCalledWith(fornitore);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Fornitore>>();
      const fornitore = new Fornitore();
      jest.spyOn(fornitoreService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fornitore });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: fornitore }));
      saveSubject.complete();

      // THEN
      expect(fornitoreService.create).toHaveBeenCalledWith(fornitore);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Fornitore>>();
      const fornitore = { id: 123 };
      jest.spyOn(fornitoreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fornitore });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(fornitoreService.update).toHaveBeenCalledWith(fornitore);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
