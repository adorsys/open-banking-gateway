import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { AnonymousComponent } from './anonymous/anonymous.component';

const routes: Routes = [
  {
    path: 'pis/:authId',
    redirectTo: ':authId'
  },
  {
    path: 'ais/:authId',
    redirectTo: ':authId'
  },
  {
    path: ':authId',
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'anonymous', component: AnonymousComponent },
      { path: 'register', component: RegisterComponent }
    ]
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthRoutingModule {}
