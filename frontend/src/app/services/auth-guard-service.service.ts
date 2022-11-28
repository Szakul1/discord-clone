import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router } from '@angular/router';
import { url } from 'inspector';
import { AuthenticationService } from './authentication.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService implements CanActivate {

  constructor(private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const authString = sessionStorage.getItem('username') && sessionStorage.getItem('password');
    switch (route.url[0].path) {
      case 'home': case 'join':
        if (!authString) {
          this.router.navigateByUrl('login');
          return false;
        }
        return true;
    
      case 'login':
        if (authString) {
          this.router.navigateByUrl('home');
          return false;
        }
        return true;
    }
    return true;
  }

}
