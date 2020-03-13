import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountsReferenceComponent } from './accounts-reference.component';

describe('AccountSelectorComponent', () => {
  let component: AccountsReferenceComponent;
  let fixture: ComponentFixture<AccountsReferenceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AccountsReferenceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountsReferenceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
