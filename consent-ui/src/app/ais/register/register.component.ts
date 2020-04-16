import { Component, OnInit } from '@angular/core';
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import { Helperfunctions } from '../../utilities/helperfunctions';
import {Subscription} from "rxjs";
import {AuthService} from '../../common/auth.service';

@Component({
  selector: 'consent-app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  public static ROUTE = 'register';
  loginForm: FormGroup;
  private username: FormControl | AbstractControl;
  private pwd: FormControl | AbstractControl;
  private pwd2: FormControl | AbstractControl;
  private subRegister: Subscription;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private route: ActivatedRoute ) { }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      id: ['', Validators.required],
      password: ['', Validators.required],
      password2: ['', Validators.required]
    }, { validators: Helperfunctions.compareFields( 'password', 'password2' ) });
    this.username = this.loginForm.get(['id']);
    this.pwd = this.loginForm.get(['password']);
    this.pwd2 = this.loginForm.get(['password2']);
  }
  onSubmit(){
    console.log( this.loginForm.valid );
    console.log( this.loginForm.value );

    const userObj = {
      id: this.loginForm.value.id,
      password: this.loginForm.value.password
    }
    this.subRegister = this.authService.userRegister(userObj).subscribe(
      res => {
      console.log(res);
      if (res.status === 201 ) {
        this.router.navigate( ['login'], { relativeTo: this.route.parent } );
      }
    },
      error => { console.log(error);
    });
  }

}
