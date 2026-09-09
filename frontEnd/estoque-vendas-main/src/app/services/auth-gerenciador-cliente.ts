import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ClienteSelectDTO } from '../models/cliente-select-dto';

@Injectable({
  providedIn: 'root',
})
export class AuthGerenciadorCliente {
  
  // URL base idêntica à do @RequestMapping no Java
  private apiUrl = 'http://localhost:8080/cliente';

  constructor(private http: HttpClient) {}

  // --- LISTAR CLIENTES ---
  listarClientes(): Observable<ClienteSelectDTO[]> {
    // Apontando exatamente para o @GetMapping("/listAll")
    return this.http.get<ClienteSelectDTO[]>(`${this.apiUrl}/listAll`);
  }

  // --- ATUALIZAR CLIENTE ---
  atualizarCliente(
    id: string, 
    nome: string, 
    sobrenome: string, 
    email: string, 
    telefone: string, 
    usuarioLogado: string
  ): Observable<any> {
    
    const dadosAtualizacao = {
      nome: nome,
      sobrenome: sobrenome,
      email: email,
      telefone: telefone
    };

    const headers = new HttpHeaders({
      'X-Usuario-Logado': usuarioLogado
    });

    // Apontando para o @PutMapping("/atualizar/{id}")
    return this.http.put<any>(
      `${this.apiUrl}/atualizar/${id}`, 
      dadosAtualizacao, 
      { headers: headers }
    );
  }

  // --- INATIVAR CLIENTE ---
  inativarCliente(id: string, usuarioLogado: string): Observable<any> {
    const headers = new HttpHeaders({
      'X-Usuario-Logado': usuarioLogado
    });

    // Apontando para o @DeleteMapping("/deletar/{id}")
    return this.http.delete<any>(
      `${this.apiUrl}/deletar/${id}`, 
      { headers: headers }
    );
  }
}