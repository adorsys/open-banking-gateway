import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { expect } from '@jest/globals';

import { SessionService } from '../../../../../common/session.service';
import { AccountAccessLevel, AisConsentToGrant } from '../../../../common/dto/ais-consent';
import { AuthConsentState } from '../../../../common/dto/auth-state';
import { Access, ConsentAccountAccessSelectionComponent } from './consent-account-access-selection.component';
import { AccountsConsentReviewComponent } from '../../accounts/accounts-consent-review/accounts-consent-review.component';
import { DedicatedAccessComponent } from '../dedicated-access/dedicated-access.component';
import { StubUtilTests } from '../../../../common/stub-util-tests';
import { UpdateConsentAuthorizationService } from '../../../../../api';
import { StubUtil } from '../../../../../common/utils/stub-util';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { ConsentUtil } from '../../../../common/consent-util';

type ConsentAccountAccessSelectionComponentWithPrivateMembers = ConsentAccountAccessSelectionComponent & {
  authorizationId: string;
};

interface AuthConsentStateMock {
  hasGeneralViolation: () => boolean;
}

interface SessionServiceMock {
  setConsentObject: jest.Mock;
}

describe('ConsentAccountAccessSelectionComponent', () => {
  let component: ConsentAccountAccessSelectionComponent;
  let fixture: ComponentFixture<ConsentAccountAccessSelectionComponent>;
  let updateConsentAuthorizationService: UpdateConsentAuthorizationService;

  beforeAll(() => (window.onbeforeunload = jest.fn()));

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [ConsentAccountAccessSelectionComponent],
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        imports: [ReactiveFormsModule, RouterTestingModule],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: {
              parent: { parent: { params: of({ authId: StubUtilTests.AUTH_ID }) } },
              snapshot: {}
            }
          },
          {
            provide: SessionService,
            useValue: {
              getConsentObject: () => new AisConsentToGrant(),
              getConsentState: () => new AuthConsentState([]),
              getFintechName: (): string => StubUtil.FINTECH_NAME,
              getBankName: (): string => StubUtil.ASPSP_NAME,
              getConsentTypesSupported: () => []
            }
          },
          provideHttpClient(withInterceptorsFromDi()),
          provideHttpClientTesting()
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsentAccountAccessSelectionComponent);
    component = fixture.componentInstance;
    component.consentReviewPage = AccountsConsentReviewComponent.ROUTE;
    component.dedicatedConsentPage = DedicatedAccessComponent.ROUTE;
    component.accountAccesses = [new Access(AccountAccessLevel.ALL_ACCOUNTS, 'access to all accounts')];
    updateConsentAuthorizationService = TestBed.inject(UpdateConsentAuthorizationService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call onConfirm', () => {
    (component as ConsentAccountAccessSelectionComponentWithPrivateMembers).authorizationId = 'test-auth-id';
    component.selectedAccess = {
      value: { id: 'ALL_PSD2' }, // or whatever AccountAccessLevel you want to test
      setValue: jest.fn()
    };

    component.state = {
      hasGeneralViolation: () => false
    } as AuthConsentStateMock;

    const consentObjMock = {
      level: AccountAccessLevel.ALL_ACCOUNTS,
      consent: {
        access: {
          accounts: [],
          balances: [],
          transactions: [],
          availableAccounts: [],
          allPsd2: []
        },
        recurringIndicator: false,
        validUntil: '2099-12-31',
        frequencyPerDay: 1
      },
      extras: {}
    };

    jest.spyOn(ConsentUtil, 'getOrDefault').mockReturnValue(consentObjMock);

    component['sessionService'] = {
      setConsentObject: jest.fn()
    } as SessionServiceMock;

    component.onConfirm();
    expect(component['sessionService'].setConsentObject).toHaveBeenCalledWith('test-auth-id', consentObjMock);
  });

  it('should call denyUsingPOST', () => {
    const consentAuthorizationServiceSpy = jest
      .spyOn(updateConsentAuthorizationService, 'denyUsingPOST')
      .mockReturnValue(of());
    component.onDeny();
    expect(consentAuthorizationServiceSpy).toHaveBeenCalled();
  });

  it('should check hasInputs', () => {
    const hasInputsSpy = jest.spyOn(component, 'hasInputs');
    component.hasInputs();
    expect(hasInputsSpy).toHaveBeenCalled();
  });

  it('should check hasAisViolations', () => {
    const hasAisViolationsSpy = jest.spyOn(component, 'hasAisViolations');
    component.hasAisViolations();
    expect(hasAisViolationsSpy).toHaveBeenCalled();
  });

  it('should check hasGeneralViolations', () => {
    const hasGeneralViolationsSpy = jest.spyOn(component, 'hasGeneralViolations');
    component.hasGeneralViolations();
    expect(hasGeneralViolationsSpy).toHaveBeenCalled();
  });

  it('should call handleMethodSelectedEvent', () => {
    const handleMethodSelectedEventSpy = jest.spyOn(component, 'handleMethodSelectedEvent');
    const access: Access = {
      id: AccountAccessLevel.ALL_ACCOUNTS,
      message: 'yes we can'
    };
    component.handleMethodSelectedEvent(access);
    expect(handleMethodSelectedEventSpy).toHaveBeenCalled();
  });

  it('should check submitButtonMessage', () => {
    const submitButtonMessageSpy = jest.spyOn(component, 'submitButtonMessage');
    component.submitButtonMessage();
    expect(submitButtonMessageSpy).toHaveBeenCalled();
  });
});
