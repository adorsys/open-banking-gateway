import { provideHttpClientTesting } from '@angular/common/http/testing';
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { HttpResponse, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { of } from 'rxjs';

import { AisService } from '../services/ais.service';
import { ListAccountsComponent } from './list-accounts.component';
import { AccountList } from '../../api';
import { BankComponent } from '../bank.component';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { LoARetrievalInformation } from '../../models/consts';
import { AccountCardComponent } from '../common/account-card/account-card.component';
import { RouteUtilsService } from '../../services/route-utils.service';

describe('ListAccountsComponent', () => {
  let component: ListAccountsComponent;
  let fixture: ComponentFixture<ListAccountsComponent>;
  let aisService: AisService;
  let route: ActivatedRoute;
  let routeUtils: RouteUtilsService;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ListAccountsComponent, BankComponent, SidebarComponent, AccountCardComponent, RouterTestingModule],
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        providers: [
          AisService,
          {
            provide: ActivatedRoute,
            useValue: {
              snapshot: { params: { bankid: '1234' } }
            }
          },
          provideHttpClient(withInterceptorsFromDi()),
          provideHttpClientTesting()
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ListAccountsComponent);
    component = fixture.componentInstance;
    aisService = TestBed.inject(AisService);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load accounts', () => {
    routeUtils = TestBed.inject(RouteUtilsService);
    const bankId = routeUtils.getBankId(route);
    const loaRetrievalInformation = LoARetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT;
    const mockAccounts: HttpResponse<AccountList> = {} as HttpResponse<AccountList>;

    spyOn(aisService, 'getAccounts')
      .withArgs(bankId, loaRetrievalInformation, '', false, true, true)
      .and.returnValue(of(mockAccounts));
    expect(component.bankId).toEqual(bankId);
    aisService.getAccounts(bankId, loaRetrievalInformation, '', false, true, true).subscribe((res) => {
      expect(res).toEqual(mockAccounts);
    });
  });
});
