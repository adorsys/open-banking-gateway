import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {AccountsConsentReviewComponent} from './accounts-consent-review.component';
import {RouterTestingModule} from '@angular/router/testing';
import {ReactiveFormsModule} from '@angular/forms';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRoute, convertToParamMap} from '@angular/router';
import {of} from 'rxjs';
import {StubUtilTests} from '../../../../common/stub-util-tests';
import {SessionService} from '../../../../../common/session.service';
import {ConsentAuthorizationService} from '../../../../../api';
import {Location} from '@angular/common';

describe('AccountsConsentReviewComponent', () => {
  let component: AccountsConsentReviewComponent;
  let fixture: ComponentFixture<AccountsConsentReviewComponent>;
  let consentAuthorizationService: ConsentAuthorizationService;

  const locationStub = {
    back: jasmine.createSpy('onBack')
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AccountsConsentReviewComponent],
      imports: [RouterTestingModule, ReactiveFormsModule, HttpClientTestingModule],
      providers: [
        SessionService,
        ConsentAuthorizationService,
        {provide: Location, useValue: locationStub},
        {
          provide: ActivatedRoute,
          useValue: {
            parent: {parent: {params: of(convertToParamMap({authId: StubUtilTests.AUTH_ID}))}}
          }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountsConsentReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    consentAuthorizationService = fixture.debugElement.injector.get(ConsentAuthorizationService);
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
    const consentAuthorizationServiceSpy = spyOn(consentAuthorizationService, 'embeddedUsingPOST').and.returnValue(of());
    component.onConfirm();
    fixture.detectChanges();
    expect(consentAuthorizationServiceSpy).toHaveBeenCalled();
  })

});
