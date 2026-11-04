# TaskFlow API

API REST para gerenciamento de tarefas, feita com **Spring Boot** e **PostgreSQL**. Faz parte de um projeto full-stack para praticar testes automatizados, CI/CD, Docker e observabilidade.

## Stack

| Tecnologia | Uso |
|---|---|
| Java 21 | Linguagem |
| Spring Boot 4.1 | Framework (Web MVC, Validation, Actuator) |
| Spring Data JPA + Hibernate | Acesso ao banco |
| PostgreSQL 18 | Banco de dados (via Docker) |
| Flyway | Versionamento do schema |
| springdoc-openapi | Documentação Swagger / OpenAPI |
| JUnit + Testcontainers | Testes |
| GitHub Actions | CI |

## Estrutura

```
src/main/java/br/com/TaskFlow/
├── config/        # CORS, OpenAPI e tratamento global de erros
├── controller/    # Endpoints REST
├── dto/           # TaskRequest / TaskResponse
├── exeption/      # Exceções de domínio
├── repository/    # Spring Data JPA
├── service/       # Regras de negócio
└── task/          # Entity Task e enum TaskStatus

src/main/resources/
├── application.yml
└── db/migration/  # Migrations do Flyway
```

## Pré-requisitos

- Java 21
- Docker e Docker Compose

## Configuração

O banco e a API leem as credenciais de variáveis de ambiente. Crie um arquivo `.env` a partir do exemplo:

```bash
cp .env.example .env
```

```env
POSTGRES_DB=tarefas
POSTGRES_USER=postgres
POSTGRES_PASSWORD=troque-por-uma-senha-forte
```

> O `.env` **não deve ser commitado**. Confirme que ele está no `.gitignore`. O `.env.example` (sem senha real) é o que vai pro repositório.

| Variável | Padrão | Descrição |
|---|---|---|
| `POSTGRES_DB` | `tarefas` | Nome do banco |
| `POSTGRES_USER` | `postgres` | Usuário do banco |
| `POSTGRES_PASSWORD` | *(obrigatória)* | Senha do banco |

O Postgres do Docker fica exposto apenas em `127.0.0.1:5433`.

## Rodando localmente

```bash
# 1. Sobe o banco
docker compose up -d

# 2. Exporta as variáveis do .env para o terminal
set -a && source .env && set +a

# 3. Roda a API (na pasta onde está o pom.xml)
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`. As migrations do Flyway rodam automaticamente na inicialização.

> **Importante:** o Spring Boot não lê o `.env` sozinho (quem lê é o Docker Compose). Se for rodar pela IDE, configure as variáveis em *Run Configuration → Environment variables*.

## Documentação (Swagger)

Com a API rodando:

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI (JSON) | http://localhost:8080/v3/api-docs |

No Swagger UI dá para testar todos os endpoints direto pelo navegador.

## Endpoints

| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| GET | `/tasks` | Lista as tarefas (mais recentes primeiro) | 200 |
| GET | `/tasks/{id}` | Busca uma tarefa | 200 |
| POST | `/tasks` | Cria uma tarefa | 201 |
| PUT | `/tasks/{id}` | Atualiza uma tarefa | 200 |
| DELETE | `/tasks/{id}` | Remove uma tarefa | 204 |

Health check: `GET /actuator/health`

### Corpo da requisição (POST / PUT)

```json
{
  "title": "Estudar Spring Boot",
  "description": "Fazer o CRUD completo com testes",
  "status": "TODO"
}
```

| Campo | Obrigatório | Regras |
|---|---|---|
| `title` | Sim | Não pode ser vazio, até 120 caracteres |
| `description` | Não | Até 2000 caracteres |
| `status` | Não | `TODO`, `DOING` ou `DONE`. Se omitido: `TODO` na criação; na atualização mantém o atual |

### Exemplos

```bash
# Criar
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Primeira tarefa"}'

# Listar
curl http://localhost:8080/tasks

# Atualizar
curl -X PUT http://localhost:8080/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Primeira tarefa","status":"DONE"}'

# Remover
curl -X DELETE http://localhost:8080/tasks/1
```

### Resposta de sucesso

```json
{
  "id": 1,
  "title": "Estudar Spring Boot",
  "description": "Fazer o CRUD completo com testes",
  "status": "TODO",
  "createdAt": "2026-11-04T18:30:00Z"
}
```

## Erros

Os erros seguem o padrão `ProblemDetail` (RFC 9457).

**404, tarefa não encontrada**

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Tarefa não encontrada: 99",
  "instance": "/tasks/99"
}
```

**400, dados inválidos**

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Dados inválidos",
  "instance": "/tasks",
  "errors": {
    "title": "O título é obrigatório"
  }
}
```

## CORS

Origens liberadas por padrão: `http://localhost:3000` e `http://localhost:5173`. Para alterar, edite `app.cors.allowed-origins` no `application.yml`.

## Testes

```bash
./mvnw test      # só os testes
./mvnw verify    # testes + empacotamento
```

## CI

O workflow `.github/workflows/backend-ci.yml` roda `./mvnw -B verify` a cada push na `main` e a cada pull request que altere o back, com um Postgres 18 como serviço.

Configure o secret no GitHub: **Settings → Secrets and variables → Actions → New repository secret**

| Secret | Descrição |
|---|---|
| `POSTGRES_PASSWORD` | Senha do Postgres usada no CI |

## Próximos passos

- [ ] Dockerfile da API e `docker-compose` completo (front + back + banco)
- [ ] Observabilidade (métricas com Prometheus e Grafana)
- [ ] CD: publicar a imagem e fazer o deploy
