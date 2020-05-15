import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';

import { EnterScaComponent } from './enter-sca.component';
import { StubUtilTests } from '../../ais/common/stub-util-tests';

describe('ScaSelectComponent', () => {
  let component: EnterScaComponent;
  let fixture: ComponentFixture<EnterScaComponent>;
  let form;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EnterScaComponent],
      imports: [ReactiveFormsModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EnterScaComponent);
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
    form.get('sca').setValue(StubUtilTests.DUMMY_INPUT);
    component.onSubmit();
    fixture.detectChanges();
    expect(component.reportScaResultForm.valid).toBe(true);
  });
});
