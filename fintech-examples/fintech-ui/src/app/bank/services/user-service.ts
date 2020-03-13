import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/internal/BehaviorSubject';
import { UserProfile } from '../../api';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private user = new BehaviorSubject<UserProfile>(null);
  currentUser = this.user.asObservable();
  private url = `${environment.FINTECH_API}`;

  constructor(private http: HttpClient) {}

  public getUserInfo() {
    return this.http.get<UserProfile>(`${this.url}/users/me`).pipe(tap(user => this.user.next(user)));
  }

  public loadUserInfo(): void {
    this.getUserInfo().subscribe(() => {});
  }

  /* public updateUserInfo(user: UserProfile): Observable<any> {
        return this.http.put(this.url + '/users', user);
    }*/
}
