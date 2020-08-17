import { SinglePaymentInitiationRequest } from '../model/singlePaymentInitiationRequest';

export class ClassSinglePaymentInitiationRequest implements SinglePaymentInitiationRequest{
  name: string;
  creditorIban: string;
  debitorIban: string;
  amount: string;
  purpose?: string;
  instantPayment?: boolean;
}
