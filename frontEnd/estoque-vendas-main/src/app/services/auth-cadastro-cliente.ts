import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthCadastroCliente {
  // Deixando a API URL na "raiz" do cliente
  private apiUrl = 'http://localhost:8080/cliente'

  constructor(private http: HttpClient){}

  cadastroCliente(nome: string, sobrenome: string, email: string, telefone: string, usuarioLogado: string): Observable<any> {
    
    // Removido o usuarioLogado daqui de dentro, pois o Java ClienteDTO não possui esse campo
    const dadosCadastroCliente = {
      nome: nome,
      sobrenome: sobrenome,
      email: email,
      telefone: telefone
    };

    const headers = new HttpHeaders({
      'X-Usuario-Logado': usuarioLogado
    });

    // Apontando para o endpoint "/cadastrar"
    return this.http.post<any>(`${this.apiUrl}/cadastrar`, dadosCadastroCliente, {headers: headers});
  }
}