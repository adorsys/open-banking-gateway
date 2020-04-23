import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-session-expired',
  templateUrl: './session-expired.component.html',
  styleUrls: ['./session-expired.component.scss']
})
export class SessionExpiredComponent implements OnInit {

  constructor(private router: Router) {

  }

  ngOnInit() {
  }

  public proceed() {
    this.router.navigate(['login']);
  }
}
