import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmConsentPageComponent } from './confirm-consent-page.component';

describe('ConfirmConsentPageComponent', () => {
  let component: ConfirmConsentPageComponent;
  let fixture: ComponentFixture<ConfirmConsentPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConfirmConsentPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmConsentPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
