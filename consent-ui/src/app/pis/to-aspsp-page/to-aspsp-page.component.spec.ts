import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { ToAspspPageComponent } from './to-aspsp-page.component';

describe('ToAspspPageComponent', () => {
  let component: ToAspspPageComponent;
  let fixture: ComponentFixture<ToAspspPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ToAspspPageComponent],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ToAspspPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
