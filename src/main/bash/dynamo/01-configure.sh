#!/bin/bash

# Inspired by https://aws.plainenglish.io/run-aws-dynamodb-locally-2788ad73c4db

echo "Creating profile..."
aws configure set aws_access_key_id default_access_key --profile=dynamodb-local
aws configure set aws_secret_access_key default_secret_key --profile=dynamodb-local
aws configure set region us-west-2 --profile=dynamodb-local

echo "Listing profile..."
aws configure list
