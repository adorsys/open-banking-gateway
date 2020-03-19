import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginComponent } from './login.component';
import { AuthService } from '../services/auth.service';
import { DebugElement } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { By } from '@angular/platform-browser';
import { SearchComponent } from '../bank-search/common/search/search.component';
import { BankSearchModule } from '../bank-search/bank-search.module';
import { ShareModule } from '../common/share.module';
import { DocumentCookieService } from '../services/document-cookie.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let authServiceSpy;
  let de: DebugElement;
  let el: HTMLElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        ShareModule,
        BankSearchModule,
        RouterTestingModule.withRoutes([{ path: 'search', component: SearchComponent }])
      ],
      providers: [AuthService, DocumentCookieService],
      declarations: [LoginComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;

    authService = fixture.debugElement.injector.get(AuthService);
    de = fixture.debugElement.query(By.css('form'));
    el = de.nativeElement;

    fixture.detectChanges();
    component.ngOnInit();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call login on the service', () => {
    authServiceSpy = spyOn(authService, 'login').and.callThrough();

    const form = component.loginForm;
    form.controls['username'].setValue('test');
    form.controls['password'].setValue('12345');

    el = fixture.debugElement.query(By.css('button')).nativeElement;
    el.click();

    expect(authServiceSpy).toHaveBeenCalledWith({ username: 'test', password: '12345' });
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
});
