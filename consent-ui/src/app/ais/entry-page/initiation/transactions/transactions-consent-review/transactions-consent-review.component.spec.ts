import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionsConsentReviewComponent } from './transactions-consent-review.component';

describe('TransactionsConsentReviewComponent', () => {
  let component: TransactionsConsentReviewComponent;
  let fixture: ComponentFixture<TransactionsConsentReviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TransactionsConsentReviewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionsConsentReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
