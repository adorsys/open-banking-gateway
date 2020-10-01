import { SessionService } from '../../common/session.service';
import { AccountAccess, AisConsent, AisConsentToGrant } from './dto/ais-consent';

export class ConsentUtil {
  public static getOrDefault(authorizationId: string, storageService: SessionService): AisConsentToGrant {
    if (!storageService.getConsentObject(authorizationId, () => new AisConsentToGrant())) {
      storageService.setConsentObject(authorizationId, ConsentUtil.initializeConsentObject());
    }

    return storageService.getConsentObject(authorizationId, () => new AisConsentToGrant());
  }

  private static initializeConsentObject(): AisConsentToGrant {
    const aisConsent = new AisConsentToGrant();
    // FIXME: These fields MUST be initialized by FinTech through API and user can only adjust it.
    aisConsent.consent = new AisConsentImpl();
    aisConsent.consent.access = new AccountAccess();
    aisConsent.consent.frequencyPerDay = 24; // Setting larger value as 10 exhausts very fast
    aisConsent.consent.recurringIndicator = true;
    aisConsent.consent.validUntil = ConsentUtil.futureDate().toISOString().split('T')[0];
    return aisConsent;
  }

  // TODO: should be removed when form is filled by FinTech
  private static futureDate(): Date {
    const result = new Date();
    result.setDate(result.getDate() + 365);
    return result;
  }
}

class AisConsentImpl implements AisConsent {
  access: AccountAccess;
  frequencyPerDay: number;
  recurringIndicator;
  validUntil: string;
}
