import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar {
  estaRecolhida: boolean = false;

  constructor(private router: Router) {}

  alternarSidebar(): void {
    this.estaRecolhida = !this.estaRecolhida;
  }

  fazerLogout(): void {
    localStorage.removeItem('usuarioLogado');
    this.router.navigate(['']);
  }
}