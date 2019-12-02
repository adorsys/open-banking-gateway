import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BankSearchComponent } from './bank-search.component';

describe('BankSearchComponent', () => {
  let component: BankSearchComponent;
  let fixture: ComponentFixture<BankSearchComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BankSearchComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BankSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
