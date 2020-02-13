import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {Consts} from "../consts";

@Component({
  selector: 'app-from-aspsp',
  templateUrl: './from-aspsp.component.html',
  styleUrls: ['./from-aspsp.component.css']
})
export class FromAspspComponent implements OnInit {

  submissionUri = Consts.API_V1_URL_BASE + 'consent/';
  redirectCode: string;

  constructor(private activatedRoute: ActivatedRoute) {  }

  ngOnInit() {
    this.activatedRoute.params.subscribe(
      params => {
        this.submissionUri = this.submissionUri + params['authId'] + '/fromAspsp/' + params['state'] + '/ok';
      }
    );
    this.activatedRoute.queryParams.subscribe(
      params => {
        this.submissionUri = this.submissionUri + '?redirectCode=' + params['redirectCode'];
      }
    );
  }

  submit() {
    window.location.href = this.submissionUri;
  }
}
