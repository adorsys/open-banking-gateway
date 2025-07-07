import { Component, OnInit } from '@angular/core';
import { AccountAccessLevel, AisConsentToGrant } from '../../common/dto/ais-consent';
import { ActivatedRoute, Router } from '@angular/router';
import { SessionService } from '../../../common/session.service';
import { ConsentUtil } from '../../common/consent-util';

@Component({
  selector: 'consent-app-consent-info',
  templateUrl: './consent-info.component.html',
  styleUrls: ['./consent-info.component.scss'],
  standalone: false
})
export class ConsentInfoComponent implements OnInit {
  accountAccessLevel = AccountAccessLevel;

  public finTechName: string;
  public aspspName: string;

  public aisConsent: AisConsentToGrant;

  private authorizationId: string;

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private sessionService: SessionService) {}

  ngOnInit() {
    this.activatedRoute.params.subscribe((res) => {
      this.authorizationId = res.authId;
      this.aspspName = this.sessionService.getBankName(res.authId);
      this.finTechName = this.sessionService.getFintechName(res.authId);
      this.aisConsent = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);
    });

    this.router.events.subscribe(() => {
      this.aisConsent = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);
    });
  }
}
