import { Component, OnInit } from '@angular/core';
import { CustomizeService } from '../../services/customize.service';

@Component({
  selector: 'consent-app-entry-page',
  templateUrl: './entry-page.component.html',
  styleUrls: ['./entry-page.component.scss']
})
export class EntryPageComponent implements OnInit {
  constructor(public customizeService: CustomizeService) {}

  ngOnInit() {}
}
