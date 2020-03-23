import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DynamicInputsComponent } from './dynamic-inputs.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';

describe('DynamicInputsComponent', () => {
  let component: DynamicInputsComponent;
  let fixture: ComponentFixture<DynamicInputsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DynamicInputsComponent],
      imports: [ReactiveFormsModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DynamicInputsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
