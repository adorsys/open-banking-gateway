const KnownControlTypes = ['textbox', 'dropdown'];
const KnownTypeValidations = ['string', 'ipaddr'];
const MagicKeyForInput = 'input.';

// Validations are defined in format:
// input.<controlType>.<valueType>.<...> - 'input' is magic anchor to find code in message template
// I.e.: 'input.textbox.string.no.psu.id'
// where:
// controlType can be: textbox, dropdown, ...
// valueType can be: string, int, decimal, enum ...

export class DynamicFormControlBase<T> {
  value: T;
  code: string;
  message: string;
  type: string;
  required: boolean;
  order: number;
  controlType: string;

  // TODO Move to children:
  options: {key: string, value: string}[] = [];

  constructor(
    code: string,
    message: string
  ) {
    this.code = code || '';
    this.message = message || '';
    this.order = 1;
    this.parseAndValidateCode(this.code);
  }

  private parseAndValidateCode(code: string) {
    let start = code.indexOf(MagicKeyForInput);
    if (start < 0) {
      throw new SyntaxError("Wrong code " + code);
    }

    let parts = code.substr(start + MagicKeyForInput.length).replace('}', '').split('.');

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
