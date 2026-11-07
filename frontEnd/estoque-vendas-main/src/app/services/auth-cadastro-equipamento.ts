import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Equipamento } from '../models/equipamento';

@Injectable({
  providedIn: 'root'
})
export class EquipamentoService {
  
  // Rota base casando com o @RequestMapping("/equipamento") do Back-End
  private apiUrl = 'http://localhost:8080/equipamento'; 

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const usuarioLogado = localStorage.getItem('usuarioLogado') || 'Admin'; 
    
    return new HttpHeaders({
      'X-Usuario-Logado': usuarioLogado,
      'Content-Type': 'application/json'
    });
  }

  // GET /equipamento/listar
  listar(): Observable<Equipamento[]> {
    return this.http.get<Equipamento[]>(`${this.apiUrl}/listar`, { headers: this.getHeaders() });
  }

  // GET /equipamento/buscar/{id}
  buscarPorId(id: number): Observable<Equipamento> {
    return this.http.get<Equipamento>(`${this.apiUrl}/buscar/${id}`, { headers: this.getHeaders() });
  }

  // POST /equipamento/cadastro
  cadastrar(equipamento: Equipamento): Observable<Equipamento> {
    return this.http.post<Equipamento>(`${this.apiUrl}/cadastro`, equipamento, { headers: this.getHeaders() });
  }

  // PUT /equipamento/atualizar/{id} -> ROTA ATUALIZADA AQUI!
  atualizar(id: number, equipamento: Equipamento): Observable<Equipamento> {
    return this.http.put<Equipamento>(`${this.apiUrl}/atualizar/${id}`, equipamento, { headers: this.getHeaders() });
  }

  // DELETE /equipamento/{id}
  deletar(id: number): Observable<void> {
    // Atenção: Se no seu Java o delete for @DeleteMapping("/deletar/{id}"), atualize aqui para `${this.apiUrl}/deletar/${id}` também.
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }
}