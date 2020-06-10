import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-confirm',
  templateUrl: './confirm.component.html',
  styleUrls: ['./confirm.component.scss']
})
export class ConfirmComponent implements OnInit {
  public static ROUTE = 'confirm';
  constructor() {}

  ngOnInit() {}

  onDeny() {}

  onConfirm() {}
}
