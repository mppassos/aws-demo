# AWS Microservice Demo - Sistema de Pedidos

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-green)
![AWS](https://img.shields.io/badge/AWS-Serverless-yellow)
![Docker](https://img.shields.io/badge/Docker-Ready-blue)
![LocalStack](https://img.shields.io/badge/LocalStack-Testing-purple)

> **Projeto demonstração para entrevista técnica - Desenvolvedor Java/AWS**

## 🎯 Sobre o Projeto

Este projeto foi desenvolvido como parte da preparação para uma vaga de desenvolvedor Java com foco em AWS. Demonstra habilidades práticas em:

- Desenvolvimento de microsserviços com **Spring Boot**
- Integração com serviços **AWS** (DynamoDB, SNS, SQS)
- **Arquitetura orientada a eventos**
- Testes de integração com **LocalStack**
- Containerização com **Docker**

---

## 🏗️ Arquitetura do Sistema

```
┌─────────┐      ┌──────────────┐      ┌────────────┐
│ Cliente │─────▶│ Spring Boot  │─────▶│ DynamoDB   │
│ (REST)  │      │ (API)        │      │ (Pedidos)  │
└─────────┘      └──────┬───────┘      └────────────┘
                        │
                        ▼
                 ┌──────────────┐
                 │   SNS Topic  │
                 │ (Eventos)    │
                 └──────┬───────┘
                        │
                        ▼
                 ┌──────────────┐
                 │  SQS Queue   │
                 │ (Consumidor) │
                 └──────────────┘
```

### Fluxo de um Pedido:

1. **Cliente** faz POST para `/api/orders`
2. **Controller** recebe e valida os dados
3. **Service** aplica regras de negócio
4. **Repository** persiste no **DynamoDB**
5. **EventPublisher** publica evento no **SNS**
6. **SNS** entrega mensagem para **SQS**
7. Sistemas downstream consomem da fila

---

## 🛠️ Stack Tecnológica

| Categoria | Tecnologia | Versão |
|-----------|------------|--------|
| **Linguagem** | Java | 17 LTS |
| **Framework** | Spring Boot | 3.2.5 |
| **Build** | Maven | 3.9+ |
| **Banco NoSQL** | DynamoDB (LocalStack) | AWS SDK 2.21.29 |
| **Mensageria** | SNS + SQS (LocalStack) | - |
| **Container** | Docker | Multi-stage build |
| **Testes** | JUnit 5 + Testcontainers | - |
| **Mock Cloud** | LocalStack | 3.4 |

---

## 📁 Estrutura do Projeto

```
src/
├── main/java/com/matheus/awsdemo/
│   ├── AwsDemoApplication.java          # Classe principal
│   ├── config/
│   │   ├── AwsConfig.java               # Configuração clientes AWS
│   │   └── DynamoDBConfig.java          # Configuração DynamoDB
│   ├── controller/
│   │   └── OrderController.java         # Endpoints REST
│   ├── dto/
│   │   └── OrderRequest.java            # DTO de entrada
│   ├── model/
│   │   ├── Order.java                   # Entidade DynamoDB
│   │   └── OrderEvent.java              # Evento de domínio
│   ├── repository/
│   │   └── OrderRepository.java         # Acesso ao DynamoDB
│   └── service/
│       ├── EventPublisher.java          # Publicação SNS
│       └── OrderService.java            # Lógica de negócio
├── main/resources/
│   ├── application.yml                  # Configurações
│   ├── docker-compose.yml               # LocalStack
│   └── localstack-init.sh               # Script recursos AWS
└── test/java/com/matheus/awsdemo/
    └── integration/
        └── OrderIntegrationTest.java    # Testes integrados
```

---

## 🎓 Padrões e Boas Práticas

| Padrão | Onde foi aplicado |
|--------|-------------------|
| **Event-Driven Architecture** | SNS/SQS desacoplando serviços |
| **Outbox Pattern** | Pedido salvo + evento publicado |
| **SOLID** | Classes com responsabilidade única |
| **Clean Code** | Nomes significativos, métodos pequenos |
| **TDD** | Testes escritos com Testcontainers |
| **DTO Pattern** | Separando entrada (Request) da entidade |
| **Repository Pattern** | Abstraindo acesso ao DynamoDB |

---

## Como Executar Localmente

### 📋 Pré-requisitos

- Java 17+
- Docker Desktop
- Maven (ou use `./mvnw`)
- AWS CLI
- cURL (para testes)

### 📦 Passo 1: Clonar o Projeto

```bash
git clone https://github.com/SEU-USUARIO/aws-microservice-demo.git
cd aws-microservice-demo
```

### 🐳 Passo 2: Iniciar LocalStack

```bash
# Opção A - Com docker-compose (recomendado)
cd src/main/resources
docker-compose up -d

# Verificar se subiu
docker ps | grep localstack

# Aguardar inicialização (~15 segundos)
docker logs -f localstack-aws
# Quando aparecer "Ready.", aperte Ctrl+C
```

### ☁️ Passo 3: Criar Recursos AWS

```bash
# Tornar script executável
chmod +x src/main/resources/localstack-init.sh

# Executar
./src/main/resources/localstack-init.sh
```

**Recursos que serão criados:**
- 📦 Tabela DynamoDB: `orders`
- 📢 Tópico SNS: `order-events`
- 📬 Fila SQS: `order-queue`

### ▶️ Passo 4: Executar Aplicação

```bash
# Compilar e rodar
./mvnw spring-boot:run

# OU no IntelliJ: Abra AwsDemoApplication.java → ▶️ Run
```

**Console deve mostrar:**
```
Started AwsDemoApplication in 4.123 seconds
```

### 🧪 Passo 5: Testar API

```bash
# 1. Health Check
curl http://localhost:8080/api/orders/health
# Resposta: ✅ AWS Microservice Demo

# 2. Criar um pedido
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "matheus123",
    "totalAmount": 299.90
  }'

# 3. Resposta esperada:
# {
#   "orderId": "550e8400-e29b-41d4-a716-446655440000",
#   "customerId": "matheus123",
#   "totalAmount": 299.90,
#   "status": "PENDING",
#   "createdAt": "2026-05-11T17:55:00.000Z"
# }

# 4. Buscar pedido específico
curl http://localhost:8080/api/orders/550e8400-e29b-41d4-a716-446655440000

# 5. Verificar dados no DynamoDB
aws dynamodb scan \
  --table-name orders \
  --endpoint-url http://localhost:4566
```

---

## 🧪 Executando Testes

```bash
# Todos os testes
./mvnw test

# Teste específico de integração
./mvnw test -Dtest=OrderIntegrationTest

# Com relatório de cobertura (se configurado)
./mvnw verify
```

---

## 🐳 Build Docker

```bash
# Criar imagem multi-stage
docker build -t matheus/aws-demo:1.0.0 .

# Executar container
docker run -p 8080:8080 \
  -e DYNAMODB_ENDPOINT=http://host.docker.internal:4566 \
  -e SNS_ENDPOINT=http://host.docker.internal:4566 \
  matheus/aws-demo:1.0.0
```

---

## 🔧 Troubleshooting

| Erro | Solução |
|------|---------|
| **"Could not connect to localhost:4566"** | `docker start localstack-aws` |
| **"Table orders does not exist"** | Execute `localstack-init.sh` |
| **"Lombok error"** | IntelliJ → Settings → Annotation Processors → Enable |
| **"Java version mismatch"** | Configure SDK para Java 17 no IntelliJ |

---

## 👤 Autor

**Matheus** - Desenvolvedor Java | Entusiasta AWS

[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/SEU-USUARIO)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://linkedin.com/in/SEU-PERFIL)

---

## 📄 Licença

Este projeto está sob licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

⭐ **Se este projeto te ajudou, deixe uma estrela!** ⭐