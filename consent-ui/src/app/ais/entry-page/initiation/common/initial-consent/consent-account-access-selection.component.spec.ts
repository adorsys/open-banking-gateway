import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { Access, ConsentAccountAccessSelectionComponent } from './consent-account-access-selection.component';
import { ReactiveFormsModule } from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { SessionService } from '../../../../../common/session.service';
import { AccountAccessLevel } from '../../../../common/dto/ais-consent';
import { AuthConsentState } from '../../../../common/dto/auth-state';
import { AccountsConsentReviewComponent } from '../../accounts/accounts-consent-review/accounts-consent-review.component';
import { DedicatedAccessComponent } from '../dedicated-access/dedicated-access.component';

describe('ConsentAccountAccessSelectionComponent', () => {
  let component: ConsentAccountAccessSelectionComponent;
  let fixture: ComponentFixture<ConsentAccountAccessSelectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConsentAccountAccessSelectionComponent],
      imports: [ReactiveFormsModule, RouterTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            parent: { parent: { params: of({ authId: 'AUTH-ID' }) } },
            snapshot: {}
          }
        },
        {
          provide: SessionService,
          useValue: {
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
    fixture = TestBed.createComponent(ConsentAccountAccessSelectionComponent);
    component = fixture.componentInstance;
    component.consentReviewPage = AccountsConsentReviewComponent.ROUTE;
    component.dedicatedConsentPage = DedicatedAccessComponent.ROUTE;
    component.accountAccesses = [new Access(AccountAccessLevel.ALL_ACCOUNTS, 'access to all accounts')];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
