import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';

import { BankProfileService } from '../bank-search/services/bank-profile.service';
import { BankComponent } from './bank.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { NavbarComponent } from '../common/navbar/navbar.component';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

describe('BankComponent', () => {
  let component: BankComponent;
  let fixture: ComponentFixture<BankComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, SidebarComponent, BankComponent, NavbarComponent],
        providers: [
          BankProfileService,
          {
            provide: ActivatedRoute,
            useValue: {
              params: of({ bankId: 1234 }),
              snapshot: {
                paramMap: {
                  get(bankId: string): string {
                    if (bankId === 'bankid') {
                      return '1234';
                    }
                    return null;
                  }
                }
              }
            }
          },
          provideHttpClient(withInterceptorsFromDi()),
          provideHttpClientTesting()
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(BankComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render SidebarComponent', () => {
    const sidebarElement = fixture.debugElement.query(By.directive(SidebarComponent));
    expect(sidebarElement).toBeTruthy();
  });
});
