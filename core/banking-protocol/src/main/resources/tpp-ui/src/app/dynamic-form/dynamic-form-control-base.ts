const KnownControlTypes = ['textbox', 'dropdown', 'boolean', 'accountaccess', 'date'];
const KnownTypeValidations = ['string', 'ipaddr', 'class', 'boolean', 'integer'];

// Validations are defined in format:
// <controlType>.<valueType>
// I.e.: 'textbox.string'
// where:
// controlType can be: textbox, dropdown, ...
// valueType can be: string, int, decimal, enum ...

export class DynamicFormControlBase<T> {
  value: T;
  id: string;
  code: string;
  message: string;
  type: string;
  required: boolean;
  order: number;
  controlType: string;

  // TODO Move to children:
  options: {key: string, value: string}[] = [];

  constructor(
    id: string,
    code: string,
    message: string
  ) {
    this.id = id;
    this.code = code || '';
    this.message = message || '';
    this.order = 1;
    this.parseAndValidateCode(this.code);
  }

  private parseAndValidateCode(code: string) {
    let parts = code.split('.');

    if (parts.length < 2) {
      throw new SyntaxError("Wrong code " + code);
    }

    this.controlType = parts[0];
    this.type = parts[1];
    this.validation();
  }

  private validation() {

    if (KnownControlTypes.indexOf(this.controlType) < 0) {
      throw new SyntaxError("Wrong control type " + this.controlType);
    }
    if (KnownTypeValidations.indexOf(this.type) < 0) {
      throw new SyntaxError("Wrong type validator " + this.type);
    }
  }
}
