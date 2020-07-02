import { ClassSinglePaymentInitiationRequest } from '../../../api/model-classes/ClassSinglePaymentInitiationRequest';
import { RedirectStruct } from '../../redirect-page/redirect-struct';

export class ConfirmData {
  paymentRequest: ClassSinglePaymentInitiationRequest;
  redirectStruct: RedirectStruct;
}
