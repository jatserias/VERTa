package mt.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class SentenceSimilarityBase implements ISentenceMetric {

    @JsonIgnore
    private MetricActivationCounter counters;

}
