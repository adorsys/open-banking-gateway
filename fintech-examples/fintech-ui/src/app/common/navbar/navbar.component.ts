import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Consts } from '../consts';
import { Router } from '@angular/router';
import { DocumentCookieService } from '../../services/document-cookie.service';

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
    this.authService.deleteAllCookies();
    this.authService.openLoginPage();
  }

  isLoggedIn(): boolean {
    return this.cookieService.exists(Consts.COOKIE_NAME_XSRF_TOKEN);
  }

  getUserName(): string {
    return localStorage.getItem(Consts.LOCAL_STORAGE_USERNAME);
  }
}
