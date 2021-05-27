import {AbstractControl, ValidatorFn} from '@angular/forms';

export class DateUtil {
  public static getActualDate(): string {
    const result = new Date();
    result.setDate(result.getDate());
    return result.toISOString().split('T')[0];
  }

  public static isDateNotInThePastValidator(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null => {

      if(!(control && control.value)) {
        return null;
      }

      const actualDate = new Date(this.getActualDate());
      const date = new Date(control.value);

      return date < actualDate
        ? { invalidDate: true }
        : null;
    }
  }
}
