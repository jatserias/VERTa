package mt.nlp;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import mt.core.DistanceMatrix;


public class SerializationTest {
    static ObjectMapper mapper;

    @BeforeAll
    public static void start() {
        YAMLFactory f = new YAMLFactory();
		f.disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID);
		mapper = new ObjectMapper(f);
    }


    @Test
    public void SentenceSerialization1() throws IOException {
		Sentence serializedEx = SentenceTest.getSentenceEx1();
        File tempFile = File.createTempFile("sentence1", ".yaml");
        tempFile.deleteOnExit();
        mapper.writeValue(tempFile, serializedEx);
    }

    @Test
    public void SentenceSerialization2() throws Exception {
		Sentence serializedEx = SentenceTest.getSentenceEx2();
        File tempFile = File.createTempFile("sentence2", ".yaml");
        tempFile.deleteOnExit();
        mapper.writeValue(tempFile, serializedEx);
    }

    @Test
    public void SentenceSerializationJson() throws Exception {
        ObjectMapper jsonMapper = new ObjectMapper();
        Sentence serializedEx = SentenceTest.getSentenceEx1();
        File tempFile1 = File.createTempFile("sentence1", ".json");
        tempFile1.deleteOnExit();
        jsonMapper.writeValue(tempFile1, serializedEx);
		serializedEx = SentenceTest.getSentenceEx2();
        File tempFile2 = File.createTempFile("sentence2", ".json");
        tempFile2.deleteOnExit();
        jsonMapper.writeValue(tempFile2, serializedEx);
    }

    @Test
    public void SentenceDeSerialization1() throws IOException {  
		InputStreamReader yamlFile = new InputStreamReader(this.getClass().getResourceAsStream("/sentence1.yaml"));
		Sentence sentence = mapper.readValue(yamlFile, Sentence.class);
    }

    @Test
    public void SentenceDeSerialization2() throws IOException {  
		InputStreamReader yamlFile = new InputStreamReader(this.getClass().getResourceAsStream("/sentence2.yaml"));
		Sentence sentence = mapper.readValue(yamlFile, Sentence.class);
    }

    @Test
    public void matrixSerializationJson() throws StreamWriteException, DatabindException, IOException {
        DistanceMatrix matrix = new DistanceMatrix(9, 9);
        for(int i=0; i<9; ++i) {
            for(int j=0;j<9; ++j) {
                if(i == j)
                    matrix.addDistance(j, i, 1.0, "");
                else
                    matrix.addDistance(j, i, 0.0, "");
            }
        }
        ObjectMapper jsonMapper = new ObjectMapper();
        File tempFile = File.createTempFile("matrix", ".json");
        tempFile.deleteOnExit();
        jsonMapper.writeValue(tempFile, matrix);
    }
}
