import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PedidoDetalheDTO, PedidoListDTO, PedidoRequestDTO, PagPedidoRequestDTO } from '../models/pedido-dto';

@Injectable({
  providedIn: 'root',
})
export class AuthPedido {
  private apiUrl = 'http://localhost:8080/pedido';

  constructor(private http: HttpClient) {}

  listarPedidos(): Observable<PedidoListDTO[]> {
    return this.http.get<PedidoListDTO[]>(`${this.apiUrl}/listAll`);
  }

  buscarPedidoPorId(id: number): Observable<PedidoDetalheDTO> {
    return this.http.get<PedidoDetalheDTO>(`${this.apiUrl}/buscar/${id}`);
  }

  criarPedido(dto: PedidoRequestDTO, usuarioLogado: string): Observable<any> {
    const headers = new HttpHeaders({ 'X-Usuario-Logado': usuarioLogado });
    return this.http.post<any>(`${this.apiUrl}/criar`, dto, { headers });
  }

  atualizarPedido(id: number, dto: PedidoRequestDTO, usuarioLogado: string): Observable<any> {
    const headers = new HttpHeaders({ 'X-Usuario-Logado': usuarioLogado });
    return this.http.put<any>(`${this.apiUrl}/atualizar/${id}`, dto, { headers });
  }

  finalizarPedido(id: number, pagamentos: PagPedidoRequestDTO[], usuarioLogado: string): Observable<any> {
    const headers = new HttpHeaders({ 'X-Usuario-Logado': usuarioLogado });
    return this.http.post<any>(`${this.apiUrl}/finalizar/${id}`, pagamentos, { headers });
  }

  devolverPedido(id: number, usuarioLogado: string): Observable<any> {
    const headers = new HttpHeaders({ 'X-Usuario-Logado': usuarioLogado });
    return this.http.post<any>(`${this.apiUrl}/devolucao/${id}`, {}, { headers });
  }

  // O back-end devolve TEXTO PURO aqui, não JSON — por isso responseType 'text'.
  // Sem isso, o Angular tenta dar JSON.parse na string e a chamada cai em error:
  // mesmo quando o cancelamento deu certo.
  cancelarPedido(id: number, usuarioLogado: string): Observable<string> {
    const headers = new HttpHeaders({ 'X-Usuario-Logado': usuarioLogado });
    return this.http.delete(`${this.apiUrl}/cancelar/${id}`, {
      headers,
      responseType: 'text',
    });
  }
}