# 🎫 CRUD Cupom API

Sistema de gerenciamento de cupons de desconto desenvolvido com **Spring Boot 3.5.9**, Java 17 e H2 Database.

## 📋 Requisitos Atendidos

✅ **Spring Boot 3.5.9** com Java 17
✅ **CRUD completo** de cupons de desconto  
✅ **Regras de negócio** encapsuladas em objetos de domínio  
✅ **H2 Database** em memória  
✅ **80% de cobertura** de testes (JaCoCo)  
✅ **Docker** e Docker Compose  
✅ **Swagger** para documentação da API  
✅ **Análise estática** de código (Checkstyle, PMD, SpotBugs)  
✅ **Soft delete** - não perde informações  
✅ **Testes unitários** com JUnit 5, Mockito e AssertJ  

## 🎯 Regras de Negócio Implementadas

### Create (Criar Cupom)
- ✅ Campos obrigatórios: `code`, `description`, `discountValue`, `expirationDate`
- ✅ Código alfanumérico de 6 caracteres
- ✅ **Remove caracteres especiais** automaticamente antes de salvar
- ✅ Valor de desconto mínimo: **0.5** (sem máximo)
- ✅ Data de expiração **não pode ser no passado**
- ✅ Pode ser criado como **já publicado**

### Delete (Deletar Cupom)
- ✅ **Soft delete** - mantém todas as informações no banco
- ✅ Não permite deletar cupom já deletado
- ✅ Marca campo `deleted` como `true` e registra `deletedAt`

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.5.9**
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
- **Mockito 5.8.0** - Mocking
- **AssertJ** - Assertions fluentes
- **JaCoCo** - Cobertura de código (meta: 80%)
- **Checkstyle** - Verificação de estilo
- **PMD** - Análise de código
- **SpotBugs** - Detecção de bugs

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
│       │   └── service/
│       │       └── CupomServiceTest.java  # 15+ testes
│       └── resources/
│           └── application-test.properties
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── checkstyle.xml
├── pmd-ruleset.xml
└── README.md
```

## 🔧 Como Executar

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
```

### Acesso ao H2 Console

```
URL: jdbc:h2:mem:cupomdb
Username: sa
Password: (deixar em branco)
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

```bash
# Testes unitários
mvn clean test

# Testes com relatório de cobertura
mvn test jacoco:report

# Ver cobertura (abre no navegador)
open target/site/jacoco/index.html

# Verificar se atingiu 80% de cobertura
mvn jacoco:check
```

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
  "message": "Data de expiração não pode ser no passado"
}
```

### Soft Delete

```bash
DELETE /api/cupons/1
# Resposta: 204 No Content

GET /api/cupons/1
# Resposta: 200 OK
{
  "id": 1,
  "deleted": true,  // ✅ Soft delete
  "deletedAt": "2024-12-30T00:00:00",
  "active": false
}
```

## 📊 Métricas de Qualidade

| Métrica | Configuração | Status |
|---------|--------------|--------|
| Cobertura de Testes | Mínimo 80% | ✅ Configurado |
| Complexidade Ciclomática | Máximo 15 | ✅ Configurado |
| Tamanho de Método | Máximo 150 linhas | ✅ Configurado |
| Parâmetros por Método | Máximo 7 | ✅ Configurado |

## 🏗️ Arquitetura

### Camada de Domínio (Entity)
- `Cupom.java` - Entidade com **regras de negócio encapsuladas**
  - `normalizeCode()` - Remove caracteres especiais
  - `validateExpirationDate()` - Valida data
  - `validateDiscountValue()` - Valida valor
  - `softDelete()` - Soft delete
  - `isActive()`, `isExpired()` - Métodos auxiliares

### Camada de Serviço (Service)
- `CupomService.java` - Lógica de negócio
  - Orquestra validações
  - Gerencia transações
  - Converte entre Entity e DTO

### Camada de Apresentação (Controller)
- `CupomController.java` - REST API
  - Endpoints HTTP
  - Validação de entrada
  - Documentação Swagger

### Camada de Dados (Repository)
- `CupomRepository.java` - Acesso a dados
  - Queries customizadas
  - Soft delete aware

## 🎓 Decisões de Design

### 1. Regras de Negócio no Domínio
As regras estão **encapsuladas** na entidade `Cupom`:
- Validações são **métodos estáticos** reutilizáveis
- Comportamentos (soft delete, publish) são **métodos de instância**
- Garante consistência e facilita testes

### 2. Soft Delete
- Campo `deleted` boolean
- Campo `deletedAt` timestamp
- Queries filtram automaticamente deletados
- **Preserva histórico** para auditoria

### 3. Normalização de Código
- Remove caracteres especiais **antes de salvar**
- Garante **exatamente 6 caracteres**
- Valida no momento da criação

## 📝 Documentação da API

Acesse o Swagger UI em: **http://localhost:8080/swagger-ui.html**

Ou o JSON da API em: **http://localhost:8080/api-docs**

## 👤 Desenvolvedor

**Projeto desenvolvido seguindo as melhores práticas de:**
- ✅ Clean Code
- ✅ SOLID Principles
- ✅ Test-Driven Development (TDD)
- ✅ Domain-Driven Design (DDD)

---
