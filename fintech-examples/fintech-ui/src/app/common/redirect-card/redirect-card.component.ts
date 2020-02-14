import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-redirect-page',
  templateUrl: './redirect-card.component.html',
  styleUrls: ['./redirect-card.component.scss']
})
export class RedirectCardComponent implements OnInit {
  constructor(private router: Router) {}

  ngOnInit() {}

  proceed(): void {}

  cancel(): void {
    this.router.navigate(['/search']);
  }
}
