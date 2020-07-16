import { Consts } from './consts';
import { SettingsData } from '../bank/settings/settings.component';

export class LocalStorage {
  public static isLoggedIn(): boolean {
    console.log('check logged in');
    const token = localStorage.getItem(Consts.LOCAL_STORAGE_XSRF_TOKEN);
    console.log('value of xsrf-token is ', token);
    return token !== undefined && token !== null;
  }

  public static login(xsrftoken) {
    localStorage.setItem(Consts.LOCAL_STORAGE_XSRF_TOKEN, xsrftoken);
  }
  public static logout(): void {
    localStorage.removeItem(Consts.LOCAL_STORAGE_XSRF_TOKEN);
  }

  public static getSettings(): SettingsData {
    const data = localStorage.getItem(Consts.LOCAL_STORAGE_SETTINGS)
    if (!data) {
      return null
    }

    return JSON.parse(data) as SettingsData
  }

  public static setSettings(data: SettingsData) {
    localStorage.setItem(Consts.LOCAL_STORAGE_SETTINGS, JSON.stringify(data));
  }
}
