import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoARetrievalInformation, LoTRetrievalInformation } from '../../models/consts';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {

  bankId = 'unknown';
  loaFromFintechCache = LoARetrievalInformation.FROM_FINTECH_CACHE;
  loaFromTppWithNewConsent = LoARetrievalInformation.FROM_TPP_WITH_NEW_CONSENT;
  loaFromTppWithAvailableConsent = LoARetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT;
  loa = LoARetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT

  lotFromTppWithNewConsent = LoTRetrievalInformation.FROM_TPP_WITH_NEW_CONSENT;
  lotFromTppWithAvailableConsent = LoTRetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT;
  lot = LoTRetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT;

  settingsForm: FormGroup;

  constructor(private route: ActivatedRoute, fb: FormBuilder) {
  this.bankId = this.route.snapshot.paramMap.get('bankid');
    this.settingsForm = fb.group({
      loa: [this.loa, Validators.required],
      lot: [this.lot, Validators.required]
    });
  }

  ngOnInit() {

  }

  onDeny() {

  }

  onConfirm() {

  }

}
