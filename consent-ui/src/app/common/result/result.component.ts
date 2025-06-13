import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'consent-app-result',
  templateUrl: './result.component.html',
  styleUrls: ['./result.component.scss'],
  standalone: false
})
export class ResultComponent implements OnInit {
  @Input() redirectTo: string;
  @Input() title: string;
  @Input() subtitle: string;
  @Input() showDeleteButton: boolean;
  @Input() finTechName: string;
  @Output() confirm = new EventEmitter<boolean>();

  constructor() {}

  ngOnInit() {}

  onDeny() {
    this.confirm.emit(false);
  }

  onConfirm() {
    this.confirm.emit(true);
  }
}
