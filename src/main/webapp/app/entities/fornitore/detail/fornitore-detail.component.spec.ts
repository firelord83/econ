import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FornitoreDetailComponent } from './fornitore-detail.component';

describe('Fornitore Management Detail Component', () => {
  let comp: FornitoreDetailComponent;
  let fixture: ComponentFixture<FornitoreDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FornitoreDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ fornitore: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(FornitoreDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(FornitoreDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load fornitore on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.fornitore).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
