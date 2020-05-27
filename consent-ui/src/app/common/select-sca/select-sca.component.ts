import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ScaUserData } from '../../api';

@Component({
  selector: 'consent-app-select-sca',
  templateUrl: './select-sca.component.html',
  styleUrls: ['./select-sca.component.scss']
})
export class SelectScaComponent implements OnInit {
  @Input() scaMethods: ScaUserData[] = [];
  @Input() selectedMethod = new FormControl();
  @Output() selectedValue = new EventEmitter<string>();
  scaMethodForm: FormGroup;

  constructor(private formBuilder: FormBuilder) {}

  ngOnInit() {
    this.scaMethodForm = this.formBuilder.group({
      selectedMethodValue: [this.selectedMethod, Validators.required]
    });
  }

  onSubmit(): void {
    this.selectedValue.emit(this.selectedMethod.value);
  }
}
