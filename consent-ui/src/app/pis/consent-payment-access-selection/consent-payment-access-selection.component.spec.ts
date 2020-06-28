import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';

import { StubUtilTests } from '../../ais/common/stub-util-tests';
import { SessionService } from '../../common/session.service';
import { AisConsentToGrant } from '../../ais/common/dto/ais-consent';
import { AuthConsentState } from '../../ais/common/dto/auth-state';
import { PaymentsConsentReviewComponent } from '../payments-consent-review/payments-consent-review.component';
import { ConsentPaymentAccessSelectionComponent } from './consent-payment-access-selection.component';

describe('ConsentPaymentAccessSelectionComponent', () => {
  let component: ConsentPaymentAccessSelectionComponent;
  let fixture: ComponentFixture<ConsentPaymentAccessSelectionComponent>;
  let sessionService;

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
            getPaymentState: () => new AisConsentToGrant(),
            hasPisViolation: () => false,
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
    component.paymentReviewPage = PaymentsConsentReviewComponent.ROUTE;
    sessionService = TestBed.get(SessionService);
    spyOn(component, 'hasGeneralViolations').and.returnValue(false);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
