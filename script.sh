#!/bin/bash

docker-compose -f Dockerfile.dynamoDB up -d

aws dynamodb --endpoint-url http://localhost:8042 create-table \
  --table-name TokenCounter \
  --attribute-definitions AttributeName=actorId,AttributeType=S AttributeName=duration,AttributeType=S \
  --key-schema AttributeName=actorId,KeyType=HASH AttributeName=duration,KeyType=RANGE \
  --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1

aws dynamodb --endpoint-url http://localhost:8042 update-time-to-live --table-name TokenCounter --time-to-live-specification "Enabled=true, AttributeName=ttl"

aws dynamodb --endpoint-url http://localhost:8042 scan --table-name TokenCounter
