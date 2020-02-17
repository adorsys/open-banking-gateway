import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-redirect-page',
  templateUrl: './redirect-page.component.html',
  styleUrls: ['./redirect-page.component.scss']
})
export class RedirectPageComponent implements OnInit {
  private bankId;

  constructor(private router: Router, private route: ActivatedRoute) {}

  ngOnInit() {
    this.bankId = this.route.snapshot.paramMap.get('id');
  }

  cancel(): void {
    this.router.navigate(['']);
  }

  proceed(): void {
    this.router.navigate(['/dashboard', this.bankId]);
  }
}
