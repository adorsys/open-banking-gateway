import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { of } from 'rxjs';

import { EnterTanComponent } from './enter-tan.component';
import { StubUtilTests } from '../../ais/common/stub-util-tests';
import { SessionService } from '../session.service';
import { UpdateConsentAuthorizationService } from '../../api';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('EnterTanComponent', () => {
  let component: EnterTanComponent;
  let fixture: ComponentFixture<EnterTanComponent>;
  let form;
  let sessionService;
  let sessionServiceSpy;
  let updateConsentAuthorizationService;
  let updateConsentAuthorizationServiceSpy;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EnterTanComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EnterTanComponent);
    component = fixture.componentInstance;
    sessionService = TestBed.get(SessionService);
    updateConsentAuthorizationService = TestBed.get(UpdateConsentAuthorizationService);
    sessionServiceSpy = spyOn(sessionService, 'getRedirectCode').and.returnValue(StubUtilTests.REDIRECT_ID);
    updateConsentAuthorizationServiceSpy = spyOn(
      updateConsentAuthorizationService,
      'embeddedUsingPOST'
    ).and.returnValue(of());

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
