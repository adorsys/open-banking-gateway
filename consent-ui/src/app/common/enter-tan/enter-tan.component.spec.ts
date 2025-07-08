import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';
import { expect } from '@jest/globals';

import { EnterTanComponent, ScaType } from './enter-tan.component';
import { StubUtilTests } from '../../ais/common/stub-util-tests';
import { UpdateConsentAuthorizationService } from '../../api';
import { HttpHeaders, HttpResponse, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { SessionService } from '../session.service';

describe('EnterTanComponent', () => {
  let component: EnterTanComponent;
  let fixture: ComponentFixture<EnterTanComponent>;
  let form;
  let updateConsentAuthorizationService;
  let updateConsentAuthorizationServiceSpy;

  const mockSessionService = {
    getRedirectCode: jest.fn().mockReturnValue(StubUtilTests.REDIRECT_ID),
    setRedirectCode: jest.fn()
  };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [EnterTanComponent],
        schemas: [NO_ERRORS_SCHEMA],
        imports: [ReactiveFormsModule],
        providers: [
          provideHttpClient(withInterceptorsFromDi()),
          provideHttpClientTesting(),
          { provide: SessionService, useValue: mockSessionService }
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(EnterTanComponent);
    component = fixture.componentInstance;

    component.authorizationSessionId = 'test-session-id';
    component.scaType = ScaType.EMAIL; // or any valid value

    updateConsentAuthorizationService = TestBed.inject(UpdateConsentAuthorizationService);
    updateConsentAuthorizationServiceSpy = jest
      .spyOn(updateConsentAuthorizationService, 'embeddedUsingPOST')
      .mockReturnValue(
        of(
          new HttpResponse({
            body: { challengeData: { image: '', data: [''] } },
            headers: new HttpHeaders({ 'X-XSRF-TOKEN': 'token-value' }),
            status: 200,
            statusText: 'OK'
          })
        )
      );

    fixture.detectChanges();
    form = component.reportScaResultForm;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be false if the form is invalid', () => {
    component.onSubmit();
    expect(component.reportScaResultForm.valid).toBe(false);
  });

  it('should be true if the form is valid', () => {
    form.get('tan').setValue(StubUtilTests.DUMMY_INPUT);
    component.onSubmit();
    expect(component.reportScaResultForm.valid).toBe(true);
  });

  it('should call method embeddedUsingPOST', () => {
    component.onSubmit();
    expect(updateConsentAuthorizationServiceSpy).toHaveBeenCalled();
  });

  it('should have been set redirectCode', () => {
    expect(component.redirectCode).toEqual(StubUtilTests.REDIRECT_ID);
  });
});
