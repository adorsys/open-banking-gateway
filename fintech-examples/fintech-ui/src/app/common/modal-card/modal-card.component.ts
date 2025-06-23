import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ModalCard } from '../../models/modalCard.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-modal-card',
  templateUrl: './modal-card.component.html',
  styleUrls: ['./modal-card.component.scss'],
  standalone: true,
  imports: [CommonModule]
})
export class ModalCardComponent {
  @Input() cardModal: ModalCard;
  @Output() eventEmitter = new EventEmitter<boolean>();

  onSubmit(value: boolean) {
    this.eventEmitter.emit(value);
  }
}
