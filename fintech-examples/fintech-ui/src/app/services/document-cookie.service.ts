import { Injectable } from '@angular/core';
import { LogException } from '../models/LogException';

@Injectable({
  providedIn: 'root'
})
export class DocumentCookieService {
  public getAll(): string[] {
    return document.cookie
      .split(';')
      .map((cookie) => cookie.trim())
      .filter((cookie) => cookie.length > 0);
  }

  public find(name: string): string {
    for (const cookie of this.getAll()) {
      if (cookie.startsWith(name + '=')) {
        const value = cookie.trim().substr(name.length + 1);
        if (value.length > 0) {
          // console.log('found cookie ' + name);
          return value;
        }
      }
    }
    // console.log('did not find cookie ' + name);
  }

  public delete(name: string): void {
    document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    this.find(name); // this call is important. due to date cookie will be deleted
  }

  public exists(name: string): boolean {
    return this.find(name) !== undefined;
  }

  public get(name: string): string {
    if (!this.exists(name)) {
      throw new LogException('DID NOT FIND COOKIE FOR ' + name);
    }
    return this.find(name);
  }
}
