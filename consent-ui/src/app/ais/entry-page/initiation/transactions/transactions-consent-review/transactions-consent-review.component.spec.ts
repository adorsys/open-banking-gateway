import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { of } from 'rxjs';
import { StubUtilTests } from '../../../../common/stub-util-tests';
import { SessionService } from '../../../../../common/session.service';
import { TransactionsConsentReviewComponent } from './transactions-consent-review.component';
import { UpdateConsentAuthorizationService } from '../../../../../api';

describe('TransactionsConsentReviewComponent', () => {
  let component: TransactionsConsentReviewComponent;
  let fixture: ComponentFixture<TransactionsConsentReviewComponent>;
  let consentAuthorizationServiceSpy;
  let consentAuthorizationService: UpdateConsentAuthorizationService;

  const locationStub = {
    back: jasmine.createSpy('onBack')
  };

  beforeAll(() => (window.onbeforeunload = jasmine.createSpy()));

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TransactionsConsentReviewComponent],
      imports: [RouterTestingModule, ReactiveFormsModule, HttpClientTestingModule],
      providers: [
        SessionService,
        UpdateConsentAuthorizationService,
        FormBuilder,
        { provide: Location, useValue: locationStub },
        {
          provide: ActivatedRoute,
          useValue: { parent: { parent: { params: of({ authId: StubUtilTests.AUTH_ID }) } } }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionsConsentReviewComponent);
    component = fixture.componentInstance;
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

  it('should confirm transaction when confirm button is pressed', () => {
    consentAuthorizationServiceSpy = spyOn(consentAuthorizationService, 'embeddedUsingPOST').and.returnValue(of());
    component.onConfirm();
    fixture.detectChanges();
    expect(consentAuthorizationServiceSpy).toHaveBeenCalled();
  });
});
