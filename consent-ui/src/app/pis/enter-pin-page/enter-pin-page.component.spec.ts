import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { EnterPinPageComponent } from './enter-pin-page.component';
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { StubUtilTests } from '../../ais/common/stub-util-tests';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { expect } from '@jest/globals';

describe('EnterPinPageComponent', () => {
  let component: EnterPinPageComponent;
  let fixture: ComponentFixture<EnterPinPageComponent>;

  beforeAll(() => (window.onbeforeunload = jest.fn()));

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [EnterPinPageComponent],
        schemas: [NO_ERRORS_SCHEMA],
        imports: [ReactiveFormsModule, RouterTestingModule],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: {
              snapshot: { queryParamMap: convertToParamMap({}) },
              parent: { snapshot: { paramMap: convertToParamMap({ authId: StubUtilTests.AUTH_ID }) } }
            }
          },
          provideHttpClient(withInterceptorsFromDi()),
          provideHttpClientTesting()
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(EnterPinPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
