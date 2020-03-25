import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Consts } from '../consts';
import { Router } from '@angular/router';
import { DocumentCookieService } from '../../services/document-cookie.service';
import {LocalStorage} from "../local-storage";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  constructor(private authService: AuthService, private cookieService: DocumentCookieService, private router: Router) {}

  ngOnInit() {}

  onLogout() {
    if (this.isLoggedIn()) {
      this.authService.logout().subscribe(ok => console.log('logout ok'), notok => console.log('logout NOT-OK'));
    } else {
      this.router.navigate(['/login']);
    }
  }

  isLoggedIn(): boolean {
    return LocalStorage.isLoggedIn();
  }

  getUserName(): string {
    return localStorage.getItem(Consts.LOCAL_STORAGE_USERNAME);
  }
}
