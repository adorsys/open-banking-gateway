import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  templateUrl: './bank.component.html',
  styleUrls: ['./bank.component.scss']
})
export class BankComponent implements OnInit {
  constructor(private router: Router) {}

  ngOnInit() {}

  backToPreviousPage() {
    this.router.navigate(['/search']);
  }
}
