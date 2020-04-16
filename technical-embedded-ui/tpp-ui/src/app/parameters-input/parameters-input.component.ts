import { Component, Input, OnInit } from '@angular/core';
import { DynamicFormControlBase, Target } from "../dynamic-form/dynamic-form-control-base";
import { ActivatedRoute } from "@angular/router";
import { Consts } from "../consts";
import { Globals, UserInfo } from "../globals";

@Component({
  selector: 'app-parameters-input',
  templateUrl: './parameters-input.component.html',
  styleUrls: ['./parameters-input.component.css']
})
export class ParametersInputComponent implements OnInit {

  Users = Users;
  AccountRefType = AccountRefType;

  @Input() inputsAisConsent: DynamicFormControlBase<any>[] = [];
  @Input() inputsDynamic: DynamicFormControlBase<any>[] = [];
  submissionUri: string = Consts.API_V1_URL_BASE + 'consent/';

  constructor(private activatedRoute: ActivatedRoute, private globals: Globals) {  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(
      params => {
        this.submissionUri = this.submissionUri + params.authorizationSessionId[0] + '/embedded?redirectCode=' + params.redirectCode[0];
        const data: DynamicFormControlBase<any>[] = JSON.parse(params['q'])
          .map(it => new DynamicFormControlBase(it.code, it.code, it.type, it.scope, it.captionMessage, it.scope as Target));
        this.inputsAisConsent = data.filter(it => it.target === Target.AIS_CONSENT);
        this.inputsDynamic = data.filter(it => it.target === Target.GENERAL);
      }
    );
  }

  setDataFor(user: Users, acc: AccountRefType) {
    this.globals.userInfoPublished.next(new UserInfo('PSU_IP_ADDRESS', '1.1.1.1'));
    this.globals.userInfoPublished.next(new UserInfo('PSU_ID', user.toString()));
    if (acc !== AccountRefType.ALL) {
      this.globals.userInfoPublished.next(
        new UserInfo(acc.toString(), user === Users.ANTON ? 'DE80760700240271232400' : 'DE38760700240320465700')
      );
    } else {
      this.globals.userInfoPublished.next(
        new UserInfo(acc.toString(), 'ALL_ACCOUNTS')
      );
    }
    this.globals.userInfoPublished.next(new UserInfo('ais.recurringIndicator', true));
    this.globals.userInfoPublished.next(new UserInfo('ais.frequencyPerDay', 12));
    this.globals.userInfoPublished.next(new UserInfo('ais.validUntil', '2030-01-01'));
  }
}

export enum AccountRefType {
  ALL = 'ais.allAccounts',
  ACCOUNT = 'ais.accounts',
  BALANCES = 'ais.balances',
  TRANSACTIONS = 'ais.transactions'
}

export enum Users {
  ANTON = 'anton.brueckner',
  MAX = 'max.musterman',
}
