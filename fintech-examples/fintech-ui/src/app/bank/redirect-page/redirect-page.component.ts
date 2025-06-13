import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RedirectStruct } from './redirect-struct';
import { Consent, HeaderConfig } from '../../models/consts';
import { ConsentAuthorizationService } from '../services/consent-authorization.service';
import { StorageService } from '../../services/storage.service';
import { ModalCard } from '../../models/modalCard.model';
import { SharedModule } from '../../common/shared.module';

@Component({
  selector: 'app-redirect-page',
  templateUrl: './redirect-page.component.html',
  styleUrls: ['./redirect-page.component.scss'],
  standalone: true,
  imports: [SharedModule]
})
export class RedirectPageComponent implements OnInit {
  public redirectStruct: RedirectStruct = new RedirectStruct();
  cardModal = new ModalCard();

  constructor(
    private authService: ConsentAuthorizationService,
    private storageService: StorageService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe((p) => {
      this.redirectStruct = JSON.parse(p.get(HeaderConfig.HEADER_FIELD_LOCATION));
      this.cardModal = {
        title: 'Redirection',
        description: 'Now we redirect you to your bank: ' + this.redirectStruct.bankName,
        imageUrl: 'assets/icons/icons8-network 2.png',
        confirmBtn: true,
        cancelBtn: true
      };
    });
  }

  onSubmit(value: boolean) {
    if (value) {
      this.storageService.setUserRedirected(true);
      window.location.href = decodeURIComponent(this.redirectStruct.redirectUrl);
    } else {
      this.storageService.setUserRedirected(false);
      this.authService.fromConsent(Consent.NOT_OK, this.redirectStruct.redirectCode);
    }
  }
}
