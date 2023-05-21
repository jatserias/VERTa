package mt.nlp;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@ToString 
@EqualsAndHashCode
@Getter
@Setter
public class TimeExpressions {


    private int start;
	private int end;
	private String date;
	private String type;
	
}
