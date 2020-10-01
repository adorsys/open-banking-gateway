import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { of } from 'rxjs';

import { BankProfileService } from '../bank-search/services/bank-profile.service';
import { BankComponent } from './bank.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { NavbarComponent } from '../common/navbar/navbar.component';

describe('BankComponent', () => {
  let component: BankComponent;
  let fixture: ComponentFixture<BankComponent>;
  const authServiceSpy = jasmine.createSpyObj('AuthService', ['isLoggedIn']);
  let bankService: BankProfileService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SidebarComponent, BankComponent, NavbarComponent],
      imports: [RouterTestingModule, HttpClientTestingModule, ReactiveFormsModule],
      providers: [
        BankProfileService,
        {
          provide: ActivatedRoute,
          useValue: {
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
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BankComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    bankService = TestBed.inject(BankProfileService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
