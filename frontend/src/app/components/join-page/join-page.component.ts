import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { url } from 'inspector';
import { Server } from 'src/app/interfaces/server';
import { ServerService } from 'src/app/services/server.service';

@Component({
  selector: 'app-join-page',
  templateUrl: './join-page.component.html',
  styleUrls: ['./join-page.component.css']
})
export class JoinPageComponent implements OnInit {
  server?: Server
  token?: string;

  constructor(private serverService: ServerService, private queryParam: ActivatedRoute, private router: Router) { }

  ngOnInit(): void {
    this.queryParam.queryParams.subscribe( params => {
      this.token = params.token;
      if (this.token) {
        this.serverService.getByToken(this.token).subscribe(server =>
          this.server = server);
      }
    })
  }

  accept() {
    this.serverService.joinServer(this.token!).subscribe(() => this.router.navigateByUrl('home'),
    error => this.router.navigateByUrl('home'));
  }

  getLogo(): string {
    if (this.server?.logo)
      return "url('data:image/jpeg;base64," + this.server?.logo + "')";
    return "url(../../../assets/login-background.png)";
  }

}
