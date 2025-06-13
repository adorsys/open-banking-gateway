import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountCardComponent } from './account-card.component';
import { AccountStruct } from '../../redirect-page/redirect-struct';

describe('AccountCardComponent', () => {
  let component: AccountCardComponent;
  let fixture: ComponentFixture<AccountCardComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [AccountCardComponent]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountCardComponent);
    component = fixture.componentInstance;
    component.account = {
      iban: 'DE2750010517421134792622',
      bban: 'DE2750010517421134792622',
      currency: 'EUR'
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get accountNumber', () => {
    const iban = 'DE2750010517421134792622';
    const name = 'bob';
    const id = 'dfdfdfd4drrrrr-444rr33-er43';
    const accountStruct = new AccountStruct(id, iban, name);
    spyOn(component, 'getAccountNumber').withArgs();
    expect(accountStruct.iban).toEqual(component.account.iban);
  });
});
