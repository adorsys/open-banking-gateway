import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-account-reference',
  templateUrl: './account-reference.component.html',
  styleUrls: ['./account-reference.component.css']
})
export class AccountReferenceComponent implements OnInit {

  iban: FormControl = new FormControl();
  currency: FormControl = new FormControl();

  @Input() ibanValue: string;
  @Input() form: FormGroup;
  @Output() ibanValueChange = new EventEmitter();


  constructor() { }


  remove() {
    this.form.removeControl(this.ibanValue + '.iban');
    this.form.removeControl(this.ibanValue + '.currency');
  }

  change(newValue) {
    this.ibanValue = newValue;
    this.ibanValueChange.emit(newValue);
  }

  ngOnInit() {
    this.form.addControl(this.ibanValue + '.iban', this.iban);
    this.form.addControl(this.ibanValue + '.currency', this.currency);
  }
}
