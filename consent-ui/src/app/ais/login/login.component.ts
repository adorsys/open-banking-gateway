import {Component, OnDestroy, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, ActivatedRouteSnapshot, Router} from '@angular/router';
import {SessionService} from "../../common/session.service";
import {Subscription} from 'rxjs';
import {AuthService} from '../../common/auth.service';
import {EntryPageTransactionsComponent} from '../entry-page/initiation/transactions/entry-page-transactions/entry-page-transactions.component';
import {ConsentAuth} from "../../api";
import {AuthConsentState} from "../common/dto/auth-state";
import {EntryPageAccountsComponent} from "../entry-page/initiation/accounts/entry-page-accounts/entry-page-accounts.component";
import ActionEnum = ConsentAuth.ActionEnum;

@Component({
  selector: 'consent-app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, OnDestroy {
  public static ROUTE = 'login';

  loginForm: FormGroup;
  private subLogin: Subscription;
  private username: FormControl | AbstractControl;
  private pwd: FormControl | AbstractControl;

  private route: ActivatedRouteSnapshot;
  private redirectCode: string;

  constructor(
    private formBuilder: FormBuilder,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private sessionService: SessionService,
    private authService: AuthService,
  ) { }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      id: ['', Validators.required],
      password: ['', Validators.required]
    });
    this.username = this.loginForm.get(['id']);
    this.pwd = this.loginForm.get(['password']);

    this.route = this.activatedRoute.snapshot;

    const authId = this.route.parent.params.authId;
     this.redirectCode = this.route.queryParams.redirectCode;
    console.log(this.redirectCode);
  }

  onSubmit(){
    this.subLogin = this.authService.userLogin(this.loginForm.value)
      .subscribe(
        res => {
          console.log(res);
          // store xsrf-token in session-storage
          const xsrfToken = res.body.xsrfToken;
          this.sessionService.setXsrfToken(xsrfToken);
          // navigate to transactions
          this.router.navigate(['../'],
            { relativeTo: this.activatedRoute, queryParams: { redirectCode: this.redirectCode } });
        },
          error => { console.log(error) } );
  }

  ngOnDestroy(): void {
    if (this.subLogin) {
      this.subLogin.unsubscribe();
    }
  }


}
