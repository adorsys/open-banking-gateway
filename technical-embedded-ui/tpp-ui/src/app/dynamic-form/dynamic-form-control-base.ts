const KnownTypes = ['BOOLEAN', 'INTEGER', 'STRING', 'DATE', 'OBJECT'];

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
  type: string;
  message: string;
  required: boolean;
  order: number;

  // TODO Move to children:
  options: {key: string, value: string}[] = [];

  constructor(
    id: string,
    code: string,
    type: string,
    scope: string,
    message: string,
    public target: Target
  ) {
    this.id = id;
    this.code = code || '';
    this.type = type;
    this.message = message || '';
    this.order = 1;
    this.validation();
  }

  private validation() {

    if (KnownTypes.indexOf(this.type) < 0) {
      throw new SyntaxError("Wrong control type " + this.type);
    }
  }
}

export enum Target {
  GENERAL = 'GENERAL',
  AIS_CONSENT = 'AIS_CONSENT'
}
