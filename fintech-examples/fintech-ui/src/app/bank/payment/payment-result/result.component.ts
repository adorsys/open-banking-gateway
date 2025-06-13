import { Component } from '@angular/core';

@Component({
  selector: 'app-result',
  templateUrl: './result.component.html',
  styleUrls: ['./result.component.scss'],
  standalone: true,
  imports: []
})
export class ResultComponent {
  public static ROUTE = 'result';

  onConfirm(): void {
    // This is the final step of the payment flow
  }
}
