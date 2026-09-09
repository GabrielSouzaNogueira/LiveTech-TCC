# Descrição do projeto / TCC

# ERP & Sistema de Gestão de Vendas e Estoque

> Projeto de Trabalho de Conclusão de Curso (TCC) - Análise e Desenvolvimento de Sistemas (ADS).

Um sistema completo para gerenciamento de vendas, controle rigoroso de estoque e auditoria interna de movimentações. Desenvolvido com foco em regras de negócio sólidas e segurança em nível de aplicação, garantindo que operações críticas sejam executadas apenas por usuários autorizados.

---

## Funcionalidades Principais

*   **Gestão de Estoque:** Controle de entradas, saídas e baixa automática de produtos atrelados a ordens de serviço/vendas.
*   **Auditoria de Movimentações:** Rastreabilidade detalhada (log interno) de todas as ações realizadas no sistema (quem fez, o que fez e quando fez).
*   **Controle de Acesso Hierárquico (RBAC):** Sistema de permissões estruturado. Apenas perfis específicos (ex: Administradores/Gerentes) possuem autorização para ações destrutivas ou edição de dados sensíveis (como ordens já finalizadas).
*   **Gestão de Entidades:** CRUD completo e relacional de Clientes, Usuários, Produtos e Serviços.

---

## Tecnologias Utilizadas

O projeto adota uma separação clara entre as responsabilidades do servidor e da interface do cliente, utilizando o padrão Controller-Service-Repository no backend.

**Backend:**
*   **Java**
*   **Spring Boot**
*   **Spring Security** (Gerenciamento de acessos e hierarquia)
*   **JPA / Hibernate** (Persistência de dados)
*   **MariaDB / PostgreSQL** (Banco de Dados Relacional)

**Frontend:**
*   **React**
*   **TypeScript (TS)**
*   **HTML5 & CSS3**

---

## Contexto Arquitetural e Fase de Desenvolvimento

Este projeto está sendo desenvolvido de forma iterativa, aplicando metodologias ágeis de entrega. 

Nesta fase atual (MVP para o TCC), o foco absoluto está na **consolidação das regras de negócio, modelagem relacional e segurança de permissões**. Por ser uma versão de validação de negócio, algumas decisões arquiteturais foram tomadas:

*   **Autenticação:** Atualmente gerenciada pela estrutura padrão baseada em sessões do Spring Security. A implementação de tokens **JWT** (JSON Web Tokens) para uma abordagem puramente *stateless* está mapeada para o próximo ciclo de refatoração.
*   **Banco de Dados:** A criação e atualização do esquema do banco estão sendo delegadas ao `spring.jpa.hibernate.ddl-auto` (geração automática). O versionamento de banco via **Migrations** (como Flyway ou Liquibase) será integrado na fase de preparação para o ambiente de produção.
*   **Validações:** As regras estão sendo tratadas explicitamente nas classes de `Service` (Domain Logic), sem o uso extensivo de dependências extras de `Annotations` de validação na camada de DTOs neste primeiro momento.

Essa abordagem garante que a lógica fundamental do sistema esteja robusta e testável antes de adicionar complexidade de infraestrutura.

---

## Como Executar o Projeto

### Pré-requisitos
*   Java 17+
*   Node.js (para o React)
*   MariaDB rodando localmente

### Rodando o Backend (Spring Boot)
1. Clone o repositório.
2. Configure as credenciais do banco de dados no arquivo `application.properties`.
3. Execute o projeto via IDE ou Maven.

### Rodando o Frontend (React)
1. Navegue até a pasta do frontend.
2. Execute `npm install` para instalar as dependências.
3. Execute `npm start` (ou `npm run dev`) para iniciar o servidor de desenvolvimento.

