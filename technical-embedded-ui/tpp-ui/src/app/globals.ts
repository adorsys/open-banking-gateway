import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';

@Injectable()
export class Globals {
  userInfoPublished: BehaviorSubject<UserInfo> = new BehaviorSubject<UserInfo>(new UserInfo('', ''));
  userInfo = this.userInfoPublished.asObservable();
}

export class UserInfo {

  constructor(public id, public value) {
  }
}
