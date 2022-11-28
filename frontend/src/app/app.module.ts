import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomePageComponent } from './components/home-page/home-page.component';

import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { LoginComponent } from './components/login/login.component'
import { AuthInterceptor } from './authInterceptor';
import { ServerDisplayComponent } from './components/server-display/server-display.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { FormsModule } from '@angular/forms';
import { SettingsComponent } from './components/settings/settings.component';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuardService } from './services/auth-guard-service.service';
import { JoinPageComponent } from './components/join-page/join-page.component';
import { ChatComponent } from './components/chat/chat.component';

const routes: Routes = [
  { path: 'home', component: HomePageComponent, canActivate: [AuthGuardService] },
  { path: 'login', component: LoginComponent, canActivate: [AuthGuardService] },
  { path: 'join', component: JoinPageComponent, canActivate: [AuthGuardService] },
  { path: '**', redirectTo: 'login'},
]

@NgModule({
  declarations: [
    AppComponent,
    HomePageComponent,
    LoginComponent,
    ServerDisplayComponent,
    SettingsComponent,
    JoinPageComponent,
    ChatComponent
  ],
  imports: [
    RouterModule.forRoot(routes),
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FontAwesomeModule,
    FormsModule
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
