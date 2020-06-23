import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ListAccountsForPaymentComponent } from './list-accounts-for-payment.component';

describe('ListAccountsForPaymentComponent', () => {
  let component: ListAccountsForPaymentComponent;
  let fixture: ComponentFixture<ListAccountsForPaymentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ListAccountsForPaymentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListAccountsForPaymentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
