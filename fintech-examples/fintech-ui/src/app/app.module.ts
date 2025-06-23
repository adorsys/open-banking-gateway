import { NgModule } from '@angular/core';

import { environment } from '../environments/environment';
import { ApiModule, Configuration, ConfigurationParameters } from './api';

export function apiConfigFactory(): Configuration {
  const params: ConfigurationParameters = {
    basePath: environment.FINTECH_API,
    withCredentials: true
  };

  return new Configuration(params);
}

@NgModule({
  imports: [ApiModule.forRoot(apiConfigFactory)]
})
export class AppModule {}
