import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { ConsentSettingType, LoARetrievalInformation, LoTRetrievalInformation } from '../../models/consts';
import { StorageService } from '../../services/storage.service';

import AllPsd2Enum = AisAccountAccessInfo.AllPsd2Enum;
import { FinTechAccountInformationService } from '../../api';
import { AisConsentRequest } from '../../api/model/aisConsentRequest';
import { AisAccountAccessInfo } from '../../api/model/aisAccountAccessInfo';
import { SharedModule } from '../../common/shared.module';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss'],
  standalone: true,
  imports: [SharedModule]
})
export class SettingsComponent implements OnInit {
  bankId = 'unknown';
  loaFromTppWithNewConsent = LoARetrievalInformation.FROM_TPP_WITH_NEW_CONSENT;
  loaFromTppWithAvailableConsent = LoARetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT;

  lotFromTppWithNewConsent = LoTRetrievalInformation.FROM_TPP_WITH_NEW_CONSENT;
  lotFromTppWithAvailableConsent = LoTRetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT;

  consentTypeNone = ConsentSettingType.NONE;
  consentTypeDefault = ConsentSettingType.DEFAULT;
  consentTypeCustom = ConsentSettingType.CUSTOM;

  settingsForm: UntypedFormGroup;

  constructor(
    private location: Location,
    private route: ActivatedRoute,
    private formBuilder: UntypedFormBuilder,
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
      paymentRequiresAuthentication: settingsData.paymentRequiresAuthentication,
      consentSettingType: [settingsData.consentSettingType, Validators.required],
      consent: JSON.stringify(settingsData.consent),
      frequencyPerDay: settingsData.consent === null ? null : settingsData.consent.frequencyPerDay,
      recurringIndicator: settingsData.consent === null ? null : settingsData.consent.recurringIndicator,
      validUntil: settingsData.consent === null ? null : settingsData.consent.validUntil,
      combinedServiceIndicator: settingsData.consent === null ? null : settingsData.consent.combinedServiceIndicator,
      dateFrom: settingsData.dateFrom,
      dateTo: settingsData.dateTo
    });
  }

  onConfirm() {
    const data = { ...this.settingsForm.getRawValue() };
    this.storageService.setSettings({
      loa: data.loa,
      lot: data.lot,
      withBalance: data.withBalance,
      cacheLoa: data.cacheLoa,
      cacheLot: data.cacheLot,
      consentRequiresAuthentication: data.consentRequiresAuthentication,
      paymentRequiresAuthentication: data.paymentRequiresAuthentication,
      consentSettingType: data.consentSettingType,
      consent: JSON.parse(data.consent),
      ...(data.consentSettingType === this.consentTypeDefault && {
        consent: {
          access: { allPsd2: AllPsd2Enum.ACCOUNTSWITHBALANCES },
          combinedServiceIndicator: data.combinedServiceIndicator,
          frequencyPerDay: data.frequencyPerDay,
          recurringIndicator: data.recurringIndicator,
          validUntil: data.validUntil
        }
      }),
      dateFrom: data.dateFrom,
      dateTo: data.dateTo
    });
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
  consentSettingType: ConsentSettingType;
  consent: AisConsentRequest;
  dateFrom: string;
  dateTo: string;
}
