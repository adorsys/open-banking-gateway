import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'three-state-checkbox',
  templateUrl: './three-state-checkbox.component.html',
  styleUrls: ['./three-state-checkbox.component.css']
})
export class ThreeStateCheckboxComponent implements OnInit {

  @Input() label: string;
  @Input() controlName: string;
  @Input() form: FormGroup;

  state = null;
  tape = [null, true, false];

  checkbox = new FormControl();

  constructor() { }

  ngOnInit() {
    this.form.removeControl(this.controlName); // FIXME, unknown why needed
    this.form.addControl(this.controlName, this.checkbox);
  }
}
