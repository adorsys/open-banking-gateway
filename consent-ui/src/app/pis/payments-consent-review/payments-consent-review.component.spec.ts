import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentsConsentReviewComponent } from './payments-consent-review.component';

describe('PaymentsConsentReviewComponent', () => {
  let component: PaymentsConsentReviewComponent;
  let fixture: ComponentFixture<PaymentsConsentReviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PaymentsConsentReviewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentsConsentReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
