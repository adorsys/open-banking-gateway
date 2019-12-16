import { Component, EventEmitter, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {
  @Output() keyword = new EventEmitter();
  constructor() {}

  ngOnInit() {}

  onSearch(keyword: string) {
    this.keyword.emit(keyword);
  }
}
