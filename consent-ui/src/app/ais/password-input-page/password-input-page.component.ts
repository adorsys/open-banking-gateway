import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'consent-app-password-input-page',
  templateUrl: './password-input-page.component.html',
  styleUrls: ['./password-input-page.component.scss']
})
export class PasswordInputPageComponent implements OnInit {
  passwordForm: FormGroup;

  constructor(private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.passwordForm = this.formBuilder.group({
      pin: ['', Validators.required]
    });
  }

}
