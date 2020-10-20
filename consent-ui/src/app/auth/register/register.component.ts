import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CustomValidators } from '../../utilities/customValidators';
import { AuthService } from '../../common/auth.service';
import { LoginComponent } from '../login/login.component';
import { PsuAuthBody } from '../../api-auth';
import { SessionService } from '../../common/session.service';
import { CustomizeService } from '../../services/customize.service';

@Component({
  selector: 'consent-app-register',
  templateUrl: './register.component.html',
  styleUrls: ['../auth.component.scss']
})
export class RegisterComponent implements OnInit {
  public static ROUTE = 'register';
  registerForm: FormGroup;

  private authId: string;
  private redirectCode: string;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private activatedRoute: ActivatedRoute,
    private sessionService: SessionService,
    public customizeService: CustomizeService
  ) {}

  ngOnInit() {
    this.registerForm = this.formBuilder.group(
      {
        login: ['', Validators.required],
        password: ['', Validators.required],
        confirmPassword: ['', Validators.required]
      },
      { validators: CustomValidators.compareFields('password', 'confirmPassword') }
    );

    this.authId = this.activatedRoute.snapshot.parent.params.authId;
    this.redirectCode = this.sessionService.getRedirectCode(this.authId);
  }

  onSubmit() {
    const credentials: PsuAuthBody = {
      login: this.registerForm.value.login,
      password: this.registerForm.value.password
    };
    this.authService.userRegister(credentials).subscribe((res) => {
      if (res.status === 201) {
        this.router.navigate([LoginComponent.ROUTE], {
          relativeTo: this.activatedRoute.parent
        });
      }
    });
  }

  get login() {
    return this.registerForm.get('login');
  }

  get password() {
    return this.registerForm.get('password');
  }

  get confirmPassword() {
    return this.registerForm.get('confirmPassword');
  }
}
