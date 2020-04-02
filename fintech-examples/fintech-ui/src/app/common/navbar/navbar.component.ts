import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Consts } from '../../models/consts';
import { Router } from '@angular/router';
import { DocumentCookieService } from '../../services/document-cookie.service';
import { LocalStorage } from '../../models/local-storage';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  constructor(private authService: AuthService, private cookieService: DocumentCookieService, private router: Router) {}

  ngOnInit() {}

  onLogout() {
    this.authService.logout().subscribe(
      ok => this.logOutHelper(),
      notok => {
        this.logOutHelper();
      }
    );
  }

  logOutHelper() {
    LocalStorage.logout();
    this.authService.openLoginPage();
  }

  isLoggedIn(): boolean {
    return LocalStorage.isLoggedIn();
  }

  getUserName(): string {
    return localStorage.getItem(Consts.LOCAL_STORAGE_USERNAME);
  }
}
