import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { UntypedFormBuilder, ReactiveFormsModule } from '@angular/forms';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { of } from 'rxjs';
import { StubUtilTests } from '../../../../common/stub-util-tests';
import { SessionService } from '../../../../../common/session.service';
import { TransactionsConsentReviewComponent } from './transactions-consent-review.component';
import { UpdateConsentAuthorizationService } from '../../../../../api';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('TransactionsConsentReviewComponent', () => {
  let component: TransactionsConsentReviewComponent;
  let fixture: ComponentFixture<TransactionsConsentReviewComponent>;
  let consentAuthorizationServiceSpy;
  let consentAuthorizationService: UpdateConsentAuthorizationService;

  const locationStub = {
    back: jasmine.createSpy('onBack')
  };

  beforeAll(() => (window.onbeforeunload = jasmine.createSpy()));

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [TransactionsConsentReviewComponent],
        imports: [RouterTestingModule, ReactiveFormsModule],
        providers: [
          SessionService,
          UpdateConsentAuthorizationService,
          UntypedFormBuilder,
          { provide: Location, useValue: locationStub },
          {
            provide: ActivatedRoute,
            useValue: { parent: { parent: { params: of({ authId: StubUtilTests.AUTH_ID }) } } }
          },
          provideHttpClient(withInterceptorsFromDi()),
          provideHttpClientTesting()
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionsConsentReviewComponent);
    component = fixture.componentInstance;
    component.ngOnInit();
    fixture.detectChanges();
    consentAuthorizationService = TestBed.inject(UpdateConsentAuthorizationService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should go back after back button is pressed', () => {
    component.onBack();
    const location = fixture.debugElement.injector.get(Location);
    expect(location.back).toHaveBeenCalled();
  });

  // FIXME Disabled as DateUtil.isDateNotInThePastValidator seem to cause 'undefined' error in control validation
  it('should confirm transaction when confirm button is pressed', () => {
    consentAuthorizationServiceSpy = spyOn(consentAuthorizationService, 'embeddedUsingPOST').and.returnValue(of());
    component.onConfirm();
    fixture.detectChanges();
    expect(consentAuthorizationServiceSpy).toHaveBeenCalled();
  });
});
