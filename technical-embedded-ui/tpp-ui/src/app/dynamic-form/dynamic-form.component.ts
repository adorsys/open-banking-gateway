import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { DynamicFormControlBase } from './dynamic-form-control-base';
import {DynamicFormFactory} from './dynamic-form-factory';
import {HttpClient} from '@angular/common/http';
import {Helpers} from '../app.component';
import {AisConsentBody} from "../app-ais-consent/app-ais-consent.component";

@Component({
  selector: 'app-dynamic-form',
  templateUrl: './dynamic-form.component.html'
})
export class DynamicFormComponent implements OnInit {

  @Input() aisControlTemplates: DynamicFormControlBase<any>[] = [];
  @Input() dynamicControlTemplates: DynamicFormControlBase<any>[] = [];
  @Input() submissionUri: string;

  aisConsent: AisConsentBody;
  formStatic: FormGroup;
  formDynamic: FormGroup;

  constructor(private client: HttpClient, private formFactory: DynamicFormFactory) {  }

  ngOnInit() {
    this.formDynamic = this.formFactory.toFormGroup(this.dynamicControlTemplates);
    if (this.aisControlTemplates.length > 0) {
      this.aisConsent = new AisConsentBody();
      this.formStatic = new FormGroup({});
    }
  }

  save() {
    const dynamicForm = this.formDynamic.getRawValue();
    this.cleanup(dynamicForm);

    const body = {
      extras: dynamicForm
    };

    if (this.aisConsent) {
      body['consentAuth'] = {consent: this.aisConsent};
    }

    this.client.post(
      this.submissionUri,
      body,
      {headers: {
        'X-Request-ID': Helpers.uuidv4(),
        'X-XSRF-TOKEN': Helpers.uuidv4(),
      }}
    ).subscribe(res => {
    }, error => {
      if (error.url.includes('redirToSandbox=')) {
        window.location.href = error.url.substr(error.url.indexOf('redirToSandbox=') + 'redirToSandbox='.length);
      } else {
        window.location.href = error.url;
      }
    });
  }

  private cleanup(form) {
    for (const propName in form) {
      if (form[propName] === null || form[propName] === undefined || form[propName].length === 0) {
        delete form[propName];
      }
    }
  }
}


