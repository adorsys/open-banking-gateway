import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentAccountComponent } from './payment-account.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { PaymentAccountPaymentsComponent } from '../payment-account-payments/payment-account-payments.component';
import { StorageService } from '../../../services/storage.service';

describe('PaymentAccountComponent', () => {
  let component: PaymentAccountComponent;
  let fixture: ComponentFixture<PaymentAccountComponent>;
  let route: ActivatedRoute;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      declarations: [PaymentAccountComponent, PaymentAccountPaymentsComponent],
      providers: [
        StorageService,
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: convertToParamMap({ accountid: '1234' }) }
          }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentAccountComponent);
    component = fixture.componentInstance;
    route = TestBed.get(ActivatedRoute);
    const storageService = TestBed.get(StorageService);
    spyOn(storageService, 'getLoa').and.returnValue([{ resourceId: '1234', iban: '2', name: '3' }]);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
