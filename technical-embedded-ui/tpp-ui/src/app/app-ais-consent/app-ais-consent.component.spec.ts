import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AppAisConsentComponent } from './app-ais-consent.component';

describe('AppAisConsentComponent', () => {
  let component: AppAisConsentComponent;
  let fixture: ComponentFixture<AppAisConsentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AppAisConsentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AppAisConsentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
