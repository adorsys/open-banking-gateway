import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, ActivatedRouteSnapshot, Router} from '@angular/router';
import { CustomValidators } from '../../utilities/customValidators';
import {AuthService} from '../../common/auth.service';
import {LoginComponent} from "../login/login.component";

@Component({
  selector: 'consent-app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  public static ROUTE = 'register';
  loginForm: FormGroup;
  private route: ActivatedRouteSnapshot;
  private redirectCode: string;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private activatedRoute: ActivatedRoute ) { }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      id: ['', Validators.required],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required]
    }, { validators: CustomValidators.compareFields( 'password', 'confirmPassword' ) });

    this.route = this.activatedRoute.snapshot;
    this.redirectCode = this.route.queryParams.redirectCode;
  }
  onSubmit(){
    const userObj = {
      id: this.loginForm.value.id,
      password: this.loginForm.value.password
    }
    this.authService.userRegister(userObj).subscribe(
      res => {
      if (res.status === 201 ) {
        this.router.navigate( [LoginComponent.ROUTE] );
      }
    },
      error => { console.log(error);
    });
  }

  get id() {
    return this.loginForm.get('id');
  }
  get password() {
    return this.loginForm.get('password');
  }
  get confirmPassword() {
    return this.loginForm.get('confirmPassword');
  }

}
