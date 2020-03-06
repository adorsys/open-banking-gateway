import {Component, OnInit} from '@angular/core';
import {CustomizeService} from '../common/services/customize.service';

@Component({
  selector: 'consent-app-card-with-sidebar',
  templateUrl: './card-with-sidebar.component.html',
  styleUrls: ['./card-with-sidebar.component.scss']
})
export class CardWithSidebarComponent implements OnInit {

  constructor(public customizeService: CustomizeService) { }

  ngOnInit() {
  }
}
