import {Component, Input, OnInit} from '@angular/core';
import {FormControl} from "@angular/forms";

@Component({
  selector: 'app-account-reference',
  templateUrl: './account-reference.component.html',
  styleUrls: ['./account-reference.component.css']
})
export class AccountReferenceComponent implements OnInit {

  iban: FormControl = new FormControl();
  currency: FormControl = new FormControl();

  @Input() elemId: number;

  constructor() { }

  static buildWithId(elemId: number) : AccountReferenceComponent {
    let result = new AccountReferenceComponent();
    result.elemId = elemId;
    return result;
  }

  ngOnInit() {
  }
}
