import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsentPaymentAccessSelectionComponent, Access } from './consent-payment-access-selection.component';
import {ReactiveFormsModule} from "@angular/forms";
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {ActivatedRoute} from "@angular/router";
import {of} from "rxjs";
import {StubUtilTests} from "../../ais/common/stub-util-tests";
import {SessionService} from "../../common/session.service";
import {AccountAccessLevel, AisConsentToGrant} from "../../ais/common/dto/ais-consent";
import {AuthConsentState} from "../../ais/common/dto/auth-state";
import {CUSTOM_ELEMENTS_SCHEMA} from "@angular/core";
import {DedicatedAccessComponent} from "../../ais/entry-page/initiation/common/dedicated-access/dedicated-access.component";
import {PaymentsConsentReviewComponent} from "../payments-consent-review/payments-consent-review.component";

describe('ConsentPaymentAccessSelectionComponent', () => {
  let component: ConsentPaymentAccessSelectionComponent;
  let fixture: ComponentFixture<ConsentPaymentAccessSelectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConsentPaymentAccessSelectionComponent],
      imports: [ReactiveFormsModule, RouterTestingModule, HttpClientTestingModule],
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
            hasAisViolation: () => false,
            hasGeneralViolation: () => false,
            getConsentState: () => new AuthConsentState([])
          }
        }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsentPaymentAccessSelectionComponent);
    component = fixture.componentInstance;
    component.consentReviewPage = PaymentsConsentReviewComponent.ROUTE;
    component.dedicatedConsentPage = DedicatedAccessComponent.ROUTE;
    component.accountAccesses = [new Access(AccountAccessLevel.ALL_ACCOUNTS, 'access to all accounts')];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
