import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-oauth2-login',
  templateUrl: './oauth2-login.component.html',
  styleUrls: ['./oauth2-login.component.scss'],
  standalone: true,
  imports: []
})
export class Oauth2LoginComponent implements OnInit {
  constructor(private router: Router, private activatedRoute: ActivatedRoute, private authService: AuthService) {}

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe((it) => {
      const state = it['state'];
      const code = it['code'];
      const scope = it['scope'];
      const error = it['error'];

      this.authService.oauth2Login(code, state, scope, error).subscribe((success) => {
        if (success) {
          this.router.navigate(['/search']);
        }
      });
    });
  }
}
