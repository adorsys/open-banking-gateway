import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { SelectScaComponent } from './select-sca.component';
import { StubUtilTests } from '../../ais/common/stub-util-tests';
import { AuthStateConsentAuthorizationService, UpdateConsentAuthorizationService } from '../../api';
import { SessionService } from '../session.service';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('SelectScaComponent', () => {
  let component: SelectScaComponent;
  let fixture: ComponentFixture<SelectScaComponent>;
  let form;
  let sessionService;
  let sessionServiceSpy;
  let updateConsentAuthorizationService;
  let updateConsentAuthorizationServiceSpy;
  let authStateConsentAuthorizationService;
  let authStateConsentAuthorizationServiceSpy;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [SelectScaComponent],
        imports: [ReactiveFormsModule],
        providers: [provideHttpClient(withInterceptorsFromDi()), provideHttpClientTesting()]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectScaComponent);
    component = fixture.componentInstance;
    sessionService = TestBed.inject(SessionService);
    updateConsentAuthorizationService = TestBed.inject(UpdateConsentAuthorizationService);
    authStateConsentAuthorizationService = TestBed.inject(AuthStateConsentAuthorizationService);
    sessionServiceSpy = spyOn(sessionService, 'getRedirectCode').and.returnValue(StubUtilTests.REDIRECT_ID);
    updateConsentAuthorizationServiceSpy = spyOn(
      updateConsentAuthorizationService,
      'embeddedUsingPOST'
    ).and.returnValue(of());
    authStateConsentAuthorizationServiceSpy = spyOn(
      authStateConsentAuthorizationService,
      'authUsingGET'
    ).and.returnValue(of());

    fixture.detectChanges();
    form = component.scaMethodForm;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be false if the scaMethodForm is not valid', () => {
    form.get('selectedMethodValue').setValue(null);
    component.onSubmit();
    expect(component.scaMethodForm.valid).toBe(false);
  });

  it('should be true if the scaMethodForm is valid', () => {
    form.get('selectedMethodValue').setValue(StubUtilTests.SCA_METHOD_VALUE);
    component.onSubmit();
    expect(component.scaMethodForm.valid).toBe(true);
  });
});
