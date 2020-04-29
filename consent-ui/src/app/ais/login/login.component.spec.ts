import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {ReactiveFormsModule} from '@angular/forms';
import {LoginComponent} from './login.component';
import {RouterTestingModule} from '@angular/router/testing';
import {ActivatedRoute, ActivatedRouteSnapshot, Params} from '@angular/router';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {Observable, of} from 'rxjs';
import {HttpHeaders, HttpResponse} from '@angular/common/http';
import {AuthService} from '../../common/auth.service';

export class MockActivatedRoute {
  snapshot: ActivatedRouteSnapshot;
  param: Observable<Params>
}

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let route;
  let authServiceSpy;
  let authService: AuthService;
  let headersOpt = new HttpHeaders({ 'Location': 'httpw://localhost:9876/?id=77168991' });
  let response = new HttpResponse({ body: { xsrfToken: 'tokenHere' }, headers: headersOpt, status: 200, statusText: 'geht' });
  let form;

  beforeEach(async(() => {
    route = new MockActivatedRoute();
    route.snapshot = {
      queryParams: {redirectCode: 'redirectCode654'},
      parent: { params: {authId: 'authIdHere'} }
    };

    TestBed.configureTestingModule({
      declarations: [ LoginComponent ],
      imports: [ReactiveFormsModule, HttpClientTestingModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    authService = fixture.debugElement.injector.get(AuthService);
    component = fixture.componentInstance;
    fixture.detectChanges();
    form = component.loginForm;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be true if the form is invalid', () => {
    authServiceSpy = spyOn(authService, 'userLoginForConsent').and
      .returnValue( of(response) );

    form.controls['login'].setValue('alex');
    form.controls['password'].setValue('');
    component.onSubmit();
    fixture.detectChanges();

    expect(component.loginForm.invalid).toBe(true);
  });
  it('should call login service', () => {
    authServiceSpy = spyOn(authService, 'userLoginForConsent').and.callThrough();

    let authID = route.snapshot.parent.params.authId;
    let redirectCode = 'redirectCode654';
    form.controls['login'].setValue('alex');
    form.controls['password'].setValue('1234');
    component.onSubmit();
    fixture.detectChanges();

    expect(authServiceSpy).toHaveBeenCalledWith(authID, redirectCode, { login: 'alex', password: '1234' });
  });
  it('should be invalid if password is not set', () => {
    authServiceSpy = spyOn(authService, 'userLoginForConsent').and
      .returnValue( of(response) );

    form.controls['login'].setValue('alex');
    form.controls['password'].setValue('');
    component.onSubmit();
    fixture.detectChanges();

    expect(component.loginForm.invalid).toBe(true);
  });
  it('should be invalid if username is not set', () => {
    authServiceSpy = spyOn(authService, 'userLoginForConsent').and.returnValue(of(response));

    form.controls['login'].setValue('');
    form.controls['password'].setValue('asdasf');
    component.onSubmit();
    fixture.detectChanges();

    expect(component.loginForm.invalid).toBe(true);
  });
});
