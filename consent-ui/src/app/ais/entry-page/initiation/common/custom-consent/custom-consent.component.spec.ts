import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomConsentComponent } from './custom-consent.component';

describe('CustomConsentComponent', () => {
  let component: CustomConsentComponent;
  let fixture: ComponentFixture<CustomConsentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CustomConsentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomConsentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
