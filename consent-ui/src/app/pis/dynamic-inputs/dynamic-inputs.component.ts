import {Component, Input, OnInit} from '@angular/core';
import {AuthViolation} from "../../api";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AisConsentToGrant} from "../../ais/common/dto/ais-consent";

@Component({
  selector: 'consent-app-dynamic-inputs',
  templateUrl: './dynamic-inputs.component.html',
  styleUrls: ['./dynamic-inputs.component.scss']
})
export class DynamicInputsComponent implements OnInit {
  @Input() violations: AuthViolation[];
  @Input() targetForm: FormGroup;
  @Input() currentConsent: AisConsentToGrant;

  constructor() {}

  ngOnInit() {
    this.violations.forEach(it => this.targetForm.addControl(it.code, new FormControl('', Validators.required)));

    if (this.currentConsent && this.currentConsent.extras) {
      this.violations
        .filter(it => this.targetForm.get(it.code) && this.currentConsent.extras[it.code])
        .forEach(it => this.targetForm.get(it.code).setValue(this.currentConsent.extras[it.code]));
    }
  }
}
