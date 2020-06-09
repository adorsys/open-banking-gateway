import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SidebarComponent } from './sidebar.component';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BankProfileService } from '../../bank-search/services/bank-profile.service';
import { BankProfile } from '../../api';
import { of } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('SidebarComponent', () => {
  let component: SidebarComponent;
  let fixture: ComponentFixture<SidebarComponent>;
  let bankProfileService: BankProfileService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SidebarComponent],
      imports: [RouterTestingModule, HttpClientTestingModule],
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
    fixture = TestBed.createComponent(SidebarComponent);
    component = fixture.componentInstance;
    bankProfileService = TestBed.get(BankProfileService);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('Should call getProfile on ngOnInit', () => {
    const bankProfileServiceSpy = spyOn(bankProfileService, 'getBankProfile')
      .withArgs('1234')
      .and.callThrough();
    component.ngOnInit();
    expect(bankProfileServiceSpy).toHaveBeenCalled();
  });

  it('should get bankId from activated route', () => {
    const bankId = '1234';
    const bankProfile: BankProfile = {
      bankId: '1234',
      bankName: 'Deutsche Bank',
      bic: '1234',
      services: []
    } as BankProfile;

    spyOn(bankProfileService, 'getBankProfile')
      .withArgs(bankId)
      .and.returnValue(of(bankProfile));
    component.getBankProfile(bankId);
    expect(component.bankId).toEqual(bankId);
    bankProfileService.getBankProfile(bankId).subscribe(res => expect(res).toEqual(bankProfile));
  });
});
