import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsentPaymentAccessSelectionComponent } from './consent-payment-access-selection.component';

describe('ConsentPaymentAccessSelectionComponent', () => {
  let component: ConsentPaymentAccessSelectionComponent;
  let fixture: ComponentFixture<ConsentPaymentAccessSelectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConsentPaymentAccessSelectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsentPaymentAccessSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
