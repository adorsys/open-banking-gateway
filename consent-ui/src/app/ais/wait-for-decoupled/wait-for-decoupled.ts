import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'wait-for-decoupled-redirection',
  templateUrl: './wait-for-decoupled.html',
  styleUrls: ['./wait-for-decoupled.scss']
})
export class WaitForDecoupled implements OnInit {
  public static ROUTE = 'wait-sca-finalization';

  ngOnInit() {}
}
