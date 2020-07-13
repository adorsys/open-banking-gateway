import { Component, OnInit } from '@angular/core';
import { StorageService } from '../services/storage.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  templateUrl: './bank.component.html',
  styleUrls: ['./bank.component.scss']
})
export class BankComponent implements OnInit {
  constructor(private router: Router, private storageService: StorageService) {}

  ngOnInit() {
    // this is added to register url where to forward
    // if LoA is cancelled after redirect page is displayed
    // to be removed when issue https://github.com/adorsys/open-banking-gateway/issues/848 is resolved
    // or Fintech UI refactored
    this.storageService.redirectCancelUrl = this.router.url;
  }
}
