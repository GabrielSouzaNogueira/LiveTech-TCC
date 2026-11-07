import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Equipamento } from '../models/equipamento';

@Injectable({
  providedIn: 'root'
})
export class EquipamentoService {
  
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

  // PUT /equipamento/atualizar/{id}
  atualizar(id: number, equipamento: Equipamento): Observable<Equipamento> {
    return this.http.put<Equipamento>(`${this.apiUrl}/atualizar/${id}`, equipamento, { headers: this.getHeaders() });
  }

  // PUT /equipamento/deletar/{id} -> AJUSTADO PARA SOFT DELETE (PUT)
  // Retorna 'any' porque o seu back-end agora devolve um ResponseDTO e não mais void/vazio.
  deletar(id: number): Observable<any> {
    // Passamos 'null' no meio porque o PUT exige um body, mas o Java não pede nenhum @RequestBody
    return this.http.put<any>(`${this.apiUrl}/deletar/${id}`, null, { headers: this.getHeaders() });
  }
}