import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { FattureAttivoService } from '../service/fatture-attivo.service';

import { FattureAttivoComponent } from './fatture-attivo.component';

describe('FattureAttivo Management Component', () => {
  let comp: FattureAttivoComponent;
  let fixture: ComponentFixture<FattureAttivoComponent>;
  let service: FattureAttivoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'fatture-attivo', component: FattureAttivoComponent }]), HttpClientTestingModule],
      declarations: [FattureAttivoComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(FattureAttivoComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FattureAttivoComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(FattureAttivoService);

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
    expect(comp.fattureAttivos?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
