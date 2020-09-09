import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoARetrievalInformation, LoTRetrievalInformation } from '../../models/consts';
import { StorageService } from '../../services/storage.service';

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
    private storageService: StorageService
  ) {}

  ngOnInit() {
    this.bankId = this.route.snapshot.paramMap.get('bankid');
    const settingsData = { ...this.storageService.getSettings() };
    this.settingsForm = this.formBuilder.group({
      loa: [settingsData.loa, Validators.required],
      lot: [settingsData.lot, Validators.required],
      withBalance: settingsData.withBalance,
      paymentRequiresAuthentication: settingsData.paymentRequiresAuthentication
    });
  }

  onConfirm() {
    this.storageService.setSettings({ ...this.settingsForm.getRawValue() });
    this.onNavigateBack();
  }

  onNavigateBack() {
    this.location.back();
  }
}

export class SettingsData {
  loa: LoARetrievalInformation;
  lot: LoTRetrievalInformation;
  withBalance: boolean;
  paymentRequiresAuthentication: boolean;
}
