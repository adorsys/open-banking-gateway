import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BankComponent } from './bank.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { RouterTestingModule } from '@angular/router/testing';
import { NavbarComponent } from '../common/navbar/navbar.component';
import { AuthService } from '../services/auth.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BankProfileService } from '../bank-search/services/bank-profile.service';
import { BankProfile } from '../api';
import { of } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { NgxSpinnerModule } from 'ngx-spinner';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('BankComponent', () => {
  let component: BankComponent;
  let fixture: ComponentFixture<BankComponent>;
  const authServiceSpy = jasmine.createSpyObj('AuthService', ['isLoggedIn']);
  let bankService: BankProfileService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SidebarComponent, BankComponent, NavbarComponent],
      imports: [RouterTestingModule, HttpClientTestingModule, ReactiveFormsModule, NgxSpinnerModule, NoopAnimationsModule],
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
    bankService = TestBed.get(BankProfileService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
