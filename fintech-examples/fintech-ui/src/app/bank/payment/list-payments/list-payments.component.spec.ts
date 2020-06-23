import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ListPaymentsComponent } from './list-payments.component';

describe('ListPaymentsComponent', () => {
  let component: ListPaymentsComponent;
  let fixture: ComponentFixture<ListPaymentsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ListPaymentsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListPaymentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
