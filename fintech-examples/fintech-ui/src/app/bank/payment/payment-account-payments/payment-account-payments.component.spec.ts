import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentAccountPaymentsComponent } from './payment-account-payments.component';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

describe('PaymentAccountPaymentsComponent', () => {
  let component: PaymentAccountPaymentsComponent;
  let fixture: ComponentFixture<PaymentAccountPaymentsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      declarations: [PaymentAccountPaymentsComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: convertToParamMap({ accountid: '1234', bankid: '1234' }) }
          }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentAccountPaymentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
