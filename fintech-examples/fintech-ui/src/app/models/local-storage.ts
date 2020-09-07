import {Consts} from './consts';

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
}
