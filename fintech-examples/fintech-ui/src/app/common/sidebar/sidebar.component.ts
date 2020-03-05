import { Component, OnInit } from '@angular/core';
import { BankProfileService } from '../../bank-search/services/bank-profile.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  services: Array<string>;
  bankId: string;
  bankName: string;

  constructor(private bankProfileService: BankProfileService, private route: ActivatedRoute, private router: Router) {}

  ngOnInit() {
    this.bankId = this.route.snapshot.paramMap.get('id');
    this.bankProfileService.getBankProfile(this.bankId).subscribe(response => {
      this.bankName = response.bankName;
      this.services = response.services;
    });
  }

  contains(service: string): boolean {
    return this.services.includes(service);
  }

  goTo(): void {
    this.router.navigate(['/dashboard', this.bankId]);
  }
}
