import { Component } from '@angular/core';
import { NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './common/navbar/navbar.component';
import { NgHttpLoaderComponent } from 'ng-http-loader';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, NgHttpLoaderComponent]
})
export class AppComponent {
  title = 'fintech-ui';

  // the recommended way to set global config for modals
  constructor(ngbModalConfig: NgbModalConfig) {
    ngbModalConfig.backdrop = 'static';
    ngbModalConfig.centered = true;
    ngbModalConfig.keyboard = false;
  }
}
