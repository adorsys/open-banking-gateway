import { Component, OnInit } from '@angular/core';
import { FormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { RoutingPath } from '../models/routing-path.model';
import { NgClass, NgIf } from '@angular/common';
import { SharedModule } from '../common/shared.module';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: true,
  imports: [FormsModule, NgClass, NgIf, SharedModule]
})
export class LoginComponent implements OnInit {
  loginForm: UntypedFormGroup;

  constructor(private formBuilder: UntypedFormBuilder, private router: Router, private authService: AuthService) {}

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.pattern('^[a-zA-Z0-9]+$')]],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value).subscribe((success) => {
        if (success) {
          this.router.navigate([RoutingPath.BANK_SEARCH]);
        }
      });
    }
  }

  gmailOauth2Login() {
    this.authService.gmailOauth2Login().subscribe((res) => {
      window.location.href = res;
    });
  }

  get username() {
    return this.loginForm.get('username');
  }
  get password() {
    return this.loginForm.get('password');
  }
}
