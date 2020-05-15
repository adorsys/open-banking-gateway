import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';

import { EnterPinComponent } from './enter-pin.component';
import { StubUtilTests } from '../../ais/common/stub-util-tests';

describe('ScaResultLayoutComponent', () => {
  let component: EnterPinComponent;
  let fixture: ComponentFixture<EnterPinComponent>;
  let form;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EnterPinComponent],
      imports: [ReactiveFormsModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EnterPinComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    form = component.pinForm;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be false if the form is invalid', () => {
    component.onSubmit();
    fixture.detectChanges();
    expect(component.pinForm.valid).toBe(false);
  });

  it('should be true if the form is valid', () => {
    form.get('pin').setValue(StubUtilTests.DUMMY_INPUT);
    component.onSubmit();
    fixture.detectChanges();
    expect(component.pinForm.valid).toBe(true);
  });
});
