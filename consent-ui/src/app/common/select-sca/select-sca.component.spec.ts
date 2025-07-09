import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { expect } from '@jest/globals';

import { SelectScaComponent } from './select-sca.component';
import { StubUtilTests } from '../../ais/common/stub-util-tests';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { AuthStateConsentAuthorizationService, UpdateConsentAuthorizationService } from '../../api';
import { SessionService } from '../session.service';

describe('SelectScaComponent', () => {
  let component: SelectScaComponent;
  let fixture: ComponentFixture<SelectScaComponent>;
  let form;

  const mockAuthStateConsentAuthorizationService = {
    authUsingGET: jest.fn().mockReturnValue(
      of(
        new HttpResponse({
          body: { scaMethods: [{ id: 'sms' }, { id: 'email' }] },
          headers: new HttpHeaders()
        })
      )
    )
  };

  const mockUpdateConsentAuthorizationService = {
    embeddedUsingPOST: jest.fn().mockReturnValue(of(new HttpResponse({ headers: new HttpHeaders() })))
  };

  const mockSessionService = {
    getRedirectCode: jest.fn().mockReturnValue('redirect-code'),
    setRedirectCode: jest.fn()
  };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [SelectScaComponent],
        imports: [ReactiveFormsModule],
        providers: [
          { provide: AuthStateConsentAuthorizationService, useValue: mockAuthStateConsentAuthorizationService },
          { provide: UpdateConsentAuthorizationService, useValue: mockUpdateConsentAuthorizationService },
          { provide: SessionService, useValue: mockSessionService }
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectScaComponent);
    component = fixture.componentInstance;
    component.authorizationSessionId = 'test-session';
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
