import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { AccountsConsentReviewComponent } from './accounts-consent-review.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';
import { StubUtilTests } from '../../../../common/stub-util-tests';
import { SessionService } from '../../../../../common/session.service';
import { Location } from '@angular/common';
import { UpdateConsentAuthorizationService } from '../../../../../api';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { expect } from '@jest/globals';

describe('AccountsConsentReviewComponent', () => {
  let component: AccountsConsentReviewComponent;
  let fixture: ComponentFixture<AccountsConsentReviewComponent>;
  let consentAuthorizationService: UpdateConsentAuthorizationService;

  const locationStub = {
    back: jest.fn().mockName('onBack')
  };

  beforeAll(() => (window.onbeforeunload = jest.fn()));

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [AccountsConsentReviewComponent],
        imports: [RouterTestingModule, ReactiveFormsModule],
        providers: [
          SessionService,
          UpdateConsentAuthorizationService,
          { provide: Location, useValue: locationStub },
          {
            provide: ActivatedRoute,
            useValue: {
              parent: { parent: { params: of(convertToParamMap({ authId: StubUtilTests.AUTH_ID })) } }
            }
          },
          provideHttpClient(withInterceptorsFromDi()),
          provideHttpClientTesting()
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountsConsentReviewComponent);
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

  it('should call onConfirm', () => {
    const consentAuthorizationServiceSpy = jest
      .spyOn(consentAuthorizationService, 'embeddedUsingPOST')
      .mockReturnValue(of());
    component.onConfirm();
    fixture.detectChanges();
    expect(consentAuthorizationServiceSpy).toHaveBeenCalled();
  });
});
