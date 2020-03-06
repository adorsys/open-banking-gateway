import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ConsentAuthorizationService} from '../../api/consentAuthorization.service';

@Component({
  selector: 'consent-app-entry-page-transactions',
  templateUrl: './entry-page-transactions.component.html',
  styleUrls: ['./entry-page-transactions.component.scss']
})
export class EntryPageTransactionsComponent implements OnInit {

  public finTechName = 'Awesome FinTech';
  public bankName = 'Adorsys Sandbox';

  public accounts = [
    new TransactionsOnAccountAccess('IBAN123456', true),
    new TransactionsOnAccountAccess('IBAN789168', true)
  ];

  public transactionsAccessForm: FormGroup;

  constructor(private formBuilder: FormBuilder, private consentAuthorisation: ConsentAuthorizationService) {
    this.transactionsAccessForm = this.formBuilder.group({});
  }

  ngOnInit() {
  }

  onSubmit() {
  }

  handleObjectSelectedEvent(container: TransactionsOnAccountAccess): void {
    container.checked = !container.checked;
  }
}

export class TransactionsOnAccountAccess {

  constructor(public accountIban: string, public checked: boolean) {
  }
}

