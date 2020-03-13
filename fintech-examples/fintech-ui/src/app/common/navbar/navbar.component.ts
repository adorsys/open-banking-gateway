import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { UserProfile } from '../../api';
import { UserService } from '../../bank/services/user-service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  public user: UserProfile;

  constructor(private authService: AuthService, private userService: UserService) {}

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.userService.currentUser.subscribe((response: UserProfile) => {
        this.user = response;
      });
      this.userService.loadUserInfo();
    }
  }

  onLogout(): void {
    this.authService.logout();
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }
}
