import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';

import { EnterTanComponent } from './enter-tan.component';
import { StubUtilTests } from '../../ais/common/stub-util-tests';

describe('EnterTanComponent', () => {
  let component: EnterTanComponent;
  let fixture: ComponentFixture<EnterTanComponent>;
  let form;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EnterTanComponent],
      imports: [ReactiveFormsModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EnterTanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    form = component.reportScaResultForm;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be false if the form is invalid', () => {
    component.onSubmit();
    fixture.detectChanges();
    expect(component.reportScaResultForm.valid).toBe(false);
  });

  it('should be true if the form is valid', () => {
    form.get('tan').setValue(StubUtilTests.DUMMY_INPUT);
    component.onSubmit();
    fixture.detectChanges();
    expect(component.reportScaResultForm.valid).toBe(true);
  });
});
