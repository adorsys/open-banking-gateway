import { AuthViolation } from '../../../api';

export class AuthConsentState {
  constructor(public violations?: AuthViolation[]) {}

  public hasPisViolation(): boolean {
    return this.violations && this.violations.filter(it => it.scope && it.scope.startsWith('PIS')).length > 0;
  }

  public hasGeneralViolation(): boolean {
    return this.violations && this.getGeneralViolations().length > 0;
  }

  public getGeneralViolations(): AuthViolation[] {
    return this.violations.filter(it => it.scope && 'GENERAL' === it.scope);
  }
}
