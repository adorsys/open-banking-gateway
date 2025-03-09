import {Component, Input, OnInit} from '@angular/core';
import {UntypedFormControl, UntypedFormGroup, Validators} from '@angular/forms';
import {AuthViolation} from '../../../../../api';
import {AisConsentToGrant} from '../../../../common/dto/ais-consent';

@Component({
  selector: 'consent-app-dynamic-inputs',
  templateUrl: './dynamic-inputs.component.html',
  styleUrls: ['./dynamic-inputs.component.scss']
})
export class DynamicInputsComponent implements OnInit {
  @Input() violations: AuthViolation[];
  @Input() targetForm: UntypedFormGroup;
  @Input() currentConsent: AisConsentToGrant;

  constructor() {}

  ngOnInit() {
    this.violations.forEach(it => this.targetForm.addControl(it.code, new UntypedFormControl('', Validators.required)));

    if (this.currentConsent && this.currentConsent.extras) {
      this.violations
        .filter(it => this.targetForm.get(it.code) && this.currentConsent.extras[it.code])
        .forEach(it => this.targetForm.get(it.code).setValue(this.currentConsent.extras[it.code]));
    }
  }
}
