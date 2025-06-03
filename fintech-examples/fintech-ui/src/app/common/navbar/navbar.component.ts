import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { TimerService } from '../../services/timer.service';
import { TimerModel } from '../../models/timer.model';
import { StorageService } from '../../services/storage.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
  standalone: true,
  imports: [CommonModule]
})
export class NavbarComponent implements OnInit {
  timer: TimerModel;

  constructor(
    private timerService: TimerService,
    private authService: AuthService,
    private storageService: StorageService
  ) {
    this.timerService.startTimer();
  }

  ngOnInit(): void {
    this.timerService.timerStatusChanged$.subscribe((timer) => {
      if (timer.started) {
        this.timer = { ...timer };
      }
    });
  }

  onLogout() {
    this.authService.logout();
    this.timerService.stopTimer();
    this.storageService.clearStorage();
    this.storageService.deleteSettings();
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  getUserName(): string {
    return this.authService.getUserName();
  }
}
