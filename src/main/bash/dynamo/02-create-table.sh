#!/bin/bash

# Inspired by https://aws.plainenglish.io/run-aws-dynamodb-locally-2788ad73c4db

echo "Creating table..."
aws "$AWS_ENDPOINT" \
    dynamodb create-table \
        --table-name agonyforge \
        --attribute-definitions \
          AttributeName=pk,AttributeType=S \
          AttributeName=sk,AttributeType=S \
        --key-schema \
          AttributeName=pk,KeyType=HASH \
          AttributeName=sk,KeyType=RANGE \
        --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5

echo "Describing table..."
aws "$AWS_ENDPOINT" \
  dynamodb describe-table --table-name agonyforge --output table


