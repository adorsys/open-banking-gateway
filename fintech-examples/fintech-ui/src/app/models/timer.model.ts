export class TimerModel {
  started: boolean;
  sessionValidUntil: string;
  redirectsValidUntil?: Array<string>;
}
