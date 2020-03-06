import { Component, OnDestroy, OnInit } from '@angular/core';
import { AisService } from '../services/ais.service';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { concatMap } from 'rxjs/operators';
import { AccountDetails } from '../../api';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {
  bankID = '';

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.params.forEach(param => {
      this.bankID = param.id;
    });
  }

  ngOnDestroy(): void {}
}
