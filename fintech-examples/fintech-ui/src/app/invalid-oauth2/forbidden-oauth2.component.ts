import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { RoutingPath } from '../models/routing-path.model';

@Component({
  selector: 'app-forbidden-oauth2',
  templateUrl: './forbidden-oauth2.component.html',
  styleUrls: ['./forbidden-oauth2.component.scss'],
  standalone: true,
  imports: []
})
export class ForbiddenOauth2Component {
  constructor(private router: Router) {}

  public proceed() {
    this.router.navigate([RoutingPath.LOGIN]);
  }
}
