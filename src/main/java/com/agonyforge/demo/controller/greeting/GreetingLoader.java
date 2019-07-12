package com.agonyforge.demo.controller.greeting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GreetingLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(GreetingLoader.class);
    private static final String GREETING_FILENAME = "/greeting.txt";

    public List<String> load() {
        InputStream is = GreetingLoader.class.getResourceAsStream(GREETING_FILENAME);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        LOGGER.info("Loaded embedded greeting: {}", GREETING_FILENAME);

        return parse(reader);
    }

    private static List<String> parse(BufferedReader reader) {
        return reader.lines()
            .map(line -> {
                if (line.startsWith("#")) {
                    return "";
                } else if (line.startsWith("*")) {
                    return line.substring(1);
                } else {
                    return line.replace(" ", "&nbsp;");
                }
            })
            .filter(line -> line.length() > 0)
            .collect(Collectors.toList());
    }
}
