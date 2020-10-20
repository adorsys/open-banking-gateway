import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CustomizeService {
  private DEFAULT_THEME: Theme = {
    globalSettings: {
      logo: '../../assets/UI/Logo_OPBA.png'
    }
  };

  constructor() {}

  getLogo() {
    return this.DEFAULT_THEME.globalSettings.logo;
  }
}

export interface Theme {
  globalSettings: GlobalSettings;
}

export interface GlobalSettings {
  logo: string;
  favicon?: Favicon;
  cssVariables?: CSSVariables;
}

export interface Favicon {
  type: string;
  href: string;
}

export interface CSSVariables {
  [key: string]: string;

  colorPrimary?: string;
  fontFamily?: string;
  bodyBG?: string;
  headerBG?: string;
  headerFontColor?: string;
  sidebarBG?: string;
  sidebarFontColor?: string;
  mainBG?: string;
  anchorFontColor?: string;
  anchorFontColorHover?: string;
}
