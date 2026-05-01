package mt.core;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import mt.SimilarityEqual;

public class FeatureMetricTest {

    @Test
    public void testWriteYaml() throws JsonProcessingException{
        YAMLFactory f = new YAMLFactory();
        f.disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID);
        final ObjectMapper mapper = new ObjectMapper(f);
       
        String featureName = "test";
        IFeaturesWordSimilarity similarityFunction = new SimilarityEqual();
        double weight = 1.0;
        FeatureMetric  serializedEx = new FeatureMetric(featureName, similarityFunction, weight);
        String jsonDataString = mapper.writeValueAsString(serializedEx);
        System.err.println(jsonDataString );
    }

    @Test
    public void testReadJson() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        String kk = "{\"featureNames\":[\"test\"],\"similarityFunction\":{\"type\":\"SimilarityEqual\",\"weight\":1.0},\"weight\":1.0,\"reversed\":false}";
        FeatureMetric  serializedEx = mapper.readValue(kk, FeatureMetric.class);
        System.err.println(serializedEx);
    }

    @Test
    public void testWriteJson() throws JsonProcessingException{
        final ObjectMapper mapper = new ObjectMapper();
       
        String featureName = "test";
        IFeaturesWordSimilarity similarityFunction = new SimilarityEqual();
        double weight = 1.0;
        FeatureMetric  serializedEx = new FeatureMetric(featureName, similarityFunction, weight);
        String jsonDataString = mapper.writeValueAsString(serializedEx);
        System.err.println(jsonDataString);
   }

    @Test
    public void testReadYaml() throws JsonMappingException, JsonProcessingException{
        YAMLFactory f = new YAMLFactory();
        f.disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID);
        final ObjectMapper mapper = new ObjectMapper(f);
       
        String yamlEx = """
            featureNames:
                - \"test\"
            similarityFunction:
              type: \"SimilarityEqual\"
              weight: 1.0
            weight: 3.0
            reversed: false
        """;
        FeatureMetric  serializedEx = mapper.readValue(yamlEx, FeatureMetric.class);
        assertFalse(serializedEx.isReversed(), "incorrect reversed value");
        assertEquals(serializedEx.getWeight(), 3.0,"incorrect weigth value");
        assertEquals(serializedEx.getFeatureNames().length, 1, "incorrect feature names size");    
    }
}
