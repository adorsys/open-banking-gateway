import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectScaPageComponent } from './select-sca-page.component';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import {UpdateConsentAuthorizationService} from "../../api";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {ActivatedRoute, convertToParamMap} from "@angular/router";
import {StubUtilTests} from "../../ais/common/stub-util-tests";
import {of} from "rxjs";

describe('SelectScaPageComponent', () => {
  let component: SelectScaPageComponent;
  let fixture: ComponentFixture<SelectScaPageComponent>;
  let consentAuthorizationService: UpdateConsentAuthorizationService;
  let consentAuthorizationServiceSpy;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SelectScaPageComponent],
      schemas: [NO_ERRORS_SCHEMA],
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            parent: {
              snapshot: {
                paramMap: convertToParamMap({
                  authId: StubUtilTests.AUTH_ID
                })
              }
            }
          }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectScaPageComponent);
    component = fixture.componentInstance;
    consentAuthorizationService = fixture.debugElement.injector.get(UpdateConsentAuthorizationService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call method embeddedUsingPOST', () => {
    consentAuthorizationServiceSpy = spyOn(consentAuthorizationService, 'embeddedUsingPOST').and.returnValue(of());
    component.onSubmit(StubUtilTests.SCA_METHOD_VALUE);
    expect(consentAuthorizationServiceSpy).toHaveBeenCalled();
  });
});
