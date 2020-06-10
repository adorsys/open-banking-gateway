import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { EnterTanPageComponent } from './enter-tan-page.component';
import { UpdateConsentAuthorizationService } from '../../api';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { StubUtilTests } from '../../ais/common/stub-util-tests';
import { of } from 'rxjs';

describe('EnterTanPageComponent', () => {
  let component: EnterTanPageComponent;
  let fixture: ComponentFixture<EnterTanPageComponent>;
  let consentAuthorizationService: UpdateConsentAuthorizationService;
  let consentAuthorizationServiceSpy;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EnterTanPageComponent],
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
    fixture = TestBed.createComponent(EnterTanPageComponent);
    component = fixture.componentInstance;
    consentAuthorizationService = fixture.debugElement.injector.get(UpdateConsentAuthorizationService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call method embeddedUsingPOST', () => {
    consentAuthorizationServiceSpy = spyOn(consentAuthorizationService, 'embeddedUsingPOST').and.returnValue(of());
    component.onSubmit(StubUtilTests.DUMMY_INPUT);
    expect(consentAuthorizationServiceSpy).toHaveBeenCalled();
  });
});
