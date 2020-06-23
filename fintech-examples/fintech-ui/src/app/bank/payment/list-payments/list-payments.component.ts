import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-list-payments',
  templateUrl: './list-payments.component.html',
  styleUrls: ['./list-payments.component.scss']
})
export class ListPaymentsComponent implements OnInit {
  public static ROUTE = 'accounts';

  constructor() { }

  ngOnInit() {
  }

}
