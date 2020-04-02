import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-fintech-callback-ok',
  templateUrl: './fintech-callback-ok.component.html',
  styleUrls: ['./fintech-callback-ok.component.css']
})
export class FintechCallbackOkComponent implements OnInit {

  serviceSessionId: string;
  redirectCode: string;

  constructor(private activatedRoute: ActivatedRoute) {  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(
      params => {
        this.redirectCode = params['redirectCode'];
        this.serviceSessionId = params['serviceSessionId'];
        if (!this.serviceSessionId) {
          this.serviceSessionId = localStorage.getItem(this.redirectCode);
        }
      });
  }

  submit() {
    window.location.href = '/initial?serviceSessionId=' + this.serviceSessionId;
  }
}
