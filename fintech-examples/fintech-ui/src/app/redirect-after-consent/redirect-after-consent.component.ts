import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ConsentAuthorizationService } from '../bank/services/consent-authorization.service';
import { Consent } from '../models/consts';
import { ModalCard } from '../models/modalCard.model';
import { SharedModule } from '../common/shared.module';

@Component({
  selector: 'app-redirect-after-consent',
  templateUrl: './redirect-after-consent.component.html',
  styleUrls: ['./redirect-after-consent.component.scss'],
  standalone: true,
  imports: [SharedModule]
})
export class RedirectAfterConsentComponent implements OnInit {
  cardModal: ModalCard = {
    title: 'Consent has been granted',
    description: 'Consent has been granted'
  };

  constructor(private authService: ConsentAuthorizationService, private route: ActivatedRoute) {}

  ngOnInit() {
    const redirectCode = this.route.snapshot.queryParams.redirectCode;
    this.authService.fromConsent(Consent.OK, redirectCode);
  }
}
