import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { 
  OsRequestDTO, OsListDTO, 
  ClienteResumoDTO, ProdutoResumoDTO, ServicoResumoDTO 
} from '../models/os-dto'; 

@Injectable({
  providedIn: 'root',
})
export class AuthOS {
  private apiUrl = 'http://localhost:8080/os';
  
  private urlClientes = 'http://localhost:8080/cliente/listAll'; 
  private urlProdutos = 'http://localhost:8080/produto/listProd';
  private urlServicos = 'http://localhost:8080/servicos/listAll';

  constructor(private http: HttpClient) {}

  // --- CRUD da OS ---
  criarOS(dto: OsRequestDTO, usuarioLogado: string): Observable<OsListDTO> {
    const headers = new HttpHeaders({ 'X-Usuario-Logado': usuarioLogado });
    return this.http.post<OsListDTO>(`${this.apiUrl}/criar`, dto, { headers });
  }

  atualizarOS(id: number, dto: OsRequestDTO, usuarioLogado: string): Observable<any> {
    const headers = new HttpHeaders({ 'X-Usuario-Logado': usuarioLogado });
    return this.http.put<any>(`${this.apiUrl}/atualizar/${id}`, dto, { headers });
  }

  finalizarOS(id: number, usuarioLogado: string): Observable<any> {
    const headers = new HttpHeaders({ 'X-Usuario-Logado': usuarioLogado });
    return this.http.patch<any>(`${this.apiUrl}/finalizar/${id}`, {}, { headers });
  }

  listarTodas(): Observable<OsListDTO[]> {
    return this.http.get<OsListDTO[]>(this.apiUrl);
  }

  deletarOS(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/deletar/${id}`);
  }

  // --- Buscas para Selects ---
  listarClientes(): Observable<ClienteResumoDTO[]> {
    return this.http.get<ClienteResumoDTO[]>(this.urlClientes);
  }

  listarProdutos(): Observable<ProdutoResumoDTO[]> {
    return this.http.get<ProdutoResumoDTO[]>(this.urlProdutos);
  }

  listarServicos(): Observable<ServicoResumoDTO[]> {
    return this.http.get<ServicoResumoDTO[]>(this.urlServicos);
  }
}