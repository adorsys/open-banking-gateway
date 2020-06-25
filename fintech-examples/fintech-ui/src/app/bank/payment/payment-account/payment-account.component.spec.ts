import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentAccountComponent } from './payment-account.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { PaymentAccountPaymentsComponent } from '../payment-account-payments/payment-account-payments.component';
// import { StorageService } from '../../../services/storage.service';

describe('PaymentAccountComponent', () => {
  let component: PaymentAccountComponent;
  let fixture: ComponentFixture<PaymentAccountComponent>;
  let route: ActivatedRoute;

  // let storageService: StorageService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      declarations: [PaymentAccountComponent, PaymentAccountPaymentsComponent],
      providers: [
        // StorageService,
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: convertToParamMap([{ bankid:    '1234' },{ accountid: '1234' }] ) }
          }
        }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentAccountComponent);
    component = fixture.componentInstance;
    route = TestBed.get(ActivatedRoute);
    // storageService = TestBed.get(StorageService);
    fixture.detectChanges();
  });

  // TODO PETER FIXME
  // it('should create', () => {
  //   spyOn(storageService, 'getLoa').withArgs().and.returnValue([]);
  //   expect(storageService.getLoa()).toEqual([]);
  //  expect(component).toBeTruthy();
  // });


});
