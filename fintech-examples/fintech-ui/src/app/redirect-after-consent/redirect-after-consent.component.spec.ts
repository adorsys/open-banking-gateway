import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RedirectAfterConsentComponent } from './redirect-after-consent.component';

describe('RedirectAfterConsentComponent', () => {
  let component: RedirectAfterConsentComponent;
  let fixture: ComponentFixture<RedirectAfterConsentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RedirectAfterConsentComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RedirectAfterConsentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
