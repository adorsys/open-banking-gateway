import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountsConsentReviewComponent } from './accounts-consent-review.component';

describe('AccountsConsentReviewComponent', () => {
  let component: AccountsConsentReviewComponent;
  let fixture: ComponentFixture<AccountsConsentReviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AccountsConsentReviewComponent ]
    })
    .compileComponents();
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
