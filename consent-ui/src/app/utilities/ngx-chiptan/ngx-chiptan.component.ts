import { Component, Input, OnInit } from '@angular/core';
import { flickerCanvas, flickerCode } from './flicker';

@Component({
  selector: 'consent-app-chiptan',
  template: ` <div id="flickercontainer"></div> `,
  standalone: true
})
export class NgxChiptanComponent implements OnInit {
  @Input()
  code: string | undefined;

  @Input()
  width: string;

  @Input()
  height: string;

  @Input()
  bgColor: string;

  @Input()
  barColor: string;

  private state: any;
  private interval: any;

  ngOnInit() {
    if (!this.code) {
      return;
    }

    this.state = {
      canvas: new flickerCanvas(this.width, this.height, this.bgColor, this.barColor),
      code: new flickerCode(this.code)
    };

    const { canvas, code } = this.state;

    document.getElementById('flickercontainer').appendChild(canvas.getCanvas());

    canvas.setCode(code);

    this.startFlicker();
  }

  startFlicker() {
    this.interval = setInterval(this.step.bind(this), 50);
  }

  stopFlicker() {
    clearInterval(this.interval);
  }

  step() {
    this.state.canvas.step();
  }
}
