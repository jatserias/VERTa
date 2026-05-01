package mt.core;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.util.Set;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

public class LabelSetTest {

    @Test
    public void testWriteYaml() throws JsonProcessingException {
        YAMLFactory f = new YAMLFactory();
        f.disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID);
        final ObjectMapper mapper = new ObjectMapper(f);

        LabelSet labelset = new LabelSet.LabelSetBuilder().id("testa").labels(Set.of("1", "2")).build();
        String jsonDataString = mapper.writeValueAsString(labelset);
        System.err.println(jsonDataString);
    }

    @Test
    public void testReadYaml() throws JsonMappingException, JsonProcessingException {
        YAMLFactory f = new YAMLFactory();
        f.disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID);
        final ObjectMapper mapper = new ObjectMapper(f);

        String yamlEx = """
        labels:
            - \"2\"
            - \"1\"
        weight: null
        id: \"testa\"
        """;
        LabelSet labelset = mapper.readValue(yamlEx, LabelSet.class);
        assertEquals(labelset.getId(), "testa", "incorrect id");
        assertNull(labelset.getWeight(), "incorrect weigth");
        assertEquals(labelset.getLabels(),Set.of("1", "2"), "incorrect labels");
    }
}
