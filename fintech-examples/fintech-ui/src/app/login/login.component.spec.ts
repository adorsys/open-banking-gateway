import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { By } from '@angular/platform-browser';
import { of } from 'rxjs';

import { LoginComponent } from './login.component';
import { BankSearchComponent } from '../bank-search/bank-search.component';
import { BankSearchModule } from '../bank-search/bank-search.module';
import { DocumentCookieService } from '../services/document-cookie.service';
import { AuthService } from '../services/auth.service';
import { RoutingPath } from '../models/routing-path.model';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let authServiceSpy;
  let el: HTMLElement;
  let router: Router;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        BankSearchModule,
        ReactiveFormsModule,
        HttpClientModule,
        RouterTestingModule.withRoutes([{ path: RoutingPath.BANK_SEARCH, component: BankSearchComponent }])
      ],
      providers: [AuthService, DocumentCookieService],
      declarations: [LoginComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.get(AuthService);
    router = TestBed.get(Router);
    fixture.detectChanges();
  });

  afterEach(() => {
    TestBed.resetTestingModule();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call login on the service', () => {
    authServiceSpy = spyOn(authService, 'login').and.returnValue(of(true));
    const routerSpy = spyOn(router, 'navigate');

    const form = component.loginForm;
    form.controls['username'].setValue('test');
    form.controls['password'].setValue('12345');

    el = fixture.debugElement.query(By.css('button')).nativeElement;
    el.click();

    expect(authServiceSpy).toHaveBeenCalledWith({ username: 'test', password: '12345' });
    expect(routerSpy).toHaveBeenCalledWith([RoutingPath.BANK_SEARCH]);
  });

  it('loginForm should be invalid when at least one field is empty', () => {
    expect(component.loginForm.valid).toBeFalsy();
  });

  it('username field validity', () => {
    let errors = {};
    const username = component.loginForm.controls['username'];
    expect(username.valid).toBeFalsy();

    // username field is required
    errors = username.errors || {};
    expect(errors['required']).toBeTruthy();

    // set login to something correct
    username.setValue('test');
    errors = username.errors || {};
    expect(errors['required']).toBeFalsy();
  });

  it('password field validity', () => {
    let errors = {};
    const password = component.loginForm.controls['password'];
    expect(password.valid).toBeFalsy();

    // password field is required
    errors = password.errors || {};
    expect(errors['required']).toBeTruthy();

    // set password to something correct
    password.setValue('12345');
    errors = password.errors || {};
    expect(errors['required']).toBeFalsy();
  });

  it('should navigate after successful login', () => {
    const form = component.loginForm;
    form.controls['username'].setValue('username');
    const username = component.username;
    expect(username.value).toEqual('username');
  });
});
