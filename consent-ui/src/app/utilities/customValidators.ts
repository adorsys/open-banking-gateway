import { UntypedFormGroup, ValidatorFn } from '@angular/forms';

export class CustomValidators {
  static readonly compareFields: (controlName: string, matchingControlName: string) => ValidatorFn = (
    controlName: string,
    matchingControlName: string
  ) => {
    return (formGroup: UntypedFormGroup) => {
      const control = formGroup.controls[controlName];
      const matchingControl = formGroup.controls[matchingControlName];

      if (matchingControl.errors && !matchingControl.errors.compareFields) {
        // return if another validator has already found an error on the matchingControl
        return;
      }
      if (control.value !== matchingControl.value) {
        matchingControl.setErrors({ compareFields: { valid: false, msg: 'Passwords are not identical.' } });
      } else {
        matchingControl.setErrors(null);
      }
      return null;
    };
  };
}
