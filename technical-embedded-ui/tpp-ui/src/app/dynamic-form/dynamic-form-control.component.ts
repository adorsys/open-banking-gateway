import {Component, Input, OnInit} from '@angular/core';
import {FormGroup} from '@angular/forms';

import {DynamicFormControlBase} from './dynamic-form-control-base';
import {Globals} from '../globals';

@Component({
  selector: 'app-dynamic-form-control',
  templateUrl: './dynamic-form-control.component.html'
})
export class DynamicFormControlComponent implements OnInit {
  @Input() control: DynamicFormControlBase<any>;
  @Input() form: FormGroup;


  constructor(private globals: Globals) {
  }

  ngOnInit(): void {
    this.globals.userInfo.subscribe(it => {
      if (it.id === this.control.id) {
        this.form.controls[this.control.id].setValue(it.value);
      }
    });
  }

  get isValid() { return this.form.controls[this.control.id].valid; }
}
