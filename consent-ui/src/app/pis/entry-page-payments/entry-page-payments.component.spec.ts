import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EntryPagePaymentsComponent } from './entry-page-payments.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('EntryPageAccountsComponent', () => {
  let component: EntryPagePaymentsComponent;
  let fixture: ComponentFixture<EntryPagePaymentsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EntryPagePaymentsComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EntryPagePaymentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
