import { Component, OnInit } from '@angular/core';
import { ScaUserData } from '../../api';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'consent-app-select-sca-page',
  templateUrl: './select-sca-page.component.html',
  styleUrls: ['./select-sca-page.component.scss']
})
export class SelectScaPageComponent implements OnInit {
  public static ROUTE = 'select-sca';

  scaMethods: ScaUserData[] = [];
  selectedMethod = new FormControl();

  constructor() {}

  ngOnInit() {}

  onSubmit(selectedMethodValue: string): void {}
}
