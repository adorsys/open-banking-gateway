import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {BankProfileService} from '../../bank-search/services/bank-profile.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  showListAccounts = false;
  showListTransactions = false;
  showInitiatePayment = false;
  showSettings = true;
  bankId: string;
  bankName: string;

  constructor(
    private bankProfileService: BankProfileService,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnInit() {
    this.bankId = this.route.snapshot.paramMap.get('bankid');
    this.getBankProfile(this.bankId);
  }

  getBankProfile(id: string) {
    this.bankProfileService.getBankProfile(id).subscribe((response) => {
      this.bankName = response.bankName;
      this.showListAccounts = response.services.includes('LIST_ACCOUNTS');
      this.showListTransactions = response.services.includes('LIST_TRANSACTIONS');
      this.showInitiatePayment = response.services.includes('SINGLE_PAYMENT');
    });
  }

  getRouterLinkListAccounts(): string {
    return this.showListAccounts ? 'accounts' : '.';
  }

  onInitiatePayment() {
    this.router.navigate(['payment'], {relativeTo: this.route});
  }

  get showPaymentNav(): boolean {
    return this.showInitiatePayment;
  }
}
