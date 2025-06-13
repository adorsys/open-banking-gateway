import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { PaymentAccountComponent } from './payment-account.component';
import { PaymentAccountPaymentsComponent } from '../payment-account-payments/payment-account-payments.component';
import { StorageService } from '../../../services/storage.service';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { RouteUtilsService } from '../../../services/route-utils.service';

describe('PaymentAccountComponent', () => {
  let component: PaymentAccountComponent;
  let fixture: ComponentFixture<PaymentAccountComponent>;
  let route: ActivatedRoute;
  let bankId;
  let routeUtils: RouteUtilsService;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        imports: [RouterTestingModule, PaymentAccountComponent, PaymentAccountPaymentsComponent],
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
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentAccountComponent);
    component = fixture.componentInstance;
    route = TestBed.inject(ActivatedRoute);
    const storageService = TestBed.inject(StorageService);
    routeUtils = TestBed.inject(RouteUtilsService);
    bankId = routeUtils.getBankId(route);
    spyOn(storageService, 'getLoa')
      .withArgs(bankId)
      .and.returnValue([{ resourceId: '1234', iban: '2', name: '3' }]);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
