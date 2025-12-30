# 🎫 CRUD Cupom API

Sistema de gerenciamento de cupons de desconto desenvolvido com **Spring Boot 3.2.11**, Java 17 e H2 Database.

## 📋 Requisitos Atendidos

✅ **Spring Boot 3.2.11** com Java 17  
✅ **CRUD completo** de cupons de desconto  
✅ **Regras de negócio** encapsuladas em objetos de domínio  
✅ **H2 Database** em memória  
✅ **80% de cobertura** de testes (JaCoCo)  
✅ **Docker** e Docker Compose  
✅ **Swagger** para documentação da API  
✅ **Análise estática** de código (Checkstyle, PMD, SpotBugs)  
✅ **Soft delete** - não perde informações  
✅ **Testes unitários e de integração** com JUnit 5, Mockito e AssertJ

## 🎯 Regras de Negócio Implementadas

### Create (Criar Cupom)
- ✅ Campos obrigatórios: `code`, `description`, `discountValue`, `expirationDate`
- ✅ Código alfanumérico de 6 caracteres
- ✅ **Remove caracteres especiais** automaticamente antes de salvar
- ✅ Valor de desconto mínimo: **0.5** (sem máximo)
- ✅ Data de expiração **não pode ser no passado**
- ✅ Pode ser criado como **já publicado**
- ✅ **Código único** - não permite duplicados

### Delete (Deletar Cupom)
- ✅ **Soft delete** - mantém todas as informações no banco
- ✅ Não permite deletar cupom já deletado
- ✅ Marca campo `deleted` como `true` e registra `deletedAt`

### Update (Atualizar Cupom)
- ✅ Não permite atualizar cupom deletado
- ✅ Valida novos valores antes de atualizar

### Publish/Unpublish (Publicar/Despublicar)
- ✅ Não permite publicar cupom deletado
- ✅ Controle de visibilidade do cupom

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.2.11**
    - Spring Web
    - Spring Data JPA
    - Spring Validation
- **H2 Database** (em memória)
- **Lombok**
- **Swagger/OpenAPI** 3.0
- **Maven**
- **Docker & Docker Compose**

### Testes e Qualidade
- **JUnit 5** - Framework de testes moderno
- **Mockito 5.8.0** - Mocking para testes unitários
- **AssertJ** - Assertions fluentes
- **JaCoCo** - Cobertura de código (meta: 80%)
- **Checkstyle** - Verificação de estilo
- **PMD** - Análise de código
- **SpotBugs** - Detecção de bugs
- **Testes de Integração** - Testes end-to-end com MockMvc

## 📂 Estrutura do Projeto

```
crud-cupom/
├── src/
│   ├── main/
│   │   ├── java/com/cupom/api/
│   │   │   ├── controller/       # REST Controllers
│   │   │   ├── service/          # Lógica de negócio
│   │   │   ├── repository/       # Acesso a dados (JPA)
│   │   │   ├── entity/           # Entidades de domínio
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── exception/        # Exceções customizadas
│   │   │   └── CrudCupomApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/com/cupom/api/
│       │   ├── controller/
│       │   │   └── CupomControllerIntegrationTest.java  # 20+ testes de integração
│       │   ├── entity/
│       │   │   └── CupomEntityTest.java  # Testes da entidade
│       │   ├── exception/
│       │   │   └── ExceptionsTest.java  # Testes das exceções
│       │   └── service/
│       │       └── CupomServiceTest.java  # 15+ testes unitários
│       └── resources/
│           └── application-test.properties
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── checkstyle.xml
├── pmd-ruleset.xml
├── QUICKSTART.md
└── README.md
```

## 🔧 Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.6+
- Docker Desktop (opcional)

### Opção 1: Localmente com Maven

```bash
# Executar testes
mvn clean test

# Executar aplicação
mvn spring-boot:run

# Acessar:
# - API: http://localhost:8080
# - Swagger: http://localhost:8080/swagger-ui.html
# - H2 Console: http://localhost:8080/h2-console
```

### Opção 2: Docker

```bash
# Build e executar
docker-compose up --build

# Acessar:
# - API: http://localhost:8080
# - Swagger: http://localhost:8080/swagger-ui.html

# Parar
docker-compose down
```

### Acesso ao H2 Console

```
URL: http://localhost:8080/h2-console

JDBC URL: jdbc:h2:mem:cupomdb
Username: samuelcupom
Password: 123
```

## 📡 Endpoints da API

### Criar Cupom
```http
POST /api/cupons
Content-Type: application/json

{
  "code": "ABC-123",         // Será normalizado para "ABC123"
  "description": "Desconto de 10%",
  "discountValue": 10.00,    // Mínimo 0.5
  "expirationDate": "2025-12-31",
  "published": false
}
```

### Listar Cupons Ativos
```http
GET /api/cupons
```

### Buscar por ID
```http
GET /api/cupons/{id}
```

### Buscar por Código
```http
GET /api/cupons/code/{code}
```

