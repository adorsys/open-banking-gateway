import { Component, NgZone, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { RoutingPath } from '../models/routing-path.model';

@Component({
  selector: 'app-session-expired',
  templateUrl: './session-expired.component.html',
  styleUrls: ['./session-expired.component.scss'],
  standalone: true
})
export class SessionExpiredComponent implements OnInit {
  constructor(private router: Router, private authService: AuthService, private ngZone: NgZone) {}

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.router.navigate([RoutingPath.BANK_SEARCH]);
    }
  }

  public proceed() {
    this.ngZone.run(() => this.router.navigate([RoutingPath.LOGIN]));
  }
}
