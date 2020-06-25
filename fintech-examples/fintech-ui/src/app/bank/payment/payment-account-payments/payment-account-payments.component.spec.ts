import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentAccountPaymentsComponent } from './payment-account-payments.component';

describe('PaymentAccountPaymentsComponent', () => {
  let component: PaymentAccountPaymentsComponent;
  let fixture: ComponentFixture<PaymentAccountPaymentsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PaymentAccountPaymentsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentAccountPaymentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // TODO Peter FIXME
//  it('should create', () => {
//    expect(component).toBeTruthy();
//  });
});
