import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountsConsentReviewComponent } from './accounts-consent-review.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';

describe('AccountsConsentReviewComponent', () => {
  let component: AccountsConsentReviewComponent;
  let fixture: ComponentFixture<AccountsConsentReviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AccountsConsentReviewComponent],
      imports: [RouterTestingModule, ReactiveFormsModule, HttpClientTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            parent: { parent: { params: of(convertToParamMap({ authId: 'AUTH-ID' })) } }
          }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountsConsentReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
