import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { EnterScaPageComponent } from './enter-sca-page.component';

describe('EnterScaPageComponent', () => {
  let component: EnterScaPageComponent;
  let fixture: ComponentFixture<EnterScaPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EnterScaPageComponent],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EnterScaPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
