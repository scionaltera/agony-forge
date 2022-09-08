package com.agonyforge.core.model;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

public interface Persistent {
    DateFormat ISO_8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    Map<String, AttributeValue> freeze();
    void thaw(Map<String, AttributeValue> item);
}
