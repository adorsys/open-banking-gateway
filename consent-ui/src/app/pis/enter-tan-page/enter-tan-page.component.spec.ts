import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { EnterTanPageComponent } from './enter-tan-page.component';

describe('EnterScaPageComponent', () => {
  let component: EnterTanPageComponent;
  let fixture: ComponentFixture<EnterTanPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EnterTanPageComponent],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EnterTanPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
