# 🎫 CRUD Cupom API - Versão em Português-BR

Sistema de gerenciamento de cupons de desconto desenvolvido com **Spring Boot 3.2.11**, Java 17 e H2 Database.

## ✨ Diferenças da Versão em Português-BR

Esta versão foi completamente refatorada para utilizar nomenclatura em português brasileiro:

### Mudanças Principais

**Classes Renomeadas:**
- `CrudCupomApplication` → `AplicacaoCrudCupom`
- `CupomService` → `CupomServico`
- `CupomRepository` → `CupomRepositorio`
- `CupomController` → `CupomControlador`
- `CupomRequest` → `CupomRequisicao`
- `CupomResponse` → `CupomResposta`

**Métodos Traduzidos:**
- `createCupom()` → `criarCupom()`
- `getAllActiveCupons()` → `obterTodosCuponsAtivos()`
- `getCupomById()` → `obterCupomPorId()`
- `updateCupom()` → `atualizarCupom()`
- `deleteCupom()` → `excluirCupom()`
- `publishCupom()` → `publicarCupom()`

**Propriedades Traduzidas:**
- `code` → `codigo`
- `description` → `descricao`
- `discountValue` → `valorDesconto`
- `expirationDate` → `dataExpiracao`
- `published` → `publicado`
- `deleted` → `excluido`
- `active` → `ativo`
- `expired` → `expirado`
- `createdAt` → `criadoEm`
- `updatedAt` → `atualizadoEm`

**Exceções Traduzidas:**
- `CupomNotFoundException` → `CupomNaoEncontradoException`
- `CupomAlreadyDeletedException` → `CupomJaExcluidoException`
- `InvalidCupomException` → `CupomInvalidoException`
- `DuplicateCupomCodeException` → `CodigoCupomDuplicadoException`
- `GlobalExceptionHandler` → `TratadorExcecaoGlobal`

**Código Limpo:**
- ✅ Comentários excessivos removidos
- ✅ Apenas JavaDoc essencial mantido
- ✅ Código mais limpo e profissional

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
✅ **Nomenclatura 100% em português-BR**

## 🎯 Regras de Negócio

### Criar Cupom
- Campos obrigatórios: `codigo`, `descricao`, `valorDesconto`, `dataExpiracao`
- Código alfanumérico de 6 caracteres
- Remove caracteres especiais automaticamente
- Valor de desconto mínimo: **0.5**
- Data de expiração não pode ser no passado
- Código único

### Deletar Cupom
- **Soft delete** - mantém todas as informações
- Não permite deletar cupom já deletado
- Marca campo `excluido` como `true`

### Atualizar Cupom
- Não permite atualizar cupom deletado
- Valida novos valores antes de atualizar

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.2.11**
- **H2 Database**
- **Lombok**
- **Swagger/OpenAPI 3.0**
- **Maven**
- **Docker**

## 📂 Estrutura do Projeto

```
crud-cupom/
├── src/
│   ├── main/
│   │   ├── java/com/cupom/api/
│   │   │   ├── controlador/        # REST Controllers
│   │   │   ├── servico/            # Lógica de negócio
│   │   │   ├── repositorio/        # Acesso a dados (JPA)
│   │   │   ├── dominio/            # Entidades de domínio
│   │   │   ├── dto/                # Data Transfer Objects
│   │   │   ├── excecao/            # Exceções customizadas
│   │   │   └── AplicacaoCrudCupom.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/cupom/api/
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
mvn clean test
mvn spring-boot:run
```

Acessar:
- **API**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console

### Opção 2: Docker

```bash
docker-compose up --build
```

### H2 Console

```
URL: jdbc:h2:mem:cupomdb
Username: samuelcupom
Password: 123
```

## 📡 Endpoints da API

### Criar Cupom
```http
POST /api/cupons
Content-Type: application/json

{
  "codigo": "ABC-123",
  "descricao": "Desconto de 10%",
  "valorDesconto": 10.00,
  "dataExpiracao": "2025-12-31",
  "publicado": false
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
GET /api/cupons/codigo/{codigo}
```

### Atualizar Cupom
```http
PUT /api/cupons/{id}
```

### Deletar Cupom
```http
DELETE /api/cupons/{id}
```

### Publicar/Despublicar
```http
POST /api/cupons/{id}/publicar
POST /api/cupons/{id}/despublicar
```

## 🧪 Testes

```bash
# Executar testes
mvn clean test

# Ver cobertura
mvn test jacoco:report
open target/site/jacoco/index.html

# Análise estática
mvn clean verify
```

## 📊 Métricas de Qualidade

| Métrica | Meta | Status |
|---------|------|--------|
| Cobertura de Testes | 80% | ✅ |
| Complexidade Ciclomática | Max 15 | ✅ |
| Tamanho de Método | Max 150 linhas | ✅ |

## 👤 Autor

**Samuel Dantas**
- Email: samueldantasbarbosa@hotmail.com

---

**Projeto refatorado seguindo:**
- ✅ Clean Code
- ✅ SOLID Principles
- ✅ Nomenclatura em Português-BR
- ✅ Código sem comentários excessivos
- ✅ Domain-Driven Design (DDD)
