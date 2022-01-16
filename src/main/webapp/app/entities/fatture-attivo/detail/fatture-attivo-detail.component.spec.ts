import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FattureAttivoDetailComponent } from './fatture-attivo-detail.component';

describe('FattureAttivo Management Detail Component', () => {
  let comp: FattureAttivoDetailComponent;
  let fixture: ComponentFixture<FattureAttivoDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FattureAttivoDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ fattureAttivo: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(FattureAttivoDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(FattureAttivoDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load fattureAttivo on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.fattureAttivo).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
