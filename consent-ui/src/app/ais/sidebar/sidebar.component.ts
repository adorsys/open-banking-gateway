import { Component, OnInit } from '@angular/core';
import { CustomizeService } from '../../services/customize.service';

@Component({
  selector: 'consent-app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
  standalone: false
})
export class SidebarComponent implements OnInit {
  constructor(public customizeService: CustomizeService) {}

  ngOnInit() {}
}
