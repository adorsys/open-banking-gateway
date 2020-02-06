import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { BankProfile } from '../../../api';
import { BankProfileService } from '../../services/bank-profile.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  profile: BankProfile;
  @Input() bankId: string;
  @Output() bankUnselect = new EventEmitter<boolean>();

  constructor(private bankProfileService: BankProfileService) {}

  ngOnInit() {
    this.bankProfileService.getBankProfile(this.bankId).subscribe((profile: BankProfile) => {
      console.log(profile);
      this.profile = profile;
    });
  }

  goBack() {
    this.bankUnselect.emit(true);
  }
}
