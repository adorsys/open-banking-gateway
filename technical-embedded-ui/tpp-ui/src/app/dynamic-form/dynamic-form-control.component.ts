import {Component, Input} from '@angular/core';
import {FormGroup} from '@angular/forms';

import {DynamicFormControlBase} from './dynamic-form-control-base';

@Component({
  selector: 'dynamic-form-control',
  templateUrl: './dynamic-form-control.component.html'
})
export class DynamicFormControlComponent {
  @Input() control: DynamicFormControlBase<any>;
  @Input() form: FormGroup;

  get isValid() { return this.form.controls[this.control.id].valid; }
}
