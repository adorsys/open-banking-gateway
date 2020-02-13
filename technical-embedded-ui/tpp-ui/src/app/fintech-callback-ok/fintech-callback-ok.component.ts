import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-fintech-callback-ok',
  templateUrl: './fintech-callback-ok.component.html',
  styleUrls: ['./fintech-callback-ok.component.css']
})
export class FintechCallbackOkComponent implements OnInit {

  serviceSessionId: string;

  constructor(private activatedRoute: ActivatedRoute) {  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(
      params => {
        this.serviceSessionId = params['serviceSessionId'];
      });
  }

  submit() {
    window.location.href = '/initial?serviceSessionId=' + this.serviceSessionId;
  }
}
