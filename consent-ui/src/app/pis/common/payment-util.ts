import { SessionService } from '../../common/session.service';
import { PisPayment } from './models/pis-payment.model';

export class PaymentUtil {
  public static getOrDefault(authorizationId: string, storageService: SessionService): PisPayment {
    if (!storageService.getPaymentObject(authorizationId)) {
      storageService.setPaymentObject(authorizationId, PaymentUtil.initializePaymentObject());
    }

    return storageService.getPaymentObject(authorizationId);
  }

  private static initializePaymentObject(): PisPayment {
    return new PisPayment();
  }
}
