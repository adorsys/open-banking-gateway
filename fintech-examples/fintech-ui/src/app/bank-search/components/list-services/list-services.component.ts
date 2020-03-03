import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BankProfileService } from '../../services/bank-profile.service';

@Component({
  selector: 'app-list-services',
  templateUrl: './list-services.component.html',
  styleUrls: ['./list-services.component.scss']
})
export class ListServicesComponent implements OnInit {
  services: string[];
  bankId: string;
  bankName: string;

  constructor(public bankProfileService: BankProfileService, public route: ActivatedRoute, public router: Router) {}

  ngOnInit() {
    this.bankId = this.route.snapshot.paramMap.get('id');
    this.bankProfileService.getBankProfile(this.bankId).subscribe(response => {
      this.bankName = response.bankName;
      this.services = response.services;
    });
  }

  goTo(): void {
    this.router.navigate(['/search/redirect', this.bankId]);
  }
}
