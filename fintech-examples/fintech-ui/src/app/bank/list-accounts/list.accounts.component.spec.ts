import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { AisService } from '../services/ais.service';
import { ListAccountsComponent } from './list-accounts.component';
import { AccountDetails, AccountStatus } from '../../api';

describe('ListAccountsComponent', () => {
  let component: ListAccountsComponent;
  let fixture: ComponentFixture<ListAccountsComponent>;
  const aisService = AisService;
  let router: Router;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule, RouterTestingModule],
      declarations: [ListAccountsComponent],
      providers: [AisService]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListAccountsComponent);
    component = fixture.componentInstance;
    router = TestBed.get(Router);
    fixture.detectChanges();
    //        this.aisService = TestBed.get(AisService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load accounts on NgOnInit', () => {
    const mockAccounts: AccountDetails[] = [
      {
        resourceId: 'XXXXXX',
        iban: 'DE35653635635663',
        bban: 'BBBAN',
        pan: 'pan',
        maskedPan: 'maskedPan',
        msisdn: 'MSISDN',
        currency: 'EUR',
        name: 'Pupkin',
        product: 'Deposit',
        cashAccountType: 'CASH',
        status: AccountStatus.Enabled,
        ownerName: 'Anton Brueckner'
      } as AccountDetails
    ];
    const getAccountsSpy = spyOn(aisService, 'getAccounts').and.returnValue(
      of({
        accounts: mockAccounts,
        totalElements: mockAccounts.length
      })
    );

    component.ngOnInit();

    expect(getAccountsSpy).toHaveBeenCalled();
    expect(component.accounts).toEqual(mockAccounts);
  });
});
