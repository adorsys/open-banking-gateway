import {Component, OnInit} from '@angular/core';
import {CustomizeService} from '../common/services/customize.service';

@Component({
  selector: 'consent-app-route-based-card-with-sidebar',
  templateUrl: './route-based-card-with-sidebar.component.html',
  styleUrls: ['./route-based-card-with-sidebar.component.scss']
})
export class RouteBasedCardWithSidebarComponent implements OnInit {

  constructor(public customizeService: CustomizeService) { }

  ngOnInit() {
  }
}
