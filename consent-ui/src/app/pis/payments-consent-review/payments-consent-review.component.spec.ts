import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { Location } from '@angular/common';
import { PaymentsConsentReviewComponent } from './payments-consent-review.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { StubUtilTests } from '../../ais/common/stub-util-tests';
import { HttpHeaders, HttpResponse, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { expect } from '@jest/globals';
import { SessionService } from '../../common/session.service';
import { PaymentUtil } from '../common/payment-util';
import { PsuAuthRequest, UpdateConsentAuthorizationService } from '../../api';
import { StubUtil } from '../../common/utils/stub-util';
import 'zone.js';
import 'zone.js/testing';

type PaymentsConsentReviewComponentWithPrivateMembers = PaymentsConsentReviewComponent & {
  authorizationId: string;
};

describe('PaymentsConsentReviewComponent', () => {
  let component: PaymentsConsentReviewComponent;
  let fixture: ComponentFixture<PaymentsConsentReviewComponent>;

  beforeAll(() => (window.onbeforeunload = jest.fn()));

  const mockSessionService = {
    getRedirectCode: jest.fn().mockReturnValue(StubUtilTests.REDIRECT_ID),
    setRedirectCode: jest.fn(),
    getBankName: jest.fn().mockReturnValue(StubUtil.ASPSP_NAME),
    getFintechName: jest.fn().mockReturnValue(StubUtil.FINTECH_NAME),
    getPaymentState: jest.fn().mockReturnValue({ singlePayment: { paymentId: 'test-payment-id' } })
  };

  const mockLocation = {
    back: jest.fn()
  };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [PaymentsConsentReviewComponent],
        imports: [RouterTestingModule, ReactiveFormsModule],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: {
              parent: { parent: { params: of({ authId: StubUtilTests.AUTH_ID }) } }
            }
          },
          provideHttpClient(withInterceptorsFromDi()),
          provideHttpClientTesting(),
          { provide: SessionService, useValue: mockSessionService },
          { provide: Location, useValue: mockLocation }
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentsConsentReviewComponent);
    component = fixture.componentInstance;

    jest.spyOn(PaymentUtil, 'getOrDefault').mockReturnValue({
      extras: { foo: 'bar' },
      payment: { paymentId: 'test-payment-id' }
    });

    (component as PaymentsConsentReviewComponentWithPrivateMembers).authorizationId = StubUtilTests.AUTH_ID;

    const updateConsentAuthorizationService = TestBed.inject(UpdateConsentAuthorizationService);
    jest.spyOn(updateConsentAuthorizationService, 'embeddedUsingPOST').mockReturnValue(
      of(
        new HttpResponse({
          headers: new HttpHeaders({
            'X-XSRF-TOKEN': 'test-token',
            Location: 'http://example.com/redirect'
          }),
          status: 200,
          statusText: 'OK'
        })
      )
    );

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call onBack', () => {
    const onBackSpy = jest.spyOn(component, 'onBack');
    component.onBack();
    expect(onBackSpy).toHaveBeenCalled();
    expect(mockLocation.back).toHaveBeenCalled();
  });

  it('should call onConfirm', () => {
    const updateConsentAuthorizationService = TestBed.inject(UpdateConsentAuthorizationService);
    const embeddedUsingPOSTSpy = jest.spyOn(updateConsentAuthorizationService, 'embeddedUsingPOST');

    const onConfirmSpy = jest.spyOn(component, 'onConfirm');

    fixture.detectChanges();

    component.onConfirm();

    expect(onConfirmSpy).toHaveBeenCalled();
    expect(embeddedUsingPOSTSpy).toHaveBeenCalledWith(
      StubUtilTests.AUTH_ID,
      StubUtil.X_REQUEST_ID,
      StubUtilTests.REDIRECT_ID,
      { extras: { foo: 'bar' } } as PsuAuthRequest,
      'response'
    );
    expect(mockSessionService.setRedirectCode).toHaveBeenCalledWith(StubUtilTests.AUTH_ID, 'test-token');
  });
});
