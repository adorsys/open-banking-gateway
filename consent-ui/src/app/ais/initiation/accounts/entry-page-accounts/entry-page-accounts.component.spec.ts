import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EntryPageAccountsComponent } from './entry-page-accounts.component';

describe('EntryPageAccountsComponent', () => {
  let component: EntryPageAccountsComponent;
  let fixture: ComponentFixture<EntryPageAccountsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EntryPageAccountsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EntryPageAccountsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
