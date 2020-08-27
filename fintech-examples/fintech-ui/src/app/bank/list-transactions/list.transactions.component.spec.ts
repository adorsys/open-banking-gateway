import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { AisService } from '../services/ais.service';
import { ListTransactionsComponent } from './list-transactions.component';
import { BankComponent } from '../bank.component';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { HttpResponse } from '@angular/common/http';
import { TransactionsResponse } from '../../api';
import { LoTRetrievalInformation } from '../../models/consts';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { StorageService } from '../../services/storage.service';

describe('ListTransactionsComponent', () => {
  let component: ListTransactionsComponent;
  let fixture: ComponentFixture<ListTransactionsComponent>;
  let aisService: AisService;
  let route: ActivatedRoute;
  let storageService;
  let storageServiceSpy;

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
            parent: { snapshot: { paramMap: convertToParamMap({ bankid: '1234' }) } },
            snapshot: { paramMap: convertToParamMap({ accountid: '1234' }) }
          }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListTransactionsComponent);
    component = fixture.componentInstance;
    aisService = TestBed.get(AisService);
    route = TestBed.get(ActivatedRoute);
    storageService = TestBed.get(StorageService);
    storageServiceSpy = spyOn(storageService, 'getLoa').and.returnValues([]);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load transactions', () => {
    const bankId = route.parent.snapshot.paramMap.get('bankid');
    const accountId = route.snapshot.paramMap.get('accountid');
    const mockTransactions: HttpResponse<TransactionsResponse> = {} as HttpResponse<TransactionsResponse>;
    const loTRetrievalInformation: LoTRetrievalInformation = LoTRetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT;

    spyOn(aisService, 'getTransactions')
      .withArgs(bankId, accountId, loTRetrievalInformation)
      .and.returnValue(of(mockTransactions));
    expect(component.bankId).toEqual(bankId);
    aisService.getTransactions(bankId, accountId, loTRetrievalInformation).subscribe(res => {
      expect(res).toEqual(mockTransactions);
    });
  });
});
