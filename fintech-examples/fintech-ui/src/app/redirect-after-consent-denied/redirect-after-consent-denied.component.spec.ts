import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RedirectAfterConsentDeniedComponent } from './redirect-after-consent-denied.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('RedirectAfterConsentDeniedComponent', () => {
  let component: RedirectAfterConsentDeniedComponent;
  let fixture: ComponentFixture<RedirectAfterConsentDeniedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RedirectAfterConsentDeniedComponent],
      imports: [RouterTestingModule]
    }).compileComponents();
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
