import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { InternalAccountReference, AccountsReferenceComponent } from './accounts-reference.component';
import { UntypedFormGroup, ReactiveFormsModule } from '@angular/forms';

describe('AccountsReferenceComponent', () => {
  let component: AccountsReferenceComponent;
  let fixture: ComponentFixture<AccountsReferenceComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [AccountsReferenceComponent],
      imports: [ReactiveFormsModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountsReferenceComponent);
    component = fixture.componentInstance;
    component.targetForm = new UntypedFormGroup({});
    component.accounts = [];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call addAccount', () => {
    component.addAccount();
    expect(component.accounts.length).toEqual(1);
  });

  it('should call removeAccount', () => {
    const account: InternalAccountReference = {
      ibanId: '12345',
      currencyId: '12345',
      iban: 'DE12344313232222',
      currency: 'EUR'
    };

    const removeAccountSpy = spyOn(component, 'removeAccount');
    component.removeAccount(account);
    expect(removeAccountSpy).toHaveBeenCalledWith(account);
    expect(component.accounts.length).toEqual(0);
  });
});
