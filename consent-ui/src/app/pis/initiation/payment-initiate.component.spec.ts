import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentInitiateComponent } from './payment-initiate.component';

describe('InitiationComponent', () => {
  let component: PaymentInitiateComponent;
  let fixture: ComponentFixture<PaymentInitiateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PaymentInitiateComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentInitiateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
