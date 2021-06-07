import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoARetrievalInformation, LoTRetrievalInformation } from '../../models/consts';
import { StorageService } from '../../services/storage.service';
import {FinTechAccountInformationService} from "../../api";

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
  bankId = 'unknown';
  loaFromTppWithNewConsent = LoARetrievalInformation.FROM_TPP_WITH_NEW_CONSENT;
  loaFromTppWithAvailableConsent = LoARetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT;

  lotFromTppWithNewConsent = LoTRetrievalInformation.FROM_TPP_WITH_NEW_CONSENT;
  lotFromTppWithAvailableConsent = LoTRetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT;

  settingsForm: FormGroup;

  constructor(
    private location: Location,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private storageService: StorageService,
    private accountService: FinTechAccountInformationService
  ) {}

  ngOnInit() {
    this.bankId = this.route.snapshot.paramMap.get('bankid');
    const settingsData = { ...this.storageService.getSettings() };
    this.settingsForm = this.formBuilder.group({
      loa: [settingsData.loa, Validators.required],
      lot: [settingsData.lot, Validators.required],
      withBalance: settingsData.withBalance,
      cacheLoa: settingsData.cacheLoa,
      cacheLot: settingsData.cacheLot,
      consentRequiresAuthentication: settingsData.consentRequiresAuthentication,
      paymentRequiresAuthentication: settingsData.paymentRequiresAuthentication
    });
  }

  onConfirm() {
    this.storageService.setSettings({ ...this.settingsForm.getRawValue() });
    this.onNavigateBack();
  }

  onDelete() {
    this.accountService.aisConsentsDELETE(this.bankId, '', '').subscribe();
  }

  onNavigateBack() {
    this.location.back();
  }
}

export class SettingsData {
  loa: LoARetrievalInformation;
  lot: LoTRetrievalInformation;
  withBalance: boolean;
  cacheLoa: boolean;
  cacheLot: boolean;
  consentRequiresAuthentication: boolean;
  paymentRequiresAuthentication: boolean;
}
