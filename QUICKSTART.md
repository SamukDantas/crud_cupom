# 🚀 Guia de Início Rápido - CRUD Cupom

## ⚡ Executar Rapidamente

### Opção 1: Maven (Local)
```bash
# 1. Executar aplicação
mvn spring-boot:run

# 2. Acessar
# - API: http://localhost:8080
# - Swagger: http://localhost:8080/swagger-ui.html
# - H2 Console: http://localhost:8080/h2-console
```

### Opção 2: Docker
```bash
# 1. Build e executar
docker-compose up --build

# 2. Acessar
# - API: http://localhost:8080
# - Swagger: http://localhost:8080/swagger-ui.html
```

## 🧪 Executar Testes

```bash
# Testes unitários
mvn clean test

# Testes + cobertura (80%)
mvn test jacoco:report

# Ver relatório
open target/site/jacoco/index.html
```

## 🔍 Análise Estática

```bash
# Executar todas as verificações
mvn clean verify

# Individual
mvn checkstyle:check    # Estilo de código
mvn pmd:check           # Análise de código
mvn spotbugs:check      # Detecção de bugs
```

## 📋 Teste Manual da API

### 1. Criar Cupom
```bash
curl -X POST http://localhost:8080/api/cupons \
  -H "Content-Type: application/json" \
  -d '{
    "code": "ABC-123",
    "description": "Desconto de 10%",
    "discountValue": 10.0,
    "expirationDate": "2025-12-31",
    "published": true
  }'
```

### 2. Listar Cupons
```bash
curl http://localhost:8080/api/cupons
```

### 3. Buscar por ID
```bash
curl http://localhost:8080/api/cupons/1
```

### 4. Buscar por Código
```bash
curl http://localhost:8080/api/cupons/code/ABC123
```

### 5. Atualizar Cupom
```bash
curl -X PUT http://localhost:8080/api/cupons/1 \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Novo desconto de 15%",
    "discountValue": 15.0
  }'
```

### 6. Deletar Cupom (Soft Delete)
```bash
curl -X DELETE http://localhost:8080/api/cupons/1
```

## 📊 Verificar Cobertura

```bash
# Executar testes e gerar relatório
mvn clean test jacoco:report

# Verificar se atingiu 80%
mvn jacoco:check

# Relatórios disponíveis em:
# - target/site/jacoco/index.html (Cobertura)
# - target/site/checkstyle.html (Checkstyle)
# - target/site/pmd.html (PMD)
```

## 🎯 Comandos Essenciais

```bash
# Compilar projeto
mvn clean install

# Executar aplicação
mvn spring-boot:run

# Executar testes
mvn test

# Análise completa
mvn clean verify

# Docker
docker-compose up --build
docker-compose down
```

## 📝 Regras de Negócio Testadas

✅ Código normalizado (remove caracteres especiais)
✅ Validação de data de expiração (não pode ser passado)
✅ Validação de valor de desconto (mínimo 0.5)
✅ Soft delete (não perde dados)
✅ Não deletar cupom já deletado
✅ Código único (não permite duplicados)

## 🔧 H2 Console

```
URL: http://localhost:8080/h2-console

JDBC URL: jdbc:h2:mem:cupomdb
Username: samuelcupom
Password: 123
```

## 📚 Próximos Passos

1. ✅ Executar testes: `mvn test`
2. ✅ Verificar cobertura: `mvn jacoco:report`
3. ✅ Testar API com Swagger: http://localhost:8080/swagger-ui.html
4. ✅ Revisar código e arquitetura
5. ✅ Ler documentação completa no README.md
