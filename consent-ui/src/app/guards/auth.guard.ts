import { Injectable } from '@angular/core';
import { CanActivate } from '@angular/router';
import {SessionService} from '../common/session.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private sessionService: SessionService
  ) {
  }

  canActivate():boolean {
    if (this.sessionService.isLoggedIn()){
      return true;
    } else {
      // implement navigate to login here
      return false
    }
  }

}
