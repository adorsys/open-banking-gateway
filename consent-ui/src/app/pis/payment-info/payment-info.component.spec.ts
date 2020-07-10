import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentInfoComponent } from './payment-info.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('PaymentInfoComponent', () => {
  let component: PaymentInfoComponent;
  let fixture: ComponentFixture<PaymentInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PaymentInfoComponent],
      imports: [RouterTestingModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
