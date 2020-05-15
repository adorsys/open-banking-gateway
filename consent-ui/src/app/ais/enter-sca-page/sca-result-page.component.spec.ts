import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { EnterScaComponent } from './sca-result-page.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { StubUtilTests } from '../common/stub-util-tests';
import { ConsentAuthorizationService } from '../../api';
import { of } from 'rxjs';

describe('ReportScaResultComponent', () => {
  let component: EnterScaComponent;
  let fixture: ComponentFixture<EnterScaComponent>;
  let consentAuthorizationService: ConsentAuthorizationService;
  let consentAuthorizationServiceSpy;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EnterScaComponent],
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
    fixture = TestBed.createComponent(EnterScaComponent);
    component = fixture.componentInstance;
    consentAuthorizationService = fixture.debugElement.injector.get(ConsentAuthorizationService);
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
