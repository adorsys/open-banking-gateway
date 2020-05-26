import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountReference, AccountsReferenceComponent} from './accounts-reference.component';
import {FormGroup, ReactiveFormsModule} from '@angular/forms';

describe('AccountsReferenceComponent', () => {
    let component: AccountsReferenceComponent;
    let fixture: ComponentFixture<AccountsReferenceComponent>;
    let form;
    let removeAccountSpy;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [AccountsReferenceComponent],
            imports: [ReactiveFormsModule]
        }).compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(AccountsReferenceComponent);
        component = fixture.componentInstance;
        component.targetForm = new FormGroup({});
        component.accounts = [];
        form = component.targetForm;
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
        const account :  AccountReference = {
            id: "12345",
            iban: "DE12344313232222"
        };

        removeAccountSpy = spyOn(component, 'removeAccount');
        component.removeAccount(account);
        expect(removeAccountSpy).toHaveBeenCalledWith(account);
        expect(component.accounts.length).toEqual(0);
    });
});
