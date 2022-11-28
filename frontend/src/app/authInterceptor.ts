import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    constructor() {}

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const username = sessionStorage.getItem('username');
        const password = sessionStorage.getItem('password');
        const authString = 'Basic ' + btoa(username + ':' + password);
        
        if (username && password) {
            req = req.clone({
                setHeaders: {
                    Authorization: authString
                }
            })
        }
        return next.handle(req);
    }

}