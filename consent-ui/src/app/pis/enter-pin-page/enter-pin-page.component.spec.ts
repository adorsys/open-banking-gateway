import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EnterPinPageComponent } from './enter-pin-page.component';

describe('EnterPinPageComponent', () => {
  let component: EnterPinPageComponent;
  let fixture: ComponentFixture<EnterPinPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EnterPinPageComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EnterPinPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
