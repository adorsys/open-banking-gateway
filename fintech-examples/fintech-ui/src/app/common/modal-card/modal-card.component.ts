import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ModalCard } from '../../models/modalCard.model';

@Component({
  selector: 'app-modal-card',
  templateUrl: './modal-card.component.html',
  styleUrls: ['./modal-card.component.scss']
})
export class ModalCardComponent implements OnInit {
  @Input() cardModal: ModalCard;
  @Output() eventEmitter: EventEmitter<boolean> = new EventEmitter();

  constructor() {}

  ngOnInit() {}

  onSubmit(value: boolean) {
    this.eventEmitter.emit(value);
  }
}
