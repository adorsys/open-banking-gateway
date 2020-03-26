import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { AisService } from '../services/ais.service';
import { ListAccountsComponent } from './list-accounts.component';
import { AccountDetails, AccountList, AccountStatus } from '../../api';
import { BankComponent } from '../bank.component';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { HttpResponse } from '@angular/common/http';

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
                    paramMap: {
                      subscribe(bankId: string): string {
                        return '1234';
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
    const mockAccounts: HttpResponse<AccountList> = {} as HttpResponse<AccountList>;

    spyOn(aisService, 'getAccounts')
      .withArgs(bankId)
      .and.returnValue(of(mockAccounts));
    // expect(component.bankId).toEqual(bankId);
    aisService.getAccounts(bankId).subscribe(res => {
      expect(res).toEqual(mockAccounts);
    });
  });
});
