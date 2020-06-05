import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EntryPagePaymentsComponent } from './entry-page-payments.component';

describe('EntryPagePaymentsComponent', () => {
  let component: EntryPagePaymentsComponent;
  let fixture: ComponentFixture<EntryPagePaymentsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EntryPagePaymentsComponent ]
    })
    .compileComponents();
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
