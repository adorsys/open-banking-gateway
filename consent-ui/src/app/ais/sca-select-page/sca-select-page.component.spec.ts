import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ScaSelectPageComponent } from './sca-select-page.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { StubUtilTests } from '../common/stub-util-tests';
import { ConsentAuthorizationService } from '../../api';
import { of } from 'rxjs';

describe('ScaSelectPageComponent', () => {
  let component: ScaSelectPageComponent;
  let fixture: ComponentFixture<ScaSelectPageComponent>;
  let consentAuthorizationService: ConsentAuthorizationService;
  let consentAuthorizationServiceSpy;
  let form;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ScaSelectPageComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule, RouterTestingModule],
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
    form = component.scaMethodForm;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be true if the scaMethodForm is invalid', () => {
    consentAuthorizationServiceSpy = spyOn(consentAuthorizationService, 'embeddedUsingPOST').and.returnValue(of());
    form.get('selectedMethodValue').setValue('');
    component.onSubmit();
    expect(component.scaMethodForm.invalid).toBe(true);
  });

  it('should be true if the scaMethodForm is valid', () => {
    consentAuthorizationServiceSpy = spyOn(consentAuthorizationService, 'embeddedUsingPOST').and.returnValue(of());
    form.get('selectedMethodValue').setValue(StubUtilTests.SCA_METHOD_VALUE);
    component.onSubmit();
    expect(component.scaMethodForm.valid).toBe(true);
  });
});
