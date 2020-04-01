import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Consts } from '../../models/consts';
import { RedirectStruct } from './redirect-struct';
import {StorageService} from "../../services/storage.service";

@Component({
  selector: 'app-redirect-page',
  templateUrl: './redirect-page.component.html',
  styleUrls: ['./redirect-page.component.scss']
})
export class RedirectPageComponent implements OnInit {
  private bankId;
  public bankName;
  private location;
  private cancelPath;

  constructor(private router: Router, private route: ActivatedRoute, private storageService: StorageService) {}

  ngOnInit() {
    this.route.paramMap.subscribe(p => {
      const r: RedirectStruct = JSON.parse(p.get('location'));
      this.location = decodeURIComponent(r.okUrl);
      this.cancelPath = decodeURIComponent(r.cancelUrl);
      console.log('LOCATION IS ', this.location);
    });
    this.bankName = this.storageService.getBankName();
    //  this.bankId = this.route.parent.parent.parent.snapshot.paramMap.get('bankid');
    //  console.log('redirect page for bankid', this.bankId);
  }

  cancel(): void {
    this.router.navigate([this.cancelPath], { relativeTo: this.route });
  }

  proceed(): void {
    console.log('NOW GO TO:', this.location);
    window.location.href = this.location;
  }
}
