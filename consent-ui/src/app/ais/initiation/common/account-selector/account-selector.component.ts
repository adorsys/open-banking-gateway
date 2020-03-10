import {Component, Input, OnInit} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {uuid} from "uuidv4";

@Component({
  selector: 'consent-app-account-selector',
  templateUrl: './account-selector.component.html',
  styleUrls: ['./account-selector.component.scss']
})
export class AccountSelectorComponent implements OnInit {

  @Input() targetForm: FormGroup;
  @Input() accounts: Account[];

  constructor() { }

  ngOnInit() {
  }

}

export class Account {
  // internally generated unique ID
  id: string;
  iban: string;


  constructor(iban?: string) {
    this.id = "account:" + uuid();
    this.iban = iban;
  }
}
