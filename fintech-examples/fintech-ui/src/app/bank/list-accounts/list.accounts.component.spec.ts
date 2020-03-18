import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { AisService } from '../services/ais.service';
import { ListAccountsComponent } from './list-accounts.component';
import { AccountDetails, AccountStatus } from '../../api';
import { AppComponent } from '../../app.component';

describe('ListAccountsComponent', () => {
  let component: ListAccountsComponent;
  let fixture: ComponentFixture<ListAccountsComponent>;
  let aisService: AisService;
  let router: Router;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      declarations: [ListAccountsComponent],
      providers: [AisService, { provide: ActivatedRoute }]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListAccountsComponent);
    component = fixture.componentInstance;
    router = TestBed.get(Router);
    aisService = TestBed.get(AisService);
    // component.route.parent.parent.params.subscribe((params) => {
    // component.bankId = params['bankId'];
    // component.getAccountDetails();
    // });
    component.bankId = localStorage.getItem('bankId');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load accounts', () => {
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

    spyOn(aisService, 'getAccounts').and.returnValue(of(mockAccounts));
    expect(component.accounts).toEqual(mockAccounts);
    expect(component.accounts).not.toBeUndefined();
  });
});
