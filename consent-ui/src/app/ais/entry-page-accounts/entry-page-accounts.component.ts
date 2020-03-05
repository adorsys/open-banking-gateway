import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'consent-app-entry-page-accounts',
  templateUrl: './entry-page-accounts.component.html',
  styleUrls: ['./entry-page-accounts.component.scss']
})
export class EntryPageAccountsComponent implements OnInit {

  public finTechName = 'Awesome FinTech';
  public bankName = 'Adorsys Sandbox';

  public accountAccesses = [
    new AccountAccess('1', 'Allow to see list of all your accounts'),
    new AccountAccess('2', 'Limit access to specific accounts')
  ];
  public selectedAccess = this.accountAccesses[0];

  public accountAccessForm: FormGroup;

  constructor(private formBuilder: FormBuilder) {
    this.accountAccessForm = this.formBuilder.group({
      accountAccess: ['', Validators.required],
    });
  }

  ngOnInit() {
  }

  handleMethodSelectedEvent(scaMethod: AccountAccess) {
    this.selectedAccess = scaMethod;
  }
}

export class AccountAccess {
  constructor(public id: string, public message: string) {
  }
}
