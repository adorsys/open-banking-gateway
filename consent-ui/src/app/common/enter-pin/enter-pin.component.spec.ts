import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { expect } from '@jest/globals';

import { EnterPinComponent } from './enter-pin.component';
import { StubUtilTests } from '../../ais/common/stub-util-tests';
import { UpdateConsentAuthorizationService } from '../../api';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { SessionService } from '../session.service';

describe('EnterPinComponent', () => {
  let component: EnterPinComponent;
  let fixture: ComponentFixture<EnterPinComponent>;
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
        declarations: [EnterPinComponent],
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
    fixture = TestBed.createComponent(EnterPinComponent);
    component = fixture.componentInstance;
    updateConsentAuthorizationService = TestBed.inject(UpdateConsentAuthorizationService);
    updateConsentAuthorizationServiceSpy = jest
      .spyOn(updateConsentAuthorizationService, 'embeddedUsingPOST')
      .mockReturnValue(of());
    fixture.detectChanges();
    form = component.pinForm;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be false if the form is invalid', () => {
    component.onSubmit();
    expect(component.pinForm.valid).toBe(false);
  });

  it('should be true if the form is valid', () => {
    form.get('pin').setValue(StubUtilTests.DUMMY_INPUT);
    component.onSubmit();
    expect(component.pinForm.valid).toBe(true);
  });

  it('should call method embeddedUsingPOST', () => {
    component.onSubmit();
    expect(updateConsentAuthorizationServiceSpy).toHaveBeenCalled();
  });

  it('should have been set redirectCode', () => {
    expect(component.redirectCode).toEqual(StubUtilTests.REDIRECT_ID);
  });
});
