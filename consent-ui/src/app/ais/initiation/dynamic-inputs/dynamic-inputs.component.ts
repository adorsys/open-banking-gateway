import {Component, Input, OnInit} from '@angular/core';
import {AuthViolation} from "../../common/dto/auth-state";
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'consent-app-dynamic-inputs',
  templateUrl: './dynamic-inputs.component.html',
  styleUrls: ['./dynamic-inputs.component.scss']
})
export class DynamicInputsComponent implements OnInit {

  @Input() violations: AuthViolation[];
  @Input() targetForm: FormGroup;

  constructor() { }

  ngOnInit() {
    this.violations.forEach(
      it => this.targetForm.addControl(it.code, new FormControl('', Validators.required))
    );
  }
}
