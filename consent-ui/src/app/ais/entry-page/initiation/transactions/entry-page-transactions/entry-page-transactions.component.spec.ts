import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { EntryPageTransactionsComponent } from './entry-page-transactions.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('EntryPageTransactionsComponent', () => {
  let component: EntryPageTransactionsComponent;
  let fixture: ComponentFixture<EntryPageTransactionsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [EntryPageTransactionsComponent],
        schemas: [CUSTOM_ELEMENTS_SCHEMA]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(EntryPageTransactionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
