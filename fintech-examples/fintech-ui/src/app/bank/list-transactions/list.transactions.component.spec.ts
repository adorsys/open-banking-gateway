import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';

import { AisService } from '../services/ais.service';
import { StorageService } from '../../services/storage.service';
import { ListTransactionsComponent } from './list-transactions.component';
import { BankComponent } from '../bank.component';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { TransactionsResponse } from '../../api';
import { Consts, LoTRetrievalInformation } from '../../models/consts';

describe('ListTransactionsComponent', () => {
  let component: ListTransactionsComponent;
  let fixture: ComponentFixture<ListTransactionsComponent>;
  let aisService: AisService;
  let route: ActivatedRoute;
  let storageService;
  let storageServiceSpy;
  let bankId;
  let accountId;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      declarations: [ListTransactionsComponent, BankComponent, SidebarComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      providers: [
        AisService,
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { params: { bankid: '1234', accountid: '1234' } }
          }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListTransactionsComponent);
    component = fixture.componentInstance;
    aisService = TestBed.inject(AisService);
    route = TestBed.inject(ActivatedRoute);
    storageService = TestBed.inject(StorageService);
    bankId = route.snapshot.params[Consts.BANK_ID_NAME];
    accountId = route.snapshot.params[Consts.ACCOUNT_ID_NAME];
    storageServiceSpy = spyOn(storageService, 'getLoa').withArgs(bankId).and.returnValues([]);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load transactions', () => {
    const mockTransactions: HttpResponse<TransactionsResponse> = {} as HttpResponse<TransactionsResponse>;
    const loTRetrievalInformation: LoTRetrievalInformation = LoTRetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT;

    spyOn(aisService, 'getTransactions')
      .withArgs(bankId, accountId, loTRetrievalInformation, '', true, true, '1970-01-01', StorageService.isoDate(new Date()))
      .and.returnValue(of(mockTransactions));
    expect(component.bankId).toEqual(bankId);
    aisService.getTransactions(bankId, accountId, loTRetrievalInformation, '', true, true, '1970-01-01', StorageService.isoDate(new Date())).subscribe((res) => {
      expect(res).toEqual(mockTransactions);
    });
  });
});
