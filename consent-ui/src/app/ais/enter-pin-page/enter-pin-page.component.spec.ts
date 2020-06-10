import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { EnterPinPageComponent } from './enter-pin-page.component';
import { StubUtilTests } from '../common/stub-util-tests';
import { SessionService } from '../../common/session.service';
import { UpdateConsentAuthorizationService } from '../../api';
import any = jasmine.any;

describe('EnterPinPageComponent', () => {
  let component: EnterPinPageComponent;
  let fixture: ComponentFixture<EnterPinPageComponent>;
  let sessionService: SessionService;
  let consentAuthService: UpdateConsentAuthorizationService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EnterPinPageComponent],
      schemas: [NO_ERRORS_SCHEMA],
      imports: [ReactiveFormsModule, HttpClientTestingModule, RouterTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { queryParamMap: convertToParamMap({}) },
            parent: { snapshot: { paramMap: convertToParamMap({ authId: StubUtilTests.AUTH_ID }) } }
          }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EnterPinPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    sessionService = TestBed.get(SessionService);
    consentAuthService = TestBed.get(UpdateConsentAuthorizationService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should retrieve redirect code on ngOnInit', () => {
    const sessionServiceSpy = spyOn(sessionService, 'getRedirectCode').and.returnValue(StubUtilTests.REDIRECT_ID);
    component.ngOnInit();
    expect(sessionServiceSpy).toHaveBeenCalledWith(StubUtilTests.AUTH_ID);
  });

  it('should call consent auth service on submit', () => {
    spyOn(sessionService, 'getRedirectCode').and.returnValue(StubUtilTests.REDIRECT_ID);
    const consentAuthServiceSpy = spyOn(consentAuthService, 'embeddedUsingPOST').and.callThrough();
    fixture.detectChanges();

    component.submit(StubUtilTests.DUMMY_INPUT);

    expect(consentAuthServiceSpy).toHaveBeenCalledWith(
      StubUtilTests.AUTH_ID,
      any(String), // these values are stubbed in component
      any(String), // these values are stubbed in component
      StubUtilTests.REDIRECT_ID,
      { scaAuthenticationData: { PSU_PASSWORD: StubUtilTests.DUMMY_INPUT } },
      'response'
    );
  });
});
