import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-relatorios',
  templateUrl: './relatorios.html',
  styleUrls: ['./relatorios.css'],
  standalone: true,
  imports: [CommonModule, RouterModule] // O RouterModule é essencial aqui para os botões internos funcionarem
})
export class RelatoriosComponent {
  // A lógica desta tela principal é bem simples, serve mais como um menu
}