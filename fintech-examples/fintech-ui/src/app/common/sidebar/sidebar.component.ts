import { Component, OnInit } from '@angular/core';
import { BankProfileService } from '../../bank-search/services/bank-profile.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  services: string[] = [];
  bankId: string;
  bankName: string;

  constructor(private bankProfileService: BankProfileService, private route: ActivatedRoute) {}

  ngOnInit() {
    this.bankId = this.route.snapshot.paramMap.get('id');
    console.log('bankid', this.bankId);
    this.getBankInfos();
  }

  getBankInfos() {
    this.bankProfileService.getBankProfile(this.bankId).subscribe(response => {
      this.bankName = response.bankName;
      this.services = response.services;
      localStorage.setItem('services', JSON.stringify(this.services));
      console.log('list-services', this.services);
    });
  }

  contains(service: string) {
    this.services = JSON.parse(localStorage.getItem('services'));
    if (this.services == null) {
      return false;
    } else {
      this.services.forEach(value => {
        return value === service;
      });
    }
  }
}
