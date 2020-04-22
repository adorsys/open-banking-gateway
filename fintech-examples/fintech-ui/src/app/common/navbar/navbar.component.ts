import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {StorageService} from '../../services/storage.service';
import {Consts} from "../../models/consts";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  constructor(private authService: AuthService, private storageService: StorageService) {
  }

  ngOnInit() {
  }

  onLogout() {
    this.authService.logout();
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  getUserName(): string {
    return this.storageService.getUserName();
  }

  getSessionValidUntil(): string {
    const validUntilDate: Date = this.storageService.getValidUntilDate();
    if (validUntilDate != null) {
      const validUntilDateString = Consts.toLocaleString(validUntilDate);
      let regEx = /.*([0-9]{2}:[0-9]{2}:[0-9]{2})/;
      let matches = validUntilDateString.match(regEx);
      if (matches.length != 2) {
        throw "valid until is not parsable " + validUntilDateString;
      }
      return matches[1];
    }
    return "";

  }
}
