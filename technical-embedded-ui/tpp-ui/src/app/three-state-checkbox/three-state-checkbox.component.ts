import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {Globals} from "../globals";

@Component({
  selector: 'app-three-state-checkbox',
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

  constructor(private globals: Globals) { }

  ngOnInit() {
    this.form.removeControl(this.controlName); // FIXME, unknown why needed
    this.form.addControl(this.controlName, this.checkbox);

    this.globals.userInfo.subscribe(it => {
      if (it.id === this.controlName) {
        this.state = it.value;
      }
    });
  }
}
