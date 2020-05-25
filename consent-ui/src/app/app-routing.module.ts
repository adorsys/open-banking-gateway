import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

const routes: Routes = [
  {
    path: 'ais',
    loadChildren: () => import('./ais/ais.module').then(m => m.AisModule)
  },
  {
    path: 'pis',
    loadChildren: () => import('./pis/pis.module').then(m => m.PisModule)
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
