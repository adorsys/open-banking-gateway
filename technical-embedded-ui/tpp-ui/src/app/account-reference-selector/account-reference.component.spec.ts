import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountReferenceComponent } from './account-reference.component';

describe('AccountReferenceSelectorComponent', () => {
  let component: AccountReferenceComponent;
  let fixture: ComponentFixture<AccountReferenceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AccountReferenceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountReferenceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
