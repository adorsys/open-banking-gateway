import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { AisService } from '../services/ais.service';
import { ListAccountsComponent } from './list-accounts.component';
import { AccountDetails, AccountStatus } from '../../api';
import { BankComponent } from '../bank.component';
import { SidebarComponent } from '../sidebar/sidebar.component';

describe('ListAccountsComponent', () => {
  let component: ListAccountsComponent;
  let fixture: ComponentFixture<ListAccountsComponent>;
  let aisService: AisService;
  let router: Router;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([
          { path: 'bank/:id', component: BankComponent },
          { path: '', component: ListAccountsComponent }
        ])
      ],
      declarations: [ListAccountsComponent, BankComponent, SidebarComponent]
    })
      .overrideComponent(ListAccountsComponent, {
        set: {
          providers: [
            AisService,
            {
              provide: ActivatedRoute,
              useValue: {
                parent: {
                  parent: {
                    params: of({ bankId: 1234 }),
                    snapshot: {
                      paramMap: {
                        get(bankId: string): string {
                          return '1234';
                        }
                      }
                    }
                  }
                }
              }
            }
          ]
        }
      })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListAccountsComponent);
    component = fixture.componentInstance;
    router = TestBed.get(Router);
    aisService = TestBed.get(AisService);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load accounts', () => {
    const bankId = '1234';
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

    spyOn(aisService, 'getAccounts')
      .withArgs(bankId)
      .and.returnValue(of(mockAccounts));
    expect(component.bankId).toEqual(bankId);
    aisService.getAccounts(bankId).subscribe(res => {
      expect(res).toEqual(mockAccounts);
    });
  });
});
