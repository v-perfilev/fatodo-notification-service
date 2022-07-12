package com.persoff68.fatodo.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ArrayListConverter implements AttributeConverter<List<Integer>, String> {

    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(List<Integer> list) {
        String s = "";
        try {
            s = objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            log.error("List converter error: ", e);
        }
        return s;
    }

    @Override
    public List<Integer> convertToEntityAttribute(String s) {
        List<Integer> list = new ArrayList<>();
        try {
            CollectionLikeType listType = objectMapper.getTypeFactory()
                    .constructCollectionLikeType(ArrayList.class, Integer.class);
            list = objectMapper.readValue(s, listType);
        } catch (IOException e) {
            log.error("List converter error: ", e);
        }
        return list;
    }

}
