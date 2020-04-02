import { Component, OnInit } from '@angular/core';
import {DynamicFormControlBase} from "../dynamic-form/dynamic-form-control-base";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-embedded-start',
  templateUrl: './embedded-start.component.html',
  styleUrls: ['./embedded-start.component.css']
})
export class EmbeddedStartComponent implements OnInit {

  submissionUri: string;

  constructor(private activatedRoute: ActivatedRoute) {  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(
      params => {
        this.submissionUri = this.submissionUri + params['to'];
      }
    );
  }

}
