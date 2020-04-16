import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from "@angular/forms";
import { HttpClient } from "@angular/common/http";
import { ActivatedRoute } from "@angular/router";
import { Consts } from "../consts";
import { Users } from '../parameters-input/parameters-input.component';
import { Helpers } from "../app.component";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  Users = Users;

  submissionUri: string = Consts.API_V1_URL_BASE + '/psu/ais/';

  private authorizationSessionId: string;
  private redirectCode: string;

  login = new FormControl();
  password = new FormControl();
  form: FormGroup = new FormGroup({login: this.login, password: this.password});

  constructor(private client: HttpClient, private activatedRoute: ActivatedRoute) {
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe(
      params => {
        this.authorizationSessionId = params.executionId;
      }
    );

    this.activatedRoute.queryParams.subscribe(
      params => {
        this.redirectCode = params.redirectCode[0];
      }
    );

    this.setDataFor(Users.ANTON);
  }

  setDataFor(user: Users) {
    if (user === Users.ANTON) {
      this.login.setValue('Anton_Brueckner');
    } else if (user === Users.MAX) {
      this.login.setValue('Max_Musterman');
    } else {
      throw new Error();
    }

    this.password.setValue('1234');
  }

  doLogin() {
    this.client.post(
      this.submissionUri + this.authorizationSessionId + '/for-approval/login' + '?redirectCode=' + this.redirectCode,
      {login: this.login.value, password: this.password.value},
      {headers: {
          'X-Request-ID': Helpers.uuidv4()
        }, observe: 'response'}
    ).subscribe(res => {
      window.location.href = res.headers.get('Location');
    });
  }
}
