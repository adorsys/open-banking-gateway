import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ActivatedRouteSnapshot, Router } from '@angular/router';
import { SessionService } from '../../common/session.service';
import { AuthService } from '../../common/auth.service';
import { ApiHeaders } from '../../api/api.headers';
import { CustomizeService } from '../../services/customize.service';

@Component({
  selector: 'consent-app-anonymous',
  templateUrl: './anonymous.component.html',
  styleUrls: ['../auth.component.scss'],
  standalone: false
})
export class AnonymousComponent implements OnInit {
  redirectCode: string;
  private route: ActivatedRouteSnapshot;
  private authId: string;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private sessionService: SessionService,
    private authService: AuthService,
    public customizeService: CustomizeService
  ) {}

  ngOnInit() {
    this.route = this.activatedRoute.snapshot;
    this.authId = this.route.parent.params.authId;
    this.redirectCode = this.route.queryParams.redirectCode;
    if (this.redirectCode) {
      this.sessionService.setRedirectCode(this.authId, this.redirectCode);
    } else {
      this.redirectCode = this.sessionService.getRedirectCode(this.authId);
    }

    this.doLoginAsAnonymous();
  }

  private doLoginAsAnonymous() {
    localStorage.setItem(ApiHeaders.COOKIE_TTL, '0');
    this.authService.userLoginForAnonymous(this.authId, this.redirectCode).subscribe((res) => {
      this.sessionService.setTTL(this.authId, res.headers.get(ApiHeaders.COOKIE_TTL));
      window.location.href = res.headers.get(ApiHeaders.LOCATION);
    });
  }
}
