import { Component } from '@angular/core';
import { NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
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
