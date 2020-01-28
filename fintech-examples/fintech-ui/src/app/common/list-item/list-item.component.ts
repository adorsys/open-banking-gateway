import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-list-item',
  templateUrl: './list-item.component.html',
  styleUrls: ['./list-item.component.scss']
})
export class ListItemComponent implements OnInit {
  @Input() bankSearch: boolean;
  @Input() card: any;
  @Input() config: any;

  constructor() {}

  ngOnInit() {}
}
