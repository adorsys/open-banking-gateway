import { provideHttpClientTesting } from '@angular/common/http/testing';
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { HttpResponse, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { of } from 'rxjs';

import { AisService } from '../services/ais.service';
import { StorageService } from '../../services/storage.service';
import { ListTransactionsComponent } from './list-transactions.component';
import { BankComponent } from '../bank.component';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { TransactionsResponse } from '../../api';
import { LoTRetrievalInformation } from '../../models/consts';
import { RouteUtilsService } from '../../services/route-utils.service';

describe('ListTransactionsComponent', () => {
  let component: ListTransactionsComponent;
  let fixture: ComponentFixture<ListTransactionsComponent>;
  let aisService: AisService;
  let route: ActivatedRoute;
  let bankId: string;
  let accountId: string;

  const BANK_ID = '1234';
  const ACCOUNT_ID = '1234';
  const MOCK_ACCOUNT = {
    resourceId: ACCOUNT_ID,
    iban: 'DE123456789',
    name: 'Test Account',
    balances: [
      {
        balanceAmount: {
          amount: '1000',
          currency: 'EUR'
        }
      }
    ]
  };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ListTransactionsComponent, BankComponent, SidebarComponent],
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        providers: [
          AisService,
          provideRouter([
            {
              path: 'bank/:bankid',
              children: [
                {
                  path: 'accounts/:accountid',
                  component: ListTransactionsComponent
                }
              ]
            }
          ]),
          RouteUtilsService,
          {
            provide: ActivatedRoute,
            useValue: {
              snapshot: { params: { accountid: ACCOUNT_ID } },
              parent: {
                snapshot: { params: {} },
                parent: {
                  snapshot: { params: { bankid: BANK_ID } }
                }
              }
            }
          },
          {
            provide: StorageService,
            useValue: {
              getSettings: () => ({
                lot: LoTRetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT,
                consent: '',
                cacheLot: false,
                consentRequiresAuthentication: true,
                dateFrom: '1970-01-01',
                dateTo: StorageService.isoDate(new Date())
              }),
              isAfterRedirect: () => false,
              getLoa: jasmine.createSpy('getLoa').and.returnValue([MOCK_ACCOUNT])
            }
          },
          provideHttpClient(withInterceptorsFromDi()),
          provideHttpClientTesting()
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ListTransactionsComponent);
    component = fixture.componentInstance;
    aisService = TestBed.inject(AisService);
    route = TestBed.inject(ActivatedRoute);
    const routeUtils = TestBed.inject(RouteUtilsService);
    bankId = routeUtils.getBankId(route);
    accountId = routeUtils.getAccountId(route);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load transactions', () => {
    const mockTransactions: HttpResponse<TransactionsResponse> = {} as HttpResponse<TransactionsResponse>;
    const loTRetrievalInformation: LoTRetrievalInformation = LoTRetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT;

    spyOn(aisService, 'getTransactions')
      .withArgs(
        bankId,
        accountId,
        loTRetrievalInformation,
        '',
        true,
        true,
        '1970-01-01',
        StorageService.isoDate(new Date())
      )
      .and.returnValue(of(mockTransactions));
    expect(component.bankId).toEqual(bankId);
    aisService
      .getTransactions(
        bankId,
        accountId,
        loTRetrievalInformation,
        '',
        true,
        true,
        '1970-01-01',
        StorageService.isoDate(new Date())
      )
      .subscribe((res) => {
        expect(res).toEqual(mockTransactions);
      });
  });
});
