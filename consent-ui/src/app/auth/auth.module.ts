import { NgModule } from '@angular/core';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { AuthRoutingModule } from './auth-routing.module';
import { SharedModule } from '../common/shared.module';
import { AnonymousComponent } from './anonymous/anonymous.component';

@NgModule({
  declarations: [LoginComponent, AnonymousComponent, RegisterComponent],
  imports: [SharedModule, AuthRoutingModule]
})
export class AuthModule {}
