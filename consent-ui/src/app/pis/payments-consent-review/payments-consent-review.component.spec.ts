import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { PaymentsConsentReviewComponent } from './payments-consent-review.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';
import { StubUtilTests } from '../../ais/common/stub-util-tests';
import { UpdateConsentAuthorizationService } from '../../api';
import { SessionService } from '../../common/session.service';

describe('PaymentsConsentReviewComponent', () => {
  let component: PaymentsConsentReviewComponent;
  let fixture: ComponentFixture<PaymentsConsentReviewComponent>;
  let updateConsentAuthorizationService: UpdateConsentAuthorizationService;
  let sessionService: SessionService;
  let updateConsentAuthorizationServiceSpy;

  beforeAll(() => (window.onbeforeunload = jasmine.createSpy()));

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [PaymentsConsentReviewComponent],
      imports: [RouterTestingModule, ReactiveFormsModule, HttpClientTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            parent: { parent: { params: of(convertToParamMap({ authId: StubUtilTests.AUTH_ID })) } }
          }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentsConsentReviewComponent);
    component = fixture.componentInstance;
    updateConsentAuthorizationService = TestBed.inject(UpdateConsentAuthorizationService);
    sessionService = TestBed.inject(SessionService);
    updateConsentAuthorizationServiceSpy = spyOn(
      updateConsentAuthorizationService,
      'embeddedUsingPOST'
    ).and.returnValue(of());
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call onBack', () => {
    const onDenySpy = spyOn(component, 'onBack').and.callThrough();
    component.onBack();
    expect(onDenySpy).toHaveBeenCalled();
  });

  it('should call onConfirm', () => {
    const onConfirmSpy = spyOn(component, 'onConfirm').and.callThrough();
    component.onConfirm();
    expect(onConfirmSpy).toHaveBeenCalled();
  });
});
