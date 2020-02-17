import {Component, Input, OnInit} from '@angular/core';
import {DynamicFormControlBase} from "../dynamic-form/dynamic-form-control-base";
import {ActivatedRoute, Router} from "@angular/router";
import {Consts} from "../consts";

@Component({
  selector: 'app-parameters-input',
  templateUrl: './parameters-input.component.html',
  styleUrls: ['./parameters-input.component.css']
})
export class ParametersInputComponent implements OnInit {

  @Input() inputs: DynamicFormControlBase<any>[] = [];
  submissionUri: string = Consts.API_V1_URL_BASE + 'consent/';

  constructor(private activatedRoute: ActivatedRoute) {  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(
      params => {
        this.submissionUri = this.submissionUri + params['authorizationSessionId'] + '/embedded?redirectCode=' + params['redirectCode'];
        this.inputs = JSON.parse(params['q']).map(it => new DynamicFormControlBase(it.ctxCode, it.uiCode, it.message));
      }
    );
  }
}
