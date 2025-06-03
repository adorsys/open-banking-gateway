import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SidebarComponent } from './sidebar/sidebar.component';

@Component({
  selector: 'app-dashboard',
  templateUrl: './bank.component.html',
  styleUrls: ['./bank.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterModule, SidebarComponent]
})
export class BankComponent {}
