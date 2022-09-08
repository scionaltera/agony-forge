package com.agonyforge.core.model.repository;

import com.agonyforge.core.config.DynamoConfiguration;
import com.agonyforge.core.model.Persistent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ComparisonOperator;
import software.amazon.awssdk.services.dynamodb.model.Condition;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class BaseRepository<T extends Persistent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseRepository.class);

    protected final DynamoConfiguration config;
    protected final DynamoDbClient DYNAMO_DB_CLIENT;
    protected final Class<T> klass;

    public BaseRepository(DynamoConfiguration config, Class<T> klass) {
        this.config = config;
        try {
            this.DYNAMO_DB_CLIENT = DynamoDbClient
                .builder()
                .region(config.getRegion())
                .endpointOverride(new URI(config.getEndpoint()))
                .build();
        } catch (URISyntaxException e) {
            LOGGER.error("Unable to configure DynamoDB client: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        this.klass = klass;
    }

    public abstract T newInstance();

    public Optional<T> getOne(AttributeValue pk, AttributeValue sk) {
        Map<String, Condition> filter = new HashMap<>();

        filter.put("pk", Condition.builder()
            .comparisonOperator(ComparisonOperator.EQ)
            .attributeValueList(pk)
            .build());
        filter.put("sk", Condition.builder()
            .comparisonOperator(ComparisonOperator.EQ)
            .attributeValueList(sk)
            .build());

        QueryRequest request = QueryRequest.builder()
            .tableName(config.getTableName())
            .keyConditions(filter)
            .build();

        try {
            QueryResponse response = DYNAMO_DB_CLIENT.query(request);

            if (!response.hasItems() || response.items().size() <= 0) {
                return Optional.empty();
            }

            T item = newInstance();
            Map<String, AttributeValue> data = response.items().get(0);

            item.thaw(data);

            return Optional.of(item);
        } catch (DynamoDbException e) {
            LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
        }

        return Optional.empty();
    }

    public void save(T item) {
        Map<String, AttributeValue> map = item.freeze();
        PutItemRequest request = PutItemRequest.builder()
            .tableName(config.getTableName())
            .item(map)
            .build();

        try {
            DYNAMO_DB_CLIENT.putItem(request);
        } catch (DynamoDbException e) {
            LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
        }
    }

    public void saveAll(List<T> items) {
        for (int i = 0; i < items.size(); i += 25) {
            List<T> sublist = items.subList(i, Math.min(i + 25, items.size()));
            List<WriteRequest> writeRequests = sublist
                .stream()
                .map(item -> WriteRequest
                    .builder()
                    .putRequest(PutRequest
                        .builder()
                        .item(item.freeze())
                        .build())
                    .build())
                .collect(Collectors.toList());
            Map<String, List<WriteRequest>> operations = new HashMap<>();

            operations.put(config.getTableName(), writeRequests);

            BatchWriteItemRequest batch = BatchWriteItemRequest
                .builder()
                .requestItems(operations)
                .build();

            try {
                DYNAMO_DB_CLIENT.batchWriteItem(batch);
                LOGGER.info("wrote batch of {} items", sublist.size());
            } catch (DynamoDbException e) {
                LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
            }
        }
    }

    public void delete(T item) {
        Map<String, AttributeValue> map = item.freeze();
        DeleteItemRequest request = DeleteItemRequest.builder()
            .tableName(config.getTableName())
            .key(Map.of(
                "pk", map.get("pk"),
                "sk", map.get("sk")))
            .build();

        try {
            DYNAMO_DB_CLIENT.deleteItem(request);
        } catch (Exception e) {
            LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
        }
    }

    public void deleteAll(List<T> items) {
        for (int i = 0; i < items.size(); i += 25) {
            List<T> sublist = items.subList(i, Math.min(i + 25, items.size()));
            List<WriteRequest> writeRequests = sublist
                .stream()
                .map(item -> {
                    Map<String, AttributeValue> map = item.freeze();
                    return WriteRequest
                        .builder()
                        .deleteRequest(DeleteRequest
                            .builder()
                            .key(Map.of(
                                "pk", map.get("pk"),
                                "sk", map.get("sk")
                            ))
                            .build())
                        .build();
                })
                .collect(Collectors.toList());
            Map<String, List<WriteRequest>> operations = new HashMap<>();

            operations.put(config.getTableName(), writeRequests);

            BatchWriteItemRequest batch = BatchWriteItemRequest
                .builder()
                .requestItems(operations)
                .build();

            try {
                DYNAMO_DB_CLIENT.batchWriteItem(batch);
                LOGGER.info("Deleted batch of {} items", sublist.size());
            } catch (DynamoDbException e) {
                LOGGER.error("DynamoDbException: {}", e.getMessage(), e);
            }
        }
    }
}
