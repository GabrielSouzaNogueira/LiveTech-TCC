import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ServicosDTO } from '../models/servicos-dto';

@Injectable({
  providedIn: 'root',
})
export class AuthServicos {
  private apiUrl = 'http://localhost:8080/servicos';

  constructor(private http: HttpClient) {}

  cadastroServicos(descServico: string, precoServico: string, usuarioLogado: string): Observable<any> {
    const dadosCadastroServicos = {
      descServico: descServico,
      precoServico: precoServico
    };
    const headers = new HttpHeaders({
      'X-Usuario-Logado': usuarioLogado
    });

    return this.http.post<any>(`${this.apiUrl}/cadastrar`, dadosCadastroServicos, { headers: headers });
  }
}
