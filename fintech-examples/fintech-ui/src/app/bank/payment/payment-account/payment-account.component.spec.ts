import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { PaymentAccountComponent } from './payment-account.component';
import { PaymentAccountPaymentsComponent } from '../payment-account-payments/payment-account-payments.component';
import { StorageService } from '../../../services/storage.service';
import { Consts } from '../../../models/consts';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('PaymentAccountComponent', () => {
  let component: PaymentAccountComponent;
  let fixture: ComponentFixture<PaymentAccountComponent>;
  let route: ActivatedRoute;
  let bankId;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
    declarations: [PaymentAccountComponent, PaymentAccountPaymentsComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    imports: [RouterTestingModule],
    providers: [
        StorageService,
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
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentAccountComponent);
    component = fixture.componentInstance;
    route = TestBed.inject(ActivatedRoute);
    const storageService = TestBed.inject(StorageService);
    bankId = route.snapshot.params[Consts.BANK_ID_NAME];
    spyOn(storageService, 'getLoa')
      .withArgs(bankId)
      .and.returnValue([{ resourceId: '1234', iban: '2', name: '3' }]);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
