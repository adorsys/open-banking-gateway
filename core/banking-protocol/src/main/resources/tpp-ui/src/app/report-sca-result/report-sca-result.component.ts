import { Component, OnInit } from '@angular/core';
import {Consts} from "../consts";
import {FormGroup} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {DynamicFormControlBase} from "../dynamic-form/dynamic-form-control-base";

@Component({
  selector: 'app-report-sca-result',
  templateUrl: './report-sca-result.component.html',
  styleUrls: ['./report-sca-result.component.css']
})
export class ReportScaResultComponent implements OnInit {

  submissionUri: string = Consts.API_V1_URL_BASE + "parameters/report-sca-result/";
  form: FormGroup;
  label: string = "";

  constructor(private activatedRoute: ActivatedRoute) { }

  ngOnInit() {
    this.activatedRoute.params.subscribe(
      params => {
        this.submissionUri = this.submissionUri + params['executionId']
      }
    );
    this.activatedRoute.queryParams.subscribe(
      params => {
        this.label = params['q'];
      }
    );
  }
}
