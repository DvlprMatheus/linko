# Linko — Encurtador de links

Linko é um encurtador de links que transforma URLs de destino em links curtos, redireciona os acessos para o endereço original e permite desativar links manualmente ou após o período de validade configurado. O serviço também oferece endpoints HTTP para integração e gerenciamento dos links.

## Funcionalidades

- Criação de links curtos com códigos únicos.
- Redirecionamento para o endereço de destino.
- Desativação manual de links.
- Expiração automática de links ativos.
- Cache Redis para consultas de links ativos.
- Migrações de banco de dados gerenciadas pelo Flyway.
- Respostas de erro HTTP padronizadas.

## Tecnologias

- Java 25
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Flyway
- Redis
- Gradle
- JUnit e Mockito

## Estrutura do projeto

```text
src/
├── main/
│   ├── java/com/dvlprmatheus/linko/
│   │   ├── api/
│   │   │   ├── controller/       # Endpoints REST
│   │   │   ├── dto/              # Objetos de requisição e resposta
│   │   │   └── exception/        # Tratamento de erros da API
│   │   ├── config/
│   │   │   ├── properties/       # Propriedades da aplicação
│   │   │   ├── redis/            # Configuração de cache
│   │   │   └── schedule/         # Tarefas agendadas
│   │   ├── entity/               # Entidades JPA
│   │   ├── repository/           # Acesso a dados
│   │   ├── service/              # Regras de negócio
│   │   └── utils/                # Utilitários
│   └── resources/
│       ├── db/migration/         # Migrações Flyway
│       └── application.yaml      # Configuração padrão
└── test/
    ├── java/                     # Testes automatizados
    └── resources/                # Configuração de testes
```

## Pré-requisitos

- JDK 25.
- PostgreSQL compatível com a migração do projeto.
- Redis.

O repositório inclui um Dev Container com serviços PostgreSQL e Redis. Para usá-lo, abra o projeto no VS Code e selecione **Reopen in Container**.

## Configuração

A aplicação lê as configurações abaixo por variáveis de ambiente. Os valores após `:` são os padrões definidos pelo projeto.

| Variável | Padrão | Descrição |
| --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | `local` | Perfil ativo do Spring. |
| `POSTGRES_URL` | `jdbc:postgresql://localhost:5432/linko_ms` | URL JDBC do PostgreSQL. |
| `POSTGRES_USERNAME` | `postgres` | Usuário do banco de dados. |
| `POSTGRES_PASSWORD` | `postgres` | Senha do banco de dados. |
| `REDIS_HOST` | `localhost` | Host do Redis. |
| `REDIS_PORT` | `6379` | Porta do Redis. |
| `SHORT_APPLICATION_URL` | `http://localhost:8080` | Base usada para formar a URL curta retornada pela API. |
| `SHORT_EXPIRATION` | `P3M` | Período de validade dos links, no formato `Period` ISO-8601. |
| `CACHE_REDIS_TTL` | `PT10M` | Tempo de vida das entradas de cache, no formato `Duration` ISO-8601. |

Exemplo para um ambiente local:

```bash
export POSTGRES_URL=jdbc:postgresql://localhost:5432/linko_ms
export POSTGRES_USERNAME=postgres
export POSTGRES_PASSWORD=postgres
export REDIS_HOST=localhost
export REDIS_PORT=6379
export SHORT_APPLICATION_URL=http://localhost:8080
export SHORT_EXPIRATION=P3M
export CACHE_REDIS_TTL=PT10M
```

O Flyway executa as migrações automaticamente durante a inicialização. A aplicação espera que o banco de dados e o Redis estejam disponíveis.

## Executar a aplicação

Na raiz do projeto, execute:

```bash
./gradlew bootRun
```

Por padrão, a API fica disponível em `http://localhost:8080`.

## Endpoints

### Criar um link curto

`POST /api/v1/links/create`

Requisição:

```http
POST /api/v1/links/create
Content-Type: application/json
```

```json
{
  "destinationUrl": "https://example.com/articles/linko"
}
```

Resposta `200 OK`:

```json
{
  "shortenedUrl": "http://localhost:8080/a1B2c3D4"
}
```

O código apresentado na resposta é gerado pela aplicação e pode variar.

### Acessar um link curto

`GET /{code}`

Uma requisição para o código ativo retorna `301 Moved Permanently`, com o endereço de destino no cabeçalho `Location`.

```bash
curl -i http://localhost:8080/a1B2c3D4
```

### Desativar um link

`DELETE /api/v1/links/{code}`

```bash
curl -i -X DELETE http://localhost:8080/api/v1/links/a1B2c3D4
```

Em caso de sucesso, a API retorna `204 No Content`.

### Erros

Links inexistentes ou inativos retornam `404 Not Found`. As respostas de erro seguem este formato:

```json
{
  "timestamp": "2026-10-24T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "labels_link_not_found",
  "path": "/a1B2c3D4"
}
```

O valor de `timestamp`, `message` e `path` depende da requisição e do erro ocorrido. Erros não tratados retornam `500 Internal Server Error` no mesmo formato.

## Expiração e cache

Uma tarefa agendada é executada diariamente às 06:00 no fuso `America/Sao_Paulo`. Ela busca links ativos criados antes do limite calculado a partir de `SHORT_EXPIRATION` e os desativa.

As consultas aos destinos dos links ativos são armazenadas no Redis. O tempo de vida do cache é configurado por `CACHE_REDIS_TTL`; a desativação de um link remove a entrada correspondente do cache.

## Testes e formatação

Execute todos os testes:

```bash
./gradlew test
```

Execute apenas os testes de uma classe:

```bash
./gradlew test --tests 'com.dvlprmatheus.linko.config.schedule.LinkExpirationSchedulerTest'
```

Verifique a formatação Java:

```bash
./gradlew spotlessCheck
```

Para aplicar a formatação:

```bash
./gradlew spotlessApply
```

## Licença

Este projeto está licenciado sob a licença MIT. Consulte o arquivo [LICENSE](./LICENSE) para mais informações.
