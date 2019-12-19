import { Component, EventEmitter, Output, OnDestroy } from '@angular/core';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnDestroy {
  private subscriptions: Subscription;

  @Output() keyword = new EventEmitter();
  searchTerm$ = new Subject<string>();

  constructor() {
    this.subscriptions = this.searchTerm$.pipe(debounceTime(200), distinctUntilChanged()).subscribe(inputData => {
      this.keyword.emit(inputData);
    });
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
