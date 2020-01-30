import { Component, OnInit } from '@angular/core';
import {Consts} from "../consts";
import {FormGroup} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-provide-psu-password',
  templateUrl: './provide-psu-password.component.html',
  styleUrls: ['./provide-psu-password.component.css']
})
export class ProvidePsuPasswordComponent implements OnInit {

  submissionUri: string = Consts.API_V1_URL_BASE + "parameters/provide-psu-password/";
  form: FormGroup;

  constructor(private activatedRoute: ActivatedRoute) { }

  ngOnInit() {
    this.activatedRoute.params.subscribe(
      params => {
        this.submissionUri = this.submissionUri + params['executionId']
      }
    );
  }
}
