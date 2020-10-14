import {Component, OnInit} from '@angular/core';
import {BankProfileService} from '../../bank-search/services/bank-profile.service';
import {ActivatedRoute, Router} from '@angular/router';
import {StorageService} from '../../services/storage.service';
import {Consts} from '../../models/consts';
import {AccountStruct} from "../redirect-page/redirect-struct";

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
    private storageService: StorageService
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
    if (!this.isLoaDone()) {
      this.router.navigate(['payment/account', Consts.RANDOM_ACCOUNT_ID, 'payments'], {relativeTo: this.route});
    } else {
      this.router.navigate(['payment'], {relativeTo: this.route});
    }
  }

  isLoaDone(): boolean {
    const loa: AccountStruct[] = this.storageService.getLoa(this.bankId);
    return loa !== null && (loa.length == 1? loa[0].resourceId !== Consts.RANDOM_ACCOUNT_ID : false);
  }

  get showPaymentNav(): boolean {
    return this.showInitiatePayment;
  }
}
