import { RouterModule, Routes } from '@angular/router';
import { Login } from './components/login/login';
import { Dashboard } from './components/dashboard/dashboard';
import { NgModule } from '@angular/core';
import { CadastroUsuario } from './components/cadastro-usuario/cadastro-usuario';
import { CadastroProduto } from './components/cadastro-produto/cadastro-produto';
import { Movimentacao } from './components/movimentacao/movimentacao';
import { FormaPagamento } from './components/forma-pagamento/forma-pagamento';
import { authGuard } from './guards/auth-guard';
import { CadastroCliente } from './components/cadastro-cliente/cadastro-cliente';
import { Servicos } from './components/servicos/servicos';

//aqui criamos as rotas das paginas
export const routes: Routes = [
    {path: '', component: Login},
    {path: 'dashboard', component: Dashboard, canActivate: [authGuard]},
    {path: 'cadastro-cliente', component: CadastroCliente, canActivate: [authGuard]},
    {path: 'cadastro-usuario', component: CadastroUsuario, canActivate: [authGuard]},
    {path: 'cadastro-produto', component: CadastroProduto, canActivate: [authGuard]},
    {path: 'movimentacao', component: Movimentacao, canActivate: [authGuard]},
    {path: 'servicos', component: Servicos, canActivate: [authGuard]},
    {path: 'forma-pagamento', component: FormaPagamento, canActivate: [authGuard]},
    // rota para caso o usuario digite algo que nao existe, ele cai no login de novo
    {path: '**', redirectTo: ''}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)], // isso e para que o roteador saiba nossas rotas
    exports: [RouterModule]  // exporta para o resto do projeto
})
export class AppRoutingModule {}