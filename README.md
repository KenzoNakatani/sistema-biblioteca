# 📚 Sistema de Biblioteca com Autenticação

API REST para gerenciamento de uma biblioteca — livros, usuários, empréstimos e reservas — com autenticação e autorização reais via **Spring Security + JWT**, implementadas do zero.

Este projeto foi construído como exercício prático para fixar conceitos de segurança em APIs Spring Boot: login stateless com token, proteção de rotas por perfil de usuário (role-based access), e regras de negócio aplicadas na camada de serviço.

---

## ✨ Funcionalidades

- **Autenticação com JWT**: registro e login, com geração e validação de token assinado.
- **Autorização por perfil (Role)**: rotas administrativas (ex: cadastro de livros) restritas a usuários `ADMIN`.
- **Gestão de empréstimos**: limite de 3 empréstimos ativos por usuário.
- **Cálculo automático de multa**: R$ 2,00 por dia de atraso na devolução.
- **Controle de estoque**: quantidade de exemplares disponíveis atualizada automaticamente a cada empréstimo/devolução.

---

## 🛠️ Tecnologias

- **Java 17+**
- **Spring Boot 3**
- **Spring Security** (autenticação/autorização)
- **JJWT** (geração e validação de tokens JWT)
- **Spring Data JPA** + **Hibernate**
- **PostgreSQL**
- **Maven**

---

## 🏗️ Arquitetura

```
src/main/java/com/example/demo
 ├── model        → Entidades (Usuario, Livro, Emprestimo, Reserva, Role, StatusEmprestimo, StatusReserva)
 ├── repository    → Interfaces JPA para acesso ao banco
 ├── security      → JwtService, UsuarioDetailsService, JwtAuthenticationFilter, SecurityConfig
 ├── service       → Regras de negócio (EmprestimoService)
 ├── controller    → Endpoints REST (AuthController, LivroController, EmprestimoController)
 ├── dto           → Objetos de entrada/saída (RegistroRequest, LoginRequest, TokenResponse)
 └── exception     → Exceções customizadas (RegraNegocioException)
```

**Fluxo de autenticação:**

```
Login → AuthenticationManager valida credenciais → JwtService gera o token
                                ↓
Requisições futuras → JwtAuthenticationFilter valida o token
                     → UsuarioDetailsService busca o usuário
                     → SecurityConfig libera ou bloqueia o acesso
```

---

## 🚀 Como rodar o projeto

### Pré-requisitos
- Java 17 ou superior
- PostgreSQL instalado e em execução
- Maven (ou use o `mvnw`/`mvnw.cmd` incluso no projeto)

### 1. Clone o repositório
```bash
git clone <url-do-repositorio>
cd api-biblioteca/demo
```

### 2. Crie o banco de dados
```sql
CREATE DATABASE biblioteca;
```

### 3. Configure as credenciais
Edite `src/main/resources/application.properties` com seus dados de conexão:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/biblioteca
spring.datasource.username=postgres
spring.datasource.password=sua_senha_aqui
```

### 4. Rode a aplicação
```bash
# Linux/Mac
./mvnw spring-boot:run

# Windows
.\mvnw.cmd spring-boot:run
```

A API sobe em `http://localhost:8080`.

---

## 📡 Endpoints principais

### Autenticação (rotas públicas)

| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/auth/registrar` | Cria um novo usuário |
| `POST` | `/auth/login` | Autentica e retorna um token JWT |

**Exemplo — Registro:**
```json
POST /auth/registrar
{
  "nome": "Ana",
  "email": "ana@teste.com",
  "senha": "123456"
}
```

**Exemplo — Login:**
```json
POST /auth/login
{
  "email": "ana@teste.com",
  "senha": "123456"
}
```
Resposta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

> A partir daqui, toda rota abaixo exige o header `Authorization: Bearer <token>`.

### Livros

| Método | Rota | Acesso | Descrição |
|--------|------|--------|-----------|
| `GET` | `/livros` | Autenticado | Lista todos os livros |
| `POST` | `/livros` | **ADMIN** | Cadastra um novo livro |

### Empréstimos

| Método | Rota | Acesso | Descrição |
|--------|------|--------|-----------|
| `POST` | `/emprestimos?livroId={id}` | Autenticado | Realiza um empréstimo |
| `PUT` | `/emprestimos/{id}/devolver` | Autenticado | Registra a devolução (calcula multa se atrasado) |

---

## 📏 Regras de negócio

- Um usuário pode ter no máximo **3 empréstimos ativos** simultaneamente.
- O prazo padrão de devolução é de **14 dias** a partir do empréstimo.
- Devoluções em atraso geram multa de **R$ 2,00 por dia**.
- Só é possível emprestar um livro se houver **exemplares disponíveis** em estoque.

---

## 🧪 Testado com

Fluxo completo validado via Postman: registro → login → cadastro de livro (com verificação de permissão ADMIN) → empréstimo → limite de empréstimos → devolução no prazo → devolução com multa.

---

## 📌 Possíveis evoluções futuras

- [ ] Endpoint completo para `Reserva` (entidade já modelada)
- [ ] Refresh token
- [ ] Documentação automática com Swagger/OpenAPI
- [ ] Testes unitários e de integração
- [ ] Deploy em ambiente cloud

---

## 👤 Autor

Desenvolvido por **Kenzo Nakatani** como exercício de estudo em Java/Spring Boot, com foco em autenticação e autorização com Spring Security + JWT.
