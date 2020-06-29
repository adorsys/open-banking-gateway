import { SessionService } from '../../common/session.service';
import { AisConsentToGrant } from './dto/ais-consent';
import { PisPayment } from './models/pis-payment.model';

export class PaymentUtil {
  public static getOrDefault(authorizationId: string, storageService: SessionService): PisPayment {
    if (!storageService.getConsentObject(authorizationId, () => new PisPayment())) {
      storageService.setPaymentObject(authorizationId, PaymentUtil.initializePaymentObject());
    }

    return storageService.getPaymentObject(authorizationId, () => new PisPayment());
  }

  private static initializePaymentObject(): AisConsentToGrant {
    return new PisPayment();
  }
}
