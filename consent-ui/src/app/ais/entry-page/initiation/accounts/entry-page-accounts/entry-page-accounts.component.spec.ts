import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { EntryPageAccountsComponent } from './entry-page-accounts.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('EntryPageAccountsComponent', () => {
  let component: EntryPageAccountsComponent;
  let fixture: ComponentFixture<EntryPageAccountsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [EntryPageAccountsComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
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
