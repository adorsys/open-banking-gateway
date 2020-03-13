import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Consts } from '../consts';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  constructor(private authService: AuthService) {}

  ngOnInit() {}

  onLogout(): void {
    this.authService.logout();
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  getUserName(): string {
    return localStorage.getItem(Consts.USERNAME);
  }
}
