import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SelectFormPag } from '../models/forma-pagamento-dto';

@Injectable({
  providedIn: 'root',
})
export class AuthFormaPagamento {
  private apiUrl = 'http://localhost:8080/formPag';

  constructor(private http: HttpClient) {}

  listarFormaPagamento(): Observable<SelectFormPag[]> {
    return this.http.get<SelectFormPag[]>(`${this.apiUrl}/listar`);
  }

  // Busca por ID antes de abrir o modal/campos de edição
  buscarPorId(id: number, usuarioLogado: string): Observable<SelectFormPag> {
    const headers = new HttpHeaders({
      'X-Usuario-Logado': usuarioLogado
    });

    return this.http.get<SelectFormPag>(`${this.apiUrl}/buscar/${id}`, { headers });
  }

  // Atualização agora ancorada no ID em vez de depender da descrição textual
  atualizarFormaPagamento(id: number, novaDescricao: string, usuarioLogado: string): Observable<any> {
    const dadosAtualizarFormaPagamento = {
      descricao: novaDescricao
    };
    const headers = new HttpHeaders({
      'X-Usuario-Logado': usuarioLogado
    });

    return this.http.patch<any>(`${this.apiUrl}/update/${id}`, dadosAtualizarFormaPagamento, { headers });
  }
  
  inativarFormaPagamento(id: number, usuarioLogado: string): Observable<any> {
    const headers = new HttpHeaders({
      'X-Usuario-Logado': usuarioLogado
    });

    // A rota aqui concatena o ID numérico e bate com o @PatchMapping do Java
    return this.http.patch<any>(`${this.apiUrl}/delete/${id}`, {}, { headers });
  }

  cadastroFormaPagamento(descricao: string, usuarioLogado: string): Observable<any> {
    const dadosCadastroFormaPagamento = {
      descricao: descricao
    };
    const headers = new HttpHeaders({
      'X-Usuario-Logado': usuarioLogado
    });

    return this.http.post<any>(`${this.apiUrl}/cadastro`, dadosCadastroFormaPagamento, { headers });
  }
}