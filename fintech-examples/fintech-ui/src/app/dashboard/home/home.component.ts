import { Component, OnDestroy, OnInit } from '@angular/core';
import { AisService } from '../services/ais.service';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { concatMap } from 'rxjs/operators';
import { AccountDetails } from '../../api';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {
  private accountsSubscription: Subscription;
  private showAccounts = false;
  private accounts: AccountDetails[];
  private bankID = '';

  constructor(private route: ActivatedRoute, private aisService: AisService) {}

  ngOnInit() {
    this.route.params.forEach(param => {
      this.aisService.getAccounts(param.id).subscribe(accounts => {
        this.bankID = param.id;
        this.accounts = accounts;
        this.showAccounts = true;
      });
    });
  }

  ngOnDestroy(): void {
    this.accountsSubscription.unsubscribe();
  }
}
