import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DedicatedAccessComponent } from './dedicated-access.component';
import { ReactiveFormsModule } from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('LimitedAccessComponent', () => {
  let component: DedicatedAccessComponent;
  let fixture: ComponentFixture<DedicatedAccessComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DedicatedAccessComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      providers: [ReactiveFormsModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DedicatedAccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
