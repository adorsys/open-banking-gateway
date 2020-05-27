import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';

import { SelectScaComponent } from './select-sca.component';
import { StubUtilTests } from '../../ais/common/stub-util-tests';

describe('SelectScaComponent', () => {
  let component: SelectScaComponent;
  let fixture: ComponentFixture<SelectScaComponent>;
  let form;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SelectScaComponent],
      imports: [ReactiveFormsModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectScaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    form = component.scaMethodForm;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be false if the scaMethodForm is not valid', () => {
    form.get('selectedMethodValue').setValue(null);
    component.onSubmit();
    expect(component.scaMethodForm.valid).toBe(false);
  });

  it('should be true if the scaMethodForm is valid', () => {
    form.get('selectedMethodValue').setValue(StubUtilTests.SCA_METHOD_VALUE);
    component.onSubmit();
    expect(component.scaMethodForm.valid).toBe(true);
  });
});
