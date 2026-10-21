import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  usuarioNome: string = 'Usuário';

  constructor(private router: Router) {}

  ngOnInit(): void {
    const logado = localStorage.getItem('usuarioLogado');
    if (logado) {
      // Capitaliza a primeira letra do nome (ex: "gabriel" -> "Gabriel")
      this.usuarioNome = logado.charAt(0).toUpperCase() + logado.slice(1).toLowerCase();
    }
  }

  navegarPara(rota: string): void {
    this.router.navigate([rota]);
  }

  fazerLogout(): void {
    localStorage.removeItem('usuarioLogado');
    this.router.navigate(['']);
  }
}