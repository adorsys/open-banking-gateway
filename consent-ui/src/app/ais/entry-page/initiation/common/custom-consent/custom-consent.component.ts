import {Component, OnInit} from '@angular/core';
import {Location} from "@angular/common";
import {ActivatedRoute, Router} from "@angular/router";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {SessionService} from "../../../../../common/session.service";
import {ConsentUtil} from "../../../../common/consent-util";
import {SharedRoutes} from "../shared-routes";

@Component({
  selector: 'consent-app-custom-consent',
  templateUrl: './custom-consent.component.html',
  styleUrls: ['./custom-consent.component.scss']
})
export class CustomConsentComponent implements OnInit {

  public static ROUTE = 'custom-consent-access';

  public finTechName: string;
  public aspspName: string;

  customConsentAccessForm: FormGroup;
  consentBody = new FormControl('', Validators.required);

  private authorizationId: string;

  constructor(
    private location: Location,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private sessionService: SessionService
  ) {
    this.customConsentAccessForm = this.formBuilder.group({consentBody: this.consentBody});
  }

  ngOnInit(): void {
    this.activatedRoute.parent.parent.params.subscribe((res) => {
      this.authorizationId = res.authId;
      this.aspspName = this.sessionService.getBankName(res.authId);
      this.finTechName = this.sessionService.getFintechName(res.authId);
      const consentData = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);
      this.consentBody.setValue(JSON.stringify(consentData.consent, null, 2));
    });
  }

  onSelect() {
    const consentData = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);
    consentData.consent = JSON.parse(this.consentBody.value);
    this.sessionService.setConsentObject(this.authorizationId, consentData);
    this.router.navigate([SharedRoutes.REVIEW], { relativeTo: this.activatedRoute.parent });
  }

  onBack() {
    ConsentUtil.rollbackConsent(this.authorizationId, this.sessionService);
    this.location.back();
  }
}
