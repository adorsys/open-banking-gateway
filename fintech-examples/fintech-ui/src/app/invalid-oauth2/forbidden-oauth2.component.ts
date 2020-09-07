import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RoutingPath } from '../models/routing-path.model';

@Component({
  selector: 'app-forbidden-oauth2',
  templateUrl: './forbidden-oauth2.component.html',
  styleUrls: ['./forbidden-oauth2.component.scss']
})
export class ForbiddenOauth2Component implements OnInit {
  constructor(private router: Router) {}

  ngOnInit() {}

  public proceed() {
    this.router.navigate([RoutingPath.LOGIN]);
  }
}
