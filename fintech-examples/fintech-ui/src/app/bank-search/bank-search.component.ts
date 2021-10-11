import {Component} from '@angular/core';
import {BankSearchService} from './services/bank-search.service';
import {ActivatedRoute, Router} from '@angular/router';
import {StorageService} from '../services/storage.service';
import {TimerService} from '../services/timer.service';
import {RoutingPath} from '../models/routing-path.model';

@Component({
  selector: 'app-bank-search',
  templateUrl: './bank-search.component.html',
  styleUrls: ['./bank-search.component.scss']
})
export class BankSearchComponent {
  searchedBanks: BankSearchInfo[] = [];
  selectedBankProfile: string;

  constructor(
    private bankSearchService: BankSearchService,
    private storageService: StorageService,
    private route: ActivatedRoute,
    private router: Router,
    private timerService: TimerService
  ) {
    this.timerService.startTimer();
  }

  onSearch(keyword: string): void {
    if (keyword && keyword.trim()) {
      this.bankSearchService.searchBanks(keyword).subscribe((bankDescriptor) => {
        this.searchedBanks = [];
        for (const descriptor of bankDescriptor.bankDescriptor) {
          if (!descriptor.profiles) {
            continue
          }

          this.searchedBanks.push(...descriptor.profiles.map(it => new BankSearchInfo(`[${it.protocolType}${null === it.name ? '' : ',' + it.name}] ${it.bankName}`, it.uuid)))
        }
      });
    } else {
      this.bankUnselect();
    }
  }

  onBankSelect(profile: BankSearchInfo): void {
    this.selectedBankProfile = profile.uuid;
    this.storageService.setBankName(profile.name);
    this.router.navigate([RoutingPath.BANK, profile.uuid]);
  }

  private bankUnselect(): void {
    this.searchedBanks = [];
    this.selectedBankProfile = null;
  }
}

export class BankSearchInfo {
  name: string
  uuid: string

  constructor(name: string, profileId: string) {
    this.name = name;
    this.uuid = profileId;
  }
}
