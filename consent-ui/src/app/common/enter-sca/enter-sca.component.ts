import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'consent-app-enter-sca',
  templateUrl: './enter-sca.component.html',
  styleUrls: ['./enter-sca.component.scss']
})
export class EnterScaComponent implements OnInit {
  reportScaResultForm: FormGroup;
  @Input() wrongSca: boolean;
  @Output() enteredSca = new EventEmitter<string>();

  constructor(private formBuilder: FormBuilder) {}

  ngOnInit() {
    this.reportScaResultForm = this.formBuilder.group({
      sca: ['', Validators.required]
    });
  }

  onSubmit() {
    this.enteredSca.emit(this.reportScaResultForm.get('sca').value);
  }
}
