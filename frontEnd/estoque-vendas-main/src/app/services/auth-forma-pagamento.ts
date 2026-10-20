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

  inativarFormaPagamento(descricao: string, usuarioLogado: string): Observable<any> {
    const headers = new HttpHeaders({
      'X-Usuario-Logado': usuarioLogado
    });

    return this.http.patch<any>(`${this.apiUrl}/delete/${descricao}`, {}, { headers: headers });
  }

  cadastroFormaPagamento(descricao: string, usuarioLogado: string): Observable<any> {
    const dadosCadastroFormaPagamento = {
      descricao: descricao
    };
    const headers = new HttpHeaders({
      'X-Usuario-Logado': usuarioLogado
    });

    return this.http.post<any>(`${this.apiUrl}/cadastro`, dadosCadastroFormaPagamento, { headers: headers });
  }

  // ---> NOVO MÉTODO DE ATUALIZAÇÃO AQUI <---
  atualizarFormaPagamento(descricaoAntiga: string, novaDescricao: string, usuarioLogado: string): Observable<any> {
    const dadosAtualizarFormaPagamento = {
      descricao: novaDescricao
    };
    const headers = new HttpHeaders({
      'X-Usuario-Logado': usuarioLogado
    });

    // Passamos a descricaoAntiga na URL (para o back-end encontrar) e a novaDescricao no Body (para ele atualizar)
    return this.http.patch<any>(`${this.apiUrl}/update/${descricaoAntiga}`, dadosAtualizarFormaPagamento, { headers: headers });
  }
}