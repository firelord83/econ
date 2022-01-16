import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FatturePassivoDetailComponent } from './fatture-passivo-detail.component';

describe('FatturePassivo Management Detail Component', () => {
  let comp: FatturePassivoDetailComponent;
  let fixture: ComponentFixture<FatturePassivoDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FatturePassivoDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ fatturePassivo: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(FatturePassivoDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(FatturePassivoDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load fatturePassivo on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.fatturePassivo).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
