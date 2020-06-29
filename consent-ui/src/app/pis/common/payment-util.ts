import { SessionService } from '../../common/session.service';
import { PisPayment } from './models/pis-payment.model';

export class PaymentUtil {
  public static getOrDefault(authorizationId: string, storageService: SessionService): PisPayment {
    if (!storageService.getPaymentObject(authorizationId, () => new PisPayment())) {
      storageService.setPaymentObject(authorizationId, PaymentUtil.initializePaymentObject());
    }

    return storageService.getPaymentObject(authorizationId, () => new PisPayment());
  }

  private static initializePaymentObject(): PisPayment {
    return new PisPayment();
  }
}
