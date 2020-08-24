import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
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
  loa;

  lotFromTppWithNewConsent = LoTRetrievalInformation.FROM_TPP_WITH_NEW_CONSENT;
  lotFromTppWithAvailableConsent = LoTRetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT;
  lot;

  paymentRequiresAuthentication = new FormControl(false)
  settingsForm: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder,
    private storageService : StorageService)
  {
    this.bankId = this.route.snapshot.paramMap.get('bankid');

    const settings = this.storageService.getSettings();
    this.loa = settings.loa;
    this.lot = settings.lot;
    this.paymentRequiresAuthentication.setValue(settings.paymentRequiresAuthentication);

    this.settingsForm = this.formBuilder.group({
      loa: [this.loa, Validators.required],
      lot: [this.lot, Validators.required],
      paymentRequiresAuthentication: this.paymentRequiresAuthentication
    });
  }

  ngOnInit() {
    console.log('settings ng on init');
    const settings = this.storageService.getSettings();
    this.loa = settings.loa;
    this.lot = settings.lot;
    this.paymentRequiresAuthentication.setValue(settings.paymentRequiresAuthentication);

    console.log('this.loa = ', this.loa.toString());
  }

  onDeny() {

  }

  onConfirm() {
    this.loa = this.settingsForm.getRawValue().loa;
    this.lot = this.settingsForm.getRawValue().lot;

    this.storageService.setSettings(new SettingsData(this.loa, this.lot, this.settingsForm.getRawValue().paymentRequiresAuthentication));
    this.router.navigate(['..'], { relativeTo: this.route });
  }

}

export class SettingsData {

  constructor(
    public loa: LoARetrievalInformation,
    public lot: LoTRetrievalInformation,
    public paymentRequiresAuthentication: boolean) {
  }
}
