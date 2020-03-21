import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Consts} from "../../common/consts";

@Component({
  selector: 'app-redirect-page',
  templateUrl: './redirect-page.component.html',
  styleUrls: ['./redirect-page.component.scss']
})
export class RedirectPageComponent implements OnInit {
  private bankId;
  private bankName;
  private location;

  constructor(private router: Router, private route: ActivatedRoute) {
  }


  ngOnInit() {
    this.route.paramMap.subscribe(p => {
      this.location = decodeURIComponent(p.get('location'));
      console.log("LOCATION IS ", this.location);
    });
    this.bankName = localStorage.getItem(Consts.LOCAL_STORAGE_BANKNAME);
    this.bankId = this.route.parent.parent.parent.snapshot.paramMap.get('bankid');
    console.log('redirect page for bankid', this.bankId);
  }

  cancel(): void {
    this.router.navigate(['']);
  }

  proceed(): void {
    console.log("NOW GO TO:", this.location);
    //            window.location.href = response.headers.get('location') + '&' + additionalParameters;
    window.location.href = this.location;
  }
}
