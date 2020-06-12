import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-result',
  templateUrl: './result.component.html',
  styleUrls: ['./result.component.scss']
})
export class ResultComponent implements OnInit {
  public static ROUTE = 'result';

  constructor() {}

  ngOnInit() {}

  onConfirm(): void {}
}
