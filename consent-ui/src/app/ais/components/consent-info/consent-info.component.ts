import { Component, OnInit } from '@angular/core';
import { AccountAccessLevel, AisConsentToGrant } from '../../common/dto/ais-consent';
import { StubUtil } from '../../../common/utils/stub-util';
import { ActivatedRoute, Router } from '@angular/router';
import { SessionService } from '../../../common/session.service';
import { ConsentUtil } from '../../common/consent-util';

@Component({
  selector: 'consent-app-consent-info',
  templateUrl: './consent-info.component.html',
  styleUrls: ['./consent-info.component.scss']
})
export class ConsentInfoComponent implements OnInit {
  accountAccessLevel = AccountAccessLevel;

  public finTechName = StubUtil.FINTECH_NAME;
  public aspspName = StubUtil.ASPSP_NAME;

  public aisConsent: AisConsentToGrant;

  private authorizationId: string;

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private sessionService: SessionService) {}

  ngOnInit() {
    this.activatedRoute.params.subscribe(res => {
      this.authorizationId = res.authId;
      this.aisConsent = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);
    });

    this.router.events.subscribe(evt => {
      this.aisConsent = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);
    });
  }
}
