import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { StubUtilTests } from '../common/stub-util-tests';
import { ConsentAuthorizationService } from '../../api';
import { ScaSelectPageComponent } from './sca-select-page.component';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('ScaSelectPageComponent', () => {
  let component: ScaSelectPageComponent;
  let fixture: ComponentFixture<ScaSelectPageComponent>;
  let consentAuthorizationService: ConsentAuthorizationService;
  let consentAuthorizationServiceSpy;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ScaSelectPageComponent],
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
    fixture = TestBed.createComponent(ScaSelectPageComponent);
    component = fixture.componentInstance;
    consentAuthorizationService = fixture.debugElement.injector.get(ConsentAuthorizationService);
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
