import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RedirectAfterConsentDeniedComponent } from './redirect-after-consent-denied.component';

describe('RedirectAfterConsentDeniedComponent', () => {
  let component: RedirectAfterConsentDeniedComponent;
  let fixture: ComponentFixture<RedirectAfterConsentDeniedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RedirectAfterConsentDeniedComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RedirectAfterConsentDeniedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
