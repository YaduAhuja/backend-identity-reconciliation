package com.example.demo.configuration;

import com.example.demo.models.Contact;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.stereotype.Component;

import java.util.List;

@Configuration
@AllArgsConstructor
public class JDBCConfiguration extends AbstractJdbcConfiguration {
    private final LinkPrecedenceReadConverter linkPrecedenceReadConverter;

    @Override
    protected List<?> userConverters() {
        return List.of(
                linkPrecedenceReadConverter
        );
    }

    @Component
    @ReadingConverter
    public static class LinkPrecedenceReadConverter implements Converter<String, Contact.LinkPrecedence> {
        @Override
        public Contact.LinkPrecedence convert(String source) {
            for (Contact.LinkPrecedence linkPrecedence : Contact.LinkPrecedence.values()) {
                if (source.equalsIgnoreCase(linkPrecedence.getValue()))
                    return linkPrecedence;
            }

            throw new IllegalArgumentException("No enum constant for value " + source);
        }
    }
}
