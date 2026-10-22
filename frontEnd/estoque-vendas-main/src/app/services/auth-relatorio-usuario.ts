import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UsuariosInativadosDTO } from '../models/relatorio-usuarios-dto';

@Injectable({
  providedIn: 'root'
})
export class RelatorioUsuarioService {
  private apiUrl = 'http://localhost:8080/relatorio/usuario'; // Ajuste se a porta for diferente

  constructor(private http: HttpClient) {}

  listarUsuariosInativos(): Observable<UsuariosInativadosDTO[]> {
    // Pega o usuário logado do localStorage (ajuste a chave se usar outro nome)
    const usuarioLogado = localStorage.getItem('usuarioLogado') || 'Sistema'; 
    const headers = new HttpHeaders().set('X-Usuario-Logado', usuarioLogado);

    return this.http.get<UsuariosInativadosDTO[]>(`${this.apiUrl}/inativos`, { headers });
  }
}