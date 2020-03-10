import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {FormBuilder, FormGroup} from "@angular/forms";
import {SessionService} from "../../../../common/session.service";
import {Account} from "../account-selector/account-selector.component";

@Component({
  selector: 'consent-app-limited-access',
  templateUrl: './limited-access.component.html',
  styleUrls: ['./limited-access.component.scss']
})
export class LimitedAccessComponent implements OnInit {

  public static ROUTE = 'limited-access';

  accounts = [new Account()];
  limitedAccountAccessForm: FormGroup;

  private authorizationId: string;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private sessionService: SessionService
  ) {
    this.limitedAccountAccessForm = this.formBuilder.group({});
  }

  ngOnInit() {
    this.activatedRoute.parent.params.subscribe(res => {
      this.authorizationId = res.authId;
    })
  }

  onSelect() {
  }
}
