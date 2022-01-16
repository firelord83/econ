import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { FornitoreService } from '../service/fornitore.service';

import { FornitoreComponent } from './fornitore.component';

describe('Fornitore Management Component', () => {
  let comp: FornitoreComponent;
  let fixture: ComponentFixture<FornitoreComponent>;
  let service: FornitoreService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'fornitore', component: FornitoreComponent }]), HttpClientTestingModule],
      declarations: [FornitoreComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(FornitoreComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FornitoreComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(FornitoreService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.fornitores?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
