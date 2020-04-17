import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CustomValidators } from '../../utilities/customValidators';
import { AuthService } from '../../common/auth.service';
import { LoginComponent } from '../login/login.component';
import { PsuAuthBody } from '../../api-auth';

@Component({
  selector: 'consent-app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  public static ROUTE = 'register';
  loginForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.loginForm = this.formBuilder.group(
      {
        login: ['', Validators.required],
        password: ['', Validators.required],
        confirmPassword: ['', Validators.required]
      },
      { validators: CustomValidators.compareFields('password', 'confirmPassword') }
    );
  }
  onSubmit() {
    const credentials: PsuAuthBody = {
      login: this.loginForm.value.login,
      password: this.loginForm.value.password
    };
    this.authService.userRegister(credentials).subscribe(
      res => {
        if (res.status === 201) {
          this.router.navigate([LoginComponent.ROUTE], { relativeTo: this.route.parent });
        }
      },
      error => {
        console.log(error);
      }
    );
  }

  get login() {
    return this.loginForm.get('login');
  }
  get password() {
    return this.loginForm.get('password');
  }
  get confirmPassword() {
    return this.loginForm.get('confirmPassword');
  }
}
