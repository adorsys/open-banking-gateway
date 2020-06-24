import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentAccountsComponent } from './payment-accounts.component';

describe('PaymentAccountsComponent', () => {
  let component: PaymentAccountsComponent;
  let fixture: ComponentFixture<PaymentAccountsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PaymentAccountsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentAccountsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
