import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { of } from 'rxjs';
import { StubUtilTests } from '../common/stub-util-tests';
import { ToAspspRedirectionComponent } from './to-aspsp-redirection.component';

describe('ToAspspRedirectionComponent', () => {
  let component: ToAspspRedirectionComponent;
  let fixture: ComponentFixture<ToAspspRedirectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ToAspspRedirectionComponent],
      schemas: [NO_ERRORS_SCHEMA],
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            parent: { params: of({ authId: StubUtilTests.AUTH_ID }) }
          }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ToAspspRedirectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
