import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './dashboard.component';
import { HomeComponent } from './home/home.component';

const routes: Routes = [
  {
    path: ':id',
    component: DashboardComponent,
    children: [
      {
        path: '',
        component: HomeComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule {}
