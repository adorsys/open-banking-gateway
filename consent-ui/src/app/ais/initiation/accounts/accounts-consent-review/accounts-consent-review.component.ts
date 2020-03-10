import {Component, OnInit} from '@angular/core';
import {ConsentUtil} from "../../../common/consent-util";
import {ActivatedRoute, Router} from "@angular/router";
import {FormBuilder} from "@angular/forms";
import {SessionService} from "../../../../common/session.service";
import {AisConsent} from "../../../common/dto/ais-consent";
import {StubUtil} from "../../../common/stub-util";

@Component({
  selector: 'consent-app-accounts-consent-review',
  templateUrl: './accounts-consent-review.component.html',
  styleUrls: ['./accounts-consent-review.component.scss']
})
export class AccountsConsentReviewComponent implements OnInit {

  public finTechName = StubUtil.FINTECH_NAME;
  public aspspName = StubUtil.ASPSP_NAME;

  public static ROUTE = 'review-consent-accounts';

  private authorizationId: string;
  private aisConsent: AisConsent;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private sessionService: SessionService
  ) { }

  ngOnInit() {
    this.activatedRoute.parent.params.subscribe(res => {
      this.authorizationId = res.authId;
      this.aisConsent = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);
    });
  }

  onConfirm() {
  }
}
