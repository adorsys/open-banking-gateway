import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportScaResultComponent } from './sca-result-page.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { StubUtilTests } from '../common/stub-util-tests';
import { ConsentAuthorizationService } from '../../api';
import { of } from 'rxjs';

describe('ReportScaResultComponent', () => {
  let component: ReportScaResultComponent;
  let fixture: ComponentFixture<ReportScaResultComponent>;
  let consentAuthorizationService: ConsentAuthorizationService;
  let consentAuthorizationServiceSpy;
  let form;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ReportScaResultComponent],
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
    fixture = TestBed.createComponent(ReportScaResultComponent);
    component = fixture.componentInstance;
    consentAuthorizationService = fixture.debugElement.injector.get(ConsentAuthorizationService);
    fixture.detectChanges();
    form = component.reportScaResultForm;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be true if the reportScaResultForm is invalid', () => {
    consentAuthorizationServiceSpy = spyOn(consentAuthorizationService, 'embeddedUsingPOST').and.returnValue(of());
    form.get('tan').setValue('');
    component.submit();
    expect(component.reportScaResultForm.invalid).toBe(true);
  });

  it('should be true if the reportScaResultForm is valid', () => {
    consentAuthorizationServiceSpy = spyOn(consentAuthorizationService, 'embeddedUsingPOST').and.returnValue(of());
    form.get('tan').setValue(StubUtilTests.SCA_METHOD_VALUE);
    component.submit();
    expect(component.reportScaResultForm.valid).toBe(true);
  });
});
