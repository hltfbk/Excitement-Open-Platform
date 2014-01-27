package eu.excitementproject.eop.distsim.mapred;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

public class SimilarityScore implements Writable {


	public SimilarityScore() {
		item = new Text();
		score = new DoubleWritable();
	}
	
	public SimilarityScore(String item, double score) {
		this.item = new Text(item);
		this.score = new DoubleWritable(score);
	}


	/* (non-Javadoc)
	 * @see org.apache.hadoop.io.Writable#readFields(java.io.DataInput)
	 */
	@Override
	public void readFields(DataInput in) throws IOException {
		item.readFields(in);
		score.readFields(in);		
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.io.Writable#write(java.io.DataOutput)
	 */
	@Override
	public void write(DataOutput out) throws IOException {
		item.write(out);
		score.write(out);
	}

	public Text getItem() { 
		return item; 
	}
	
	public DoubleWritable getScore() { 
		return score; 
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return item.toString() + ":" + score.get();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		result = prime * result + ((score == null) ? 0 : score.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimilarityScore other = (SimilarityScore) obj;
		if (item == null) {
			if (other.item != null)
				return false;
		} else if (!item.equals(other.item))
			return false;
		if (score == null) {
			if (other.score != null)
				return false;
		} else if (!score.equals(other.score))
			return false;
		return true;
	}

	
	protected Text item;
	protected DoubleWritable score;
	
	public static void main(String[] args) {
		SimilarityScore tmp1 = new SimilarityScore("a",0.01);
		SimilarityScore tmp2 = new SimilarityScore("b",0.02);
		List<SimilarityScore> values = new LinkedList<SimilarityScore>();
		values.add(tmp1);
		values.add(tmp2);
		
		System.out.println(values);
		
		Collections.sort(values, new Comparator<SimilarityScore>() {
			@Override
			public int compare(SimilarityScore o1, SimilarityScore o2) {
				return (o2.getScore().get() > o1.getScore().get() ? 1 : o1.getScore().get() > o2.getScore().get() ? -1 : 0); }}
			);
		
		System.out.println(values);
		
	}

}
