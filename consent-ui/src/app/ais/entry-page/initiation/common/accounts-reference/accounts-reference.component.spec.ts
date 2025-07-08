import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { InternalAccountReference, AccountsReferenceComponent } from './accounts-reference.component';
import { UntypedFormGroup, ReactiveFormsModule } from '@angular/forms';
import { expect } from '@jest/globals';
import { Subscription } from 'rxjs';
import 'zone.js';
import 'zone.js/testing';

type AccountsReferenceComponentWithPrivateMembers = AccountsReferenceComponent & {
  subscriptions: Map<string, Subscription>;
};

describe('AccountsReferenceComponent', () => {
  let component: AccountsReferenceComponent;
  let fixture: ComponentFixture<AccountsReferenceComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [AccountsReferenceComponent],
        imports: [ReactiveFormsModule]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountsReferenceComponent);
    component = fixture.componentInstance;
    component.targetForm = new UntypedFormGroup({});
    component.accounts = [];
    (component as AccountsReferenceComponentWithPrivateMembers).subscriptions = new Map<string, Subscription>();
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
    const account = new InternalAccountReference('DE12344313232222', 'EUR');
    component.accounts = [account];

    const ibanSub = ({ unsubscribe: jest.fn() } as unknown) as Subscription;
    const currencySub = ({ unsubscribe: jest.fn() } as unknown) as Subscription;

    (component as AccountsReferenceComponentWithPrivateMembers).subscriptions.set(account.ibanId, ibanSub);
    (component as AccountsReferenceComponentWithPrivateMembers).subscriptions.set(account.currencyId, currencySub);

    const ibanUnsubscribeSpy = jest.spyOn(ibanSub, 'unsubscribe');
    const currencyUnsubscribeSpy = jest.spyOn(currencySub, 'unsubscribe');

    const removeAccountSpy = jest.spyOn(component, 'removeAccount');

    component.removeAccount(account);

    expect(removeAccountSpy).toHaveBeenCalledWith(account);
    expect(component.accounts.length).toEqual(0);
    expect(ibanUnsubscribeSpy).toHaveBeenCalled();
    expect(currencyUnsubscribeSpy).toHaveBeenCalled();
  });
});
