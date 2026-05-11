#!/bin/bash
echo "Criando recursos no LocalStack Community..."

# 1. Criar tabela DynamoDB
aws dynamodb create-table \
    --table-name orders \
    --attribute-definitions AttributeName=orderId,AttributeType=S \
    --key-schema AttributeName=orderId,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --endpoint-url http://localhost:4566 \
    --region us-east-1

echo "Tabela orders criada!"

# Criar tópico SNS
aws sns create-topic \
    --name order-events \
    --endpoint-url http://localhost:4566 \
    --region us-east-1

echo "Tópico SNS criado!"

# Criar fila SQS
aws sqs create-queue \
    --queue-name order-queue \
    --endpoint-url http://localhost:4566 \
    --region us-east-1

echo "Fila SQS criada!"

# 4. Inscrever SQS no SNS
aws sns subscribe \
    --topic-arn arn:aws:sns:us-east-1:000000000000:order-events \
    --protocol sqs \
    --notification-endpoint arn:aws:sqs:us-east-1:000000000000:order-queue \
    --endpoint-url http://localhost:4566 \
    --region us-east-1

echo "Inscrição SQS → SNS concluída!"
echo "LocalStack pronto para uso!"