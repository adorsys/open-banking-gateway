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
    this.activatedRoute.queryParams.subscribe(
      params => {
        this.submissionUri = params.dest;
        console.log(params.dest)
        if (params.dest.includes('/fintech-callback/ok')) {
          this.submissionUri += '?serviceSessionId=' + params.serviceSessionId;
        }
      }
    );
  }

  submit() {
    window.location.href = this.submissionUri;
  }
}
