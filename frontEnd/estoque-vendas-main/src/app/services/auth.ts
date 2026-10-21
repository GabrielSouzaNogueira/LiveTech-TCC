import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class Auth {
  //aqui e url basica da API
  private apiUrl = 'http://localhost:8080/usuario/login'

  //para fazer requisiçoes http como get post etc.
  constructor(private http: HttpClient) {}

  //ele vai recever os metodos e preparar pra enviar para o spring bot, o Observable<any> 
  //funciona como um promisse esperando uma resposta do servidor
  login(nome: string, senha: string): Observable<any> {
    const dadosLogin = {
      nome: nome,
      senha: senha
    };
    // envoando um post para a url enviando o dadosLogin
    return this.http.post<any>(this.apiUrl, dadosLogin);
  }
}
