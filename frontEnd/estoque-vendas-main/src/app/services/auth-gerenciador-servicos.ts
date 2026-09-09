import { Injectable } from '@angular/core';
import { ServicosDTO } from '../models/servicos-dto';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthGerenciadorServicos {

  // 1. Corrigido para bater com o @RequestMapping do Java
  private apiUrl = 'http://localhost:8080/servicos';

  constructor(private http: HttpClient) {}

  // Método para buscar a lista de serviços
  listarServicos(): Observable<ServicosDTO[]> {
    // 2. Corrigido para bater com o @GetMapping("/listAll")
    return this.http.get<ServicosDTO[]>(
      `${this.apiUrl}/listAll`
    );
  }

  // Método para inativar um serviço
  inativarServicos(
    servicosId: string,
    usuarioLogado: string
  ): Observable<any> {

    const headers = new HttpHeaders({
      'X-Usuario-Logado': usuarioLogado
    });

    // 3. Corrigido para http.delete e rota "/deletar"
    return this.http.delete<any>(
      `${this.apiUrl}/deletar/${servicosId}`,
      { headers: headers }
    );
  }

  // Método para atualizar um serviço
  atualizarServicos(
    servicosId: string,
    descServico: string,
    precoServico: string,
    usuarioLogado: string
  ): Observable<any> {

    // Removido o 'status' daqui pois o BackEnd atualiza apenas descrição e preço
    const dadosAtualizacao = {
      descServico: descServico,
      precoServico: precoServico
    };

    const headers = new HttpHeaders({
      'X-Usuario-Logado': usuarioLogado
    });

    // 4. Corrigido para http.put e rota "/atualizar"
    return this.http.put<any>(
      `${this.apiUrl}/atualizar/${servicosId}`,
      dadosAtualizacao,
      { headers: headers }
    );
  }
}