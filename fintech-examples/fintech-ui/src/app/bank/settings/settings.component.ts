import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoARetrievalInformation, LoTRetrievalInformation } from '../../models/consts';
import { SettingsService } from '../services/settings.service';
import { tap } from 'rxjs/operators';

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
  loa;

  lotFromTppWithNewConsent = LoTRetrievalInformation.FROM_TPP_WITH_NEW_CONSENT;
  lotFromTppWithAvailableConsent = LoTRetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT;
  lot;

  settingsForm: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private settingsService: SettingsService,
    private router: Router,
    private formBuilder: FormBuilder)
  {
    this.bankId = this.route.snapshot.paramMap.get('bankid');
    this.settingsService.getLoA().pipe(tap(el => this.loa = el)).subscribe();
    this.settingsService.getLoT().pipe(tap(el => this.lot = el)).subscribe();
    this.settingsForm = formBuilder.group({
      loa: [this.loa, Validators.required],
      lot: [this.lot, Validators.required]
    });
  }

  ngOnInit() {

  }

  onDeny() {

  }

  onConfirm() {
    this.loa = this.settingsForm.getRawValue().loa;
    this.lot = this.settingsForm.getRawValue().lot;
    this.settingsService.setLoA(this.loa);
    this.settingsService.setLoT(this.lot);
    this.router.navigate(['..'], { relativeTo: this.route });
  }

}
