import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountsReferenceComponent } from './accounts-reference.component';
import { ReactiveFormsModule } from '@angular/forms';

describe('AccountSelectorComponent', () => {
  let component: AccountsReferenceComponent;
  let fixture: ComponentFixture<AccountsReferenceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AccountsReferenceComponent],
      imports: [ReactiveFormsModule]
    }).compileComponents();
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
