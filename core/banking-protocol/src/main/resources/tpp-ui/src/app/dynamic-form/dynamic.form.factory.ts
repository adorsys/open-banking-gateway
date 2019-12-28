import { Injectable }   from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { DynamicFormControlBase } from './dynamic.form.control.base';

@Injectable()
export class DynamicFormFactory {
  constructor() { }

  toFormGroup(controls: DynamicFormControlBase<any>[] ) {
    let group: any = {};

    controls.forEach(control => {
      group[control.code] = control.required
        ? new FormControl(control.value || '', Validators.required)
        : new FormControl(control.value || '');
    });
    return new FormGroup(group);
  }
}
