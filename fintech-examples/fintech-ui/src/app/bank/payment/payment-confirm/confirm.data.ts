import { RedirectStruct } from '../../redirect-page/redirect-struct';
import { SinglePaymentInitiationRequest } from '../../../api';

export class ConfirmData {
  paymentRequest: SinglePaymentInitiationRequest;
  redirectStruct: RedirectStruct;
}
