import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'consent-app-enter-pin',
  templateUrl: './enter-pin.component.html',
  styleUrls: ['./enter-pin.component.scss']
})
export class EnterPinComponent implements OnInit {
  pinForm: FormGroup;
  @Input() wrongPassword;
  @Output() enteredPin = new EventEmitter<string>();

  constructor(private formBuilder: FormBuilder) {}

  ngOnInit() {
    this.pinForm = this.formBuilder.group({
      pin: ['', Validators.required]
    });
  }

  onSubmit() {
    this.enteredPin.emit(this.pinForm.get('pin').value);
  }
}
