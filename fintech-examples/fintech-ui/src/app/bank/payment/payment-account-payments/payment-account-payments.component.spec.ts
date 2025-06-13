import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';

import { PaymentAccountPaymentsComponent } from './payment-account-payments.component';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('PaymentAccountPaymentsComponent', () => {
  let component: PaymentAccountPaymentsComponent;
  let fixture: ComponentFixture<PaymentAccountPaymentsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        imports: [RouterTestingModule, ReactiveFormsModule, PaymentAccountPaymentsComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: {
              snapshot: { params: { bankid: '1234', accountid: '1234' } }
            }
          },
          provideHttpClient(withInterceptorsFromDi()),
          provideHttpClientTesting()
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentAccountPaymentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
