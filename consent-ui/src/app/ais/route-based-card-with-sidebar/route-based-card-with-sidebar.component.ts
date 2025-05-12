import { Component, OnInit } from '@angular/core';
import { CustomizeService } from '../../services/customize.service';

@Component({
    selector: 'consent-app-route-based-card-with-sidebar',
    templateUrl: './route-based-card-with-sidebar.component.html',
    styleUrls: ['./route-based-card-with-sidebar.component.scss'],
    standalone: false
})
export class RouteBasedCardWithSidebarComponent implements OnInit {
  constructor(public customizeService: CustomizeService) {}

  ngOnInit() {}
}
