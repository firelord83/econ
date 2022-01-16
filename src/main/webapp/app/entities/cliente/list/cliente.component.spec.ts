import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ClienteService } from '../service/cliente.service';

import { ClienteComponent } from './cliente.component';

describe('Cliente Management Component', () => {
  let comp: ClienteComponent;
  let fixture: ComponentFixture<ClienteComponent>;
  let service: ClienteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'cliente', component: ClienteComponent }]), HttpClientTestingModule],
      declarations: [ClienteComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(ClienteComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ClienteComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ClienteService);

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
    expect(comp.clientes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
