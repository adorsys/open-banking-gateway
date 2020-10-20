import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';

import { RedirectPageComponent } from './redirect-page.component';
import { ConsentAuthorizationService } from '../services/consent-authorization.service';
import { RedirectStruct } from './redirect-struct';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('RedirectPageComponent', () => {
  let component: RedirectPageComponent;
  let fixture: ComponentFixture<RedirectPageComponent>;

  beforeAll(() => (window.onbeforeunload = jasmine.createSpy()));

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      declarations: [RedirectPageComponent],
      schemas: [NO_ERRORS_SCHEMA]
    })
      .overrideComponent(RedirectPageComponent, {
        set: {
          providers: [
            {
              provide: ActivatedRoute,
              useValue: {
                paramMap: {
                  subscribe(location: string): string {
                    const r: RedirectStruct = new RedirectStruct();
                    r.bankName = 'peter';
                    r.redirectUrl = 'redirectUrl';
                    return JSON.stringify(r);
                  }
                }
              }
            },
            {
              provide: ConsentAuthorizationService
            }
          ]
        }
      })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RedirectPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
