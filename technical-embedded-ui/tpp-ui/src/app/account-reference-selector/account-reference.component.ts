import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-account-reference',
  templateUrl: './account-reference.component.html',
  styleUrls: ['./account-reference.component.css']
})
export class AccountReferenceComponent implements OnInit {

  iban: FormControl = new FormControl();
  currency: FormControl = new FormControl();

  @Input() form: FormGroup;
  @Input() elemId: number;
  @Input() prefix = '';

  ibanName: string;
  currencyName: string;

  constructor() { }

  static buildWithId(elemId: number): AccountReferenceComponent {
    const result = new AccountReferenceComponent();
    result.elemId = elemId;
    return result;
  }

  remove() {
    this.form.removeControl(this.ibanName);
    this.form.removeControl(this.currencyName);
  }

  ngOnInit() {
    this.ibanName = this.prefix + 'iban';
    this.currencyName = this.prefix + 'currency';

    this.form.addControl(this.ibanName, this.iban);
    this.form.addControl(this.currencyName, this.currency);
  }
}
