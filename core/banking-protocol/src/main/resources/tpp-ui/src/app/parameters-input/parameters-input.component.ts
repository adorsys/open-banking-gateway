import {Component, Input, OnInit} from '@angular/core';
import {DynamicFormControlBase} from "../dynamic-form/dynamic.form.control.base";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-parameters-input',
  templateUrl: './parameters-input.component.html',
  styleUrls: ['./parameters-input.component.css']
})
export class ParametersInputComponent implements OnInit {

  @Input() inputs: DynamicFormControlBase<any>[] = [];

  constructor(private activatedRoute: ActivatedRoute, private router:Router) {  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(
      params => {
        this.inputs = JSON.parse(params['q']).map(it => new DynamicFormControlBase(it.code, it.message))
      }
    );
  }
}
