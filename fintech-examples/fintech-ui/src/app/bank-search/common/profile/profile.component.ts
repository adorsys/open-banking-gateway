import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { BankProfileService } from '../../services/bank-profile.service';
import { BankProfile } from '../../models/bank-profile.model';

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
