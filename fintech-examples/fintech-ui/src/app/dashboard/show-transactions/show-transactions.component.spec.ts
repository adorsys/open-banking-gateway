import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowTransactionsComponent } from './show-transactions.component';

describe('ShowTransactionsComponent', () => {
  let component: ShowTransactionsComponent;
  let fixture: ComponentFixture<ShowTransactionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ShowTransactionsComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShowTransactionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
