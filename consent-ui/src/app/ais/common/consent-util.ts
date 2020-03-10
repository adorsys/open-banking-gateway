import {SessionService} from "../../common/session.service";
import {AisConsent} from "./dto/ais-consent";

export class ConsentUtil {

  public static getOrDefault(authorizationId: string, storageService: SessionService) : AisConsent {
    if (!storageService.getConsentObject(authorizationId, () => new AisConsent())) {
      storageService.setConsentObject(authorizationId, ConsentUtil.initializeConsentObject());
    }

    return storageService.getConsentObject(authorizationId, () => new AisConsent());
  }

  private static initializeConsentObject() : AisConsent {
    const aisConsent = new AisConsent();
    // FIXME: These fields MUST be initialized by FinTech through API and user can only adjust it.
    aisConsent.frequencyPerDay = 10;
    aisConsent.recurringIndicator = true;
    aisConsent.validUntil = ConsentUtil.futureDate().toISOString().split("T")[0];
    return aisConsent;
  }

  // TODO: should be removed when form is filled by FinTech
  private static futureDate() : Date {
    const result = new Date();
    result.setDate(result.getDate() + 365);
    return result;
  }
}
