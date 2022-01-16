import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CantiereDetailComponent } from './cantiere-detail.component';

describe('Cantiere Management Detail Component', () => {
  let comp: CantiereDetailComponent;
  let fixture: ComponentFixture<CantiereDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CantiereDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ cantiere: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CantiereDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CantiereDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load cantiere on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.cantiere).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
