import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { DynamicInputsComponent } from './dynamic-inputs.component';
import { ReactiveFormsModule } from '@angular/forms';

describe('DynamicInputsComponent', () => {
  let component: DynamicInputsComponent;
  let fixture: ComponentFixture<DynamicInputsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [DynamicInputsComponent],
        imports: [ReactiveFormsModule]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(DynamicInputsComponent);
    component = fixture.componentInstance;
    component.violations = [];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
