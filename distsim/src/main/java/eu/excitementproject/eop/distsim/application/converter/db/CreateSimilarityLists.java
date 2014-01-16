package eu.excitementproject.eop.distsim.application.converter.db;


import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import eu.excitementproject.eop.distsim.mapred.SimilarityScore;

public class CreateSimilarityLists { 

	public static class L2RMapClass extends Mapper<LongWritable, Text, Text, SimilarityScore> {
	    
	    @Override
	    public void map(LongWritable left, Text value, Context context) throws IOException,  InterruptedException {
	    	String[] toks = value.toString().split("\t");
	        context.write(new Text(toks[0]), new SimilarityScore(toks[1],Double.parseDouble(toks[2])));
	    }
	  }
	
	public static class R2LMapClass extends Mapper<LongWritable, Text, Text, SimilarityScore> {
	    
	    @Override
	    public void map(LongWritable left, Text value, Context context) throws IOException,  InterruptedException {
	    	String[] toks = value.toString().split("\t");
	        context.write(new Text(toks[1]), new SimilarityScore(toks[0],Double.parseDouble(toks[2])));
	    }
	  }

	public static class ReduceClass extends Reducer<Text,SimilarityScore,Text,Text> {
		@Override
	    public void reduce(Text item, Iterable<SimilarityScore> itemScores, Context context) throws IOException,  InterruptedException {
			List<SimilarityScore> values = new LinkedList<SimilarityScore>();
			for (SimilarityScore itemScore : itemScores)   
				values.add(new SimilarityScore(itemScore.getItem().toString(),itemScore.getScore().get()));

			Collections.sort(values, new Comparator<SimilarityScore>() {
				@Override
				public int compare(SimilarityScore o1, SimilarityScore o2) {
					return (o2.getScore().get() > o1.getScore().get() ? 1 : o1.getScore().get() > o2.getScore().get() ? -1 : 0);
				}}
				);
			
			int i=0;
			StringBuilder sb = new StringBuilder();
			for (SimilarityScore itemScore : values) {					
				sb.append(itemScore.getItem());
				sb.append("\t");
				sb.append(itemScore.getScore());
				if (i<values.size()-1)
					sb.append("\t");
				i++;
			}
			context.write(item, new Text(sb.toString()));
			
		}
	}
	  
	 public static void main(String[] args) throws Exception {
		    Configuration conf = new Configuration();
		    //conf.set("mapred.map.tasks","10");
		    //conf.set("mapred.reduce.tasks","2");
		    conf.set("hadoop.tmp.dir","/home/ir/adlerm6/tmp");
		    conf.set("dfs.name.dir", "/home/ir/adlerm6/tmp");
		    conf.set("dfs.data.dir", "/home/ir/adlerm6/tmp");
		    conf.set("dfs.client.buffer.dir", "/home/ir/adlerm6/tmp");
		    conf.set("mapred.local.dir", "/home/ir/adlerm6/tmp");

		    new File(args[1]+ "/l2r").delete();
		    new File(args[1]+ "/r2l").delete();
		    
		    Job job1 = new Job(conf, "Create L2R list");		    
		    job1.setJarByClass(CreateSimilarityLists.class);
		    job1.setMapperClass(L2RMapClass.class);
		    job1.setReducerClass(ReduceClass.class);
		    job1.setMapOutputKeyClass(Text.class);
		    job1.setMapOutputValueClass(SimilarityScore.class);
		    job1.setOutputKeyClass(Text.class);    
		    job1.setOutputValueClass(Text.class);
		    FileInputFormat.addInputPath(job1, new Path(args[0]));
		    FileOutputFormat.setOutputPath(job1, new Path(args[1] + "/l2r"));
		    job1.waitForCompletion(true);
		    
		    Job job2 = new Job(conf, "Create R2L list");		    
		    job2.setJarByClass(CreateSimilarityLists.class);
		    job2.setMapperClass(R2LMapClass.class);
		    job2.setReducerClass(ReduceClass.class);
		    job2.setMapOutputKeyClass(Text.class);
		    job2.setMapOutputValueClass(SimilarityScore.class);
		    job2.setOutputKeyClass(Text.class);    
		    job2.setOutputValueClass(Text.class);
		    FileInputFormat.addInputPath(job2, new Path(args[0]));
		    FileOutputFormat.setOutputPath(job2, new Path(args[1] + "/r2l"));
		    job2.waitForCompletion(true);

	 }
}
