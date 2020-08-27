import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { AisService } from '../services/ais.service';
import { ListAccountsComponent } from './list-accounts.component';
import { AccountList } from '../../api';
import { BankComponent } from '../bank.component';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { HttpResponse } from '@angular/common/http';
import { LoARetrievalInformation } from '../../models/consts';
import { AccountCardComponent } from './account-card/account-card.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('ListAccountsComponent', () => {
  let component: ListAccountsComponent;
  let fixture: ComponentFixture<ListAccountsComponent>;
  let aisService: AisService;
  let route: ActivatedRoute;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      declarations: [ListAccountsComponent, BankComponent, SidebarComponent, AccountCardComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      providers: [
        AisService,
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ bankid: '1234' }) } }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListAccountsComponent);
    component = fixture.componentInstance;
    aisService = TestBed.get(AisService);
    route = TestBed.get(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load accounts', () => {
    const bankId = route.snapshot.paramMap.get('bankid');
    const loaRetrievalInformation = LoARetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT;
    const mockAccounts: HttpResponse<AccountList> = {} as HttpResponse<AccountList>;

    spyOn(aisService, 'getAccounts')
      .withArgs(bankId, loaRetrievalInformation)
      .and.returnValue(of(mockAccounts));
    expect(component.bankId).toEqual(bankId);
    aisService.getAccounts(bankId, loaRetrievalInformation).subscribe(res => {
      expect(res).toEqual(mockAccounts);
    });
  });
});
