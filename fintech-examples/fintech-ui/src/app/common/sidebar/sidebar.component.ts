import { Component, OnInit } from '@angular/core';
import { BankProfileService } from '../../bank-search/services/bank-profile.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  showListAccounts = false;
  showListTransactions = false;
  showInitiatePayment = false;
  bankId: string;
  bankName: string;

  constructor(private bankProfileService: BankProfileService, private route: ActivatedRoute) {}

  ngOnInit() {
    this.bankId = this.route.snapshot.paramMap.get('id');
    console.log('bankid', this.bankId);
    this.bankProfileService.getBankProfile(this.bankId).subscribe(response => {
      console.log('bank profile returns:' + JSON.stringify(response));
      this.bankName = response.bankName;
      this.showListAccounts = response.services.includes('LIST_ACCOUNTS');
      this.showListTransactions = response.services.includes('LIST_TRANSACTIONS');
      this.showInitiatePayment = response.services.includes('UPDATE_AUTHORIZATION');
    });
  }
}
