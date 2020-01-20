import {Component, Input, OnInit} from '@angular/core';
import {FormGroup} from "@angular/forms";

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

  constructor() { }

  ngOnInit() {
  }

}
