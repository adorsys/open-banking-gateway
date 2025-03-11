import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';

import { EnterTanComponent } from './enter-tan.component';
import { StubUtilTests } from '../../ais/common/stub-util-tests';
import { SessionService } from '../session.service';
import { UpdateConsentAuthorizationService } from '../../api';
import { AuthStateConsentAuthorizationService } from '../../api';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('EnterTanComponent', () => {
  let component: EnterTanComponent;
  let fixture: ComponentFixture<EnterTanComponent>;
  let form;
  let sessionService;
  let sessionServiceSpy;
  let updateConsentAuthorizationService;
  let updateConsentAuthorizationServiceSpy;
  let consentAuthorizationService;
  let consentAuthorizationServiceSpy;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [EnterTanComponent],
        schemas: [NO_ERRORS_SCHEMA],
        imports: [ReactiveFormsModule],
        providers: [provideHttpClient(withInterceptorsFromDi()), provideHttpClientTesting()]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(EnterTanComponent);
    component = fixture.componentInstance;
    sessionService = TestBed.inject(SessionService);
    updateConsentAuthorizationService = TestBed.inject(UpdateConsentAuthorizationService);
    consentAuthorizationService = TestBed.inject(AuthStateConsentAuthorizationService);
    sessionServiceSpy = spyOn(sessionService, 'getRedirectCode').and.returnValue(StubUtilTests.REDIRECT_ID);
    updateConsentAuthorizationServiceSpy = spyOn(
      updateConsentAuthorizationService,
      'embeddedUsingPOST'
    ).and.returnValue(of());
    consentAuthorizationServiceSpy = spyOn(consentAuthorizationService, 'authUsingGET').and.returnValue(of());

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
