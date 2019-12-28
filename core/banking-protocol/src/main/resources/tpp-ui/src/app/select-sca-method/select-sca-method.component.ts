import { Component, OnInit } from '@angular/core';
import {Consts} from "../consts";
import {FormGroup} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-select-sca-method',
  templateUrl: './select-sca-method.component.html',
  styleUrls: ['./select-sca-method.component.css']
})
export class SelectScaMethodComponent implements OnInit {

  submissionUri: string = Consts.API_V1_URL_BASE + "parameters/select-sca-method/";
  methods: ScaMethod[];
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

class ScaMethod {

  key: string;
  value: string;
}
