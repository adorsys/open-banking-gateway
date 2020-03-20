import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SidebarComponent } from './sidebar.component';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BankProfileService } from '../../bank-search/services/bank-profile.service';
import { MockActivatedRoute } from '../../services/mock-active-router';

describe('SidebarComponent', () => {
  let component: SidebarComponent;
  let fixture: ComponentFixture<SidebarComponent>;
  let activatedRoute: MockActivatedRoute;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SidebarComponent],
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [BankProfileService, MockActivatedRoute]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SidebarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should get bankId from activated route', () => {
    activatedRoute = new MockActivatedRoute();
    expect(component.bankId).toBeUndefined();
    activatedRoute.snapshot.paramMap.subscribe(id => (component.bankId = id));

    fixture.detectChanges();
    expect(component.bankId).toEqual('xxxxx');
  });
});
