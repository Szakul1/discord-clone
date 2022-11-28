import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CreateUser } from 'src/app/interfaces/create-user';
import { StatusCode } from 'src/app/interfaces/statusCode';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { UserService } from 'src/app/services/user.service';

enum Page {
  login,
  register,
  successRegister,
  emailConfirmed,
  userNotEnabled,
  error
}

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  pageType = Page;
  page: Page = Page.login;
  error: boolean = false;
  usernameTaken: boolean = false;
  emailTaken: boolean = false;
  user?: CreateUser;

  constructor(private router: Router, private authenticationService: AuthenticationService,
     private userService: UserService,
     private queryParam: ActivatedRoute) { }

  ngOnInit(): void {
    this.queryParam.queryParams.subscribe( params => {
      const token: string = params.token;
      if (token) {
        this.userService.confirmEmail(token).subscribe(() => 
          this.showEmailConfirmed(),
          error => {
            this.page = Page.error;
          }
        )
      }
    })
  }

  login(loginForm: NgForm) {
    const username = loginForm.value.username;
    const password = loginForm.value.password;
    this.authenticationService.authenticate(username, password).subscribe(user => {
        this.userService.user = user;
        sessionStorage.setItem('user', JSON.stringify(user)); // delete
        this.error = false;
        sessionStorage.setItem('username', username);
        sessionStorage.setItem('password', password);
        this.router.navigateByUrl('home');
      },
      (error: HttpErrorResponse) => {
        switch (error.error) {
          case StatusCode.USER_NOT_ENABLED:
            this.page = Page.userNotEnabled;
            this.user = {username: username, password: password};
            break;
          default:
            this.error = true;
        }
      }
    );
  }

  signUp(signUpForm: NgForm) {
    const form = signUpForm.value;
    const createUser: CreateUser = { username: form.username,
                               email: form.email,
                               password: form.password};
    this.userService.create(createUser).subscribe(
      () => {
        this.usernameTaken = false;
        this.emailTaken = false;
        this.showSuccessRegister();
      },
      (error: HttpErrorResponse) => {
        switch (error.error) {
          case StatusCode.USERNAME_TAKEN:
            this.usernameTaken = true;
            break;
          case StatusCode.EMAIL_TAKEN:
            this.emailTaken = true;
            break;
        }
      }
    );
  }

  showLogin() {
    this.error = false;
    this.page = Page.login;
  }
  
  showRegister() {
    this.page = Page.register;
  }
  
  showSuccessRegister() {
    this.page = Page.successRegister;
  }
  
  showEmailConfirmed() {
    this.page = Page.emailConfirmed;
  }

  resendToken() {
    this.showToast()
    this.userService.resendToken(this.user!).subscribe(() => this.showToast());
  }

  showToast() {
    let toast = document.getElementById('toast')!;
    toast.className = "show";
    setTimeout(() => toast.className = toast.className.replace("show", ""), 2000);
  }

  resendEmail(resendForm: NgForm) {
    this.userService.resendEmail(resendForm.value.email).subscribe(() => this.showToast());
  }

}