### Atualizar Cupom
```http
PUT /api/cupons/{id}
Content-Type: application/json

{
  "description": "Nova descrição",
  "discountValue": 15.00,
  "expirationDate": "2026-01-31",
  "published": true
}
```

### Deletar Cupom (Soft Delete)
```http
DELETE /api/cupons/{id}
```

### Publicar Cupom
```http
POST /api/cupons/{id}/publish
```

### Despublicar Cupom
```http
POST /api/cupons/{id}/unpublish
```

## 🧪 Executar Testes

### Testes Unitários
```bash
# Executar todos os testes
mvn clean test

# Executar apenas testes unitários do Service
mvn test -Dtest=CupomServiceTest

# Executar apenas testes da Entity
mvn test -Dtest=CupomEntityTest
```

### Testes de Integração
```bash
# Executar testes de integração
mvn test -Dtest=CupomControllerIntegrationTest
```

### Cobertura de Testes
```bash
# Testes com relatório de cobertura
mvn clean test jacoco:report

# Ver cobertura (abre no navegador)
open target/site/jacoco/index.html

# Verificar se atingiu 80% de cobertura
mvn jacoco:check
```

### Resumo dos Testes

| Categoria | Arquivo | Testes | Descrição |
|-----------|---------|--------|-----------|
| Service | CupomServiceTest | 15+ | Testes unitários com mocks |
| Entity | CupomEntityTest | 25+ | Testes das regras de negócio |
| Controller | CupomControllerIntegrationTest | 20+ | Testes de integração end-to-end |
| Exceptions | ExceptionsTest | 6+ | Testes do tratamento de erros |

**Total: 65+ testes** garantindo a qualidade e cobertura do código.

## 🔍 Análise Estática

```bash
# Checkstyle (estilo de código)
mvn checkstyle:check

# PMD (análise de código)
mvn pmd:check

# SpotBugs (detecção de bugs)
mvn spotbugs:check

# Executar todas as análises + testes
mvn clean verify
```

### Relatórios Gerados

Após executar `mvn clean verify`, os seguintes relatórios estarão disponíveis:

- **JaCoCo Coverage**: `target/site/jacoco/index.html`
- **Checkstyle**: `target/site/checkstyle.html`
- **PMD**: `target/site/pmd.html`
- **SpotBugs**: `target/spotbugsXml.xml`

## 💡 Exemplos de Uso

### Criar cupom com caracteres especiais no código

O código será automaticamente normalizado:

```json
POST /api/cupons
{
  "code": "AB@C-12#3!",  // Input
  "description": "Desconto especial",
  "discountValue": 5.0,
  "expirationDate": "2025-12-31"
}

// Resposta:
{
  "id": 1,
  "code": "ABC123",  // ✅ Normalizado (6 caracteres alfanuméricos)
  "description": "Desconto especial",
  "discountValue": 5.0,
  "expirationDate": "2025-12-31",
  "published": false,
  "deleted": false,
  "active": true,
  "expired": false
}
```

### Tentativa de criar com data passada

```json
POST /api/cupons
{
  "code": "TESTE1",
  "description": "Teste",
  "discountValue": 5.0,
  "expirationDate": "2024-01-01"  // ❌ Data no passado
}

// Resposta: 400 Bad Request
{
  "timestamp": "2024-12-30T00:00:00",
  "status": 400,
  "error": "Invalid Cupom",
  "message": "Data de expiração não pode ser no passado. Data fornecida: 2024-01-01"
}
```

### Tentativa de criar com código duplicado

```json
POST /api/cupons
{
  "code": "ABC123",  // ❌ Código já existe
  "description": "Teste",
  "discountValue": 5.0,
  "expirationDate": "2025-12-31"
}

// Resposta: 409 Conflict
{
  "timestamp": "2024-12-30T00:00:00",
  "status": 409,
  "error": "Duplicate Code",
  "message": "Já existe um cupom ativo com o código: ABC123"
}
```

### Soft Delete

```bash
# Deletar cupom
DELETE /api/cupons/1
# Resposta: 204 No Content

# Buscar cupom deletado (ainda retorna os dados)
GET /api/cupons/1
# Resposta: 200 OK
{
  "id": 1,
  "code": "ABC123",
  "deleted": true,  // ✅ Soft delete
  "deletedAt": "2024-12-30T15:30:00",
  "active": false
}

# Cupons deletados NÃO aparecem na listagem
GET /api/cupons
# Resposta: [] (vazio)
```

### Teste com curl

```bash
# Criar cupom
curl -X POST http://localhost:8080/api/cupons \
  -H "Content-Type: application/json" \
  -d '{
    "code": "NATAL25",
    "description": "Desconto de Natal",
    "discountValue": 25.0,
    "expirationDate": "2025-12-25",
    "published": true
  }'

# Listar cupons
curl http://localhost:8080/api/cupons

# Buscar por código
curl http://localhost:8080/api/cupons/code/NATAL25
```

## 📊 Métricas de Qualidade

