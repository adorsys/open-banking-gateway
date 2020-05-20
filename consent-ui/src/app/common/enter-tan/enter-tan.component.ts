import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'consent-app-enter-tan',
  templateUrl: './enter-tan.component.html',
  styleUrls: ['./enter-tan.component.scss']
})
export class EnterTanComponent implements OnInit {
  reportScaResultForm: FormGroup;
  @Input() wrongSca: boolean;
  @Output() enteredSca = new EventEmitter<string>();

  constructor(private formBuilder: FormBuilder) {}

  ngOnInit() {
    this.reportScaResultForm = this.formBuilder.group({
      tan: ['', Validators.required]
    });
  }

  onSubmit() {
    this.enteredSca.emit(this.reportScaResultForm.get('tan').value);
  }
}
