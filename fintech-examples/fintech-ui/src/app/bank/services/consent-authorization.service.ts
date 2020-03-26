import {FinTechAuthorizationService} from '../../api';
import {Router} from '@angular/router';
import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ConsentAuthorizationService {
  constructor(
    private router: Router,
    private authService: FinTechAuthorizationService
  ) {}

  fromConsentOk(redirectCode: string) {
    this.authService.fromConsentOkGET(
      'redirectState',
      redirectCode,
    '',
    '',
    'response'
  ).subscribe(resp => {
    console.log(resp);
      this.router.navigate([resp.headers.get('Location')]);
    });
  }
}