| Métrica | Configuração | Status |
|---------|--------------|--------|
| Cobertura de Testes | Mínimo 80% | ✅ Configurado |
| Complexidade Ciclomática | Máximo 15 | ✅ Configurado |
| Tamanho de Método | Máximo 150 linhas | ✅ Configurado |
| Parâmetros por Método | Máximo 7 | ✅ Configurado |
| Tamanho de Arquivo | Máximo 500 linhas | ✅ Configurado |

## 🏗️ Arquitetura

### Camada de Domínio (Entity)
- `Cupom.java` - Entidade com **regras de negócio encapsuladas**
    - `normalizeCode()` - Remove caracteres especiais (método estático)
    - `validateExpirationDate()` - Valida data (método estático)
    - `validateDiscountValue()` - Valida valor (método estático)
    - `softDelete()` - Soft delete (método de instância)
    - `isActive()`, `isExpired()` - Métodos auxiliares

### Camada de Serviço (Service)
- `CupomService.java` - Lógica de negócio
    - Orquestra validações
    - Gerencia transações com `@Transactional`
    - Converte entre Entity e DTO
    - Logging de operações

### Camada de Apresentação (Controller)
- `CupomController.java` - REST API
    - Endpoints HTTP com validação
    - Documentação Swagger/OpenAPI
    - Tratamento de exceções via `@RestControllerAdvice`

### Camada de Dados (Repository)
- `CupomRepository.java` - Acesso a dados
    - Queries JPQL customizadas
    - Soft delete aware
    - Métodos específicos para buscar cupons ativos

### Tratamento de Exceções
- `GlobalExceptionHandler.java` - Handler centralizado
    - `CupomNotFoundException` → 404 Not Found
    - `CupomAlreadyDeletedException` → 400 Bad Request
    - `InvalidCupomException` → 400 Bad Request
    - `DuplicateCupomCodeException` → 409 Conflict
    - `MethodArgumentNotValidException` → 400 Bad Request (validação)

## 🎓 Decisões de Design

### 1. Regras de Negócio no Domínio
As regras estão **encapsuladas** na entidade `Cupom`:
- Validações são **métodos estáticos** reutilizáveis
- Comportamentos (soft delete, publish) são **métodos de instância**
- Garante consistência e facilita testes
- Segue princípios de **Domain-Driven Design (DDD)**

### 2. Soft Delete
- Campo `deleted` boolean
- Campo `deletedAt` timestamp
- Queries filtram automaticamente deletados
- **Preserva histórico** para auditoria
- Possibilita recuperação de dados

### 3. Normalização de Código
- Remove caracteres especiais **antes de salvar**
- Garante **exatamente 6 caracteres**
- Converte para maiúsculas
- Valida no momento da criação
- Exemplo: `"AB@C-12#3!"` → `"ABC123"`

### 4. Separação de DTOs
- `CupomRequest` - Para entrada (criação/atualização)
- `CupomResponse` - Para saída (inclui campos calculados)
- Evita exposição de detalhes internos
- Facilita versionamento da API

### 5. Testes Abrangentes
- **Testes Unitários**: Service e Entity com mocks
- **Testes de Integração**: Controller com banco H2
- **Cobertura > 80%**: Garantida pelo JaCoCo
- **Assertions Fluentes**: AssertJ para legibilidade

## 📝 Documentação da API

### Swagger UI
Acesse o Swagger UI em: **http://localhost:8080/swagger-ui.html**

### OpenAPI JSON
JSON da API em: **http://localhost:8080/api-docs**

### Postman Collection
Uma collection do Postman pode ser gerada importando o JSON do OpenAPI.

## 🐳 Docker

### Build da Imagem
```bash
docker build -t crud-cupom:1.0.0 .
```

### Executar Container
```bash
docker run -p 8080:8080 crud-cupom:1.0.0
```

### Docker Compose
```bash
# Subir aplicação
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar aplicação
docker-compose down
```

### Características do Dockerfile
- **Multi-stage build** para otimizar tamanho
- **Usuário não-root** para segurança
- **Healthcheck** configurado
- **Otimizações JVM** para containers
- Imagem final baseada em **Alpine Linux**

## 🚀 Deploy

### Variáveis de Ambiente

Para produção, configure as seguintes variáveis:

```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/cupomdb
SPRING_DATASOURCE_USERNAME=usuario
SPRING_DATASOURCE_PASSWORD=senha
```

### Banco de Dados em Produção

Para usar PostgreSQL em produção, adicione ao `pom.xml`:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

E crie um `application-prod.properties`:

```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
```

## 👤 Autor

**Samuel Dantas**
- Email: samueldantasbarbosa@hotmail.com

---

## 🔗 Links Úteis

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [H2 Database Documentation](https://www.h2database.com/html/main.html)
- [Swagger/OpenAPI Specification](https://swagger.io/specification/)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [AssertJ Documentation](https://assertj.github.io/doc/)

---

**Projeto desenvolvido seguindo as melhores práticas de:**
- ✅ Clean Code
- ✅ SOLID Principles
- ✅ Test-Driven Development (TDD)
- ✅ Domain-Driven Design (DDD)
- ✅ RESTful API Design
- ✅ Continuous Integration

---
