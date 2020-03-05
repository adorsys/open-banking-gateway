import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EntryPageTransactionsComponent } from './entry-page-transactions.component';

describe('EntryPageTransactionsComponent', () => {
  let component: EntryPageTransactionsComponent;
  let fixture: ComponentFixture<EntryPageTransactionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EntryPageTransactionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EntryPageTransactionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
