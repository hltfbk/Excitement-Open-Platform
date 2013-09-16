package eu.excitementproject.eop.distsim.builders.mapred;

import java.io.File;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.domains.relation.PredicateArgumentSlots;

public class ExtractAndCountBasicNodeBasedDirtElementsFeatures { 

  public static void main(String[] args) throws Exception {
	    ConfigurationFile confFile = new ConfigurationFile(args[0]);
	    ConfigurationParams confParams = confFile.getModuleConfiguration(eu.excitementproject.eop.distsim.util.Configuration.MAPRED_COOCCURRENCE_COUNTING);

	    Configuration conf = new Configuration();
	    
	    // tmp for my linux account
	    /*conf.set("hadoop.tmp.dir","/home/ir/adlerm6/tmp");
	    conf.set("dfs.name.dir", "/home/ir/adlerm6/tmp");
	    conf.set("dfs.data.dir", "/home/ir/adlerm6/tmp");
	    conf.set("dfs.client.buffer.dir", "/home/ir/adlerm6/tmp");
	    conf.set("mapred.local.dir", "/home/ir/adlerm6/tmp");*/
	    
	    conf.set(eu.excitementproject.eop.distsim.util.Configuration.CONFIGURATION_FILE, args[0]);
	    conf.set(eu.excitementproject.eop.distsim.util.Configuration.CONFIGURATION_MODULE,eu.excitementproject.eop.distsim.util.Configuration.MAPRED_COOCCURRENCE_COUNTING);
	    Job job = new Job(conf, "Count cooccurrences");
	    
	    job.setJarByClass(ExtractAndCountBasicNodeBasedDirtElementsFeatures.class);
	    job.setMapperClass(ExtractAndCountBasicNodeBasedElementsFeatures.MapClass.class);
	    job.setCombinerClass(ExtractAndCountBasicNodeBasedElementsFeatures.ReduceClass.class);
	    job.setReducerClass(ExtractAndCountBasicNodeBasedElementsFeatures.ReduceClass.class);
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(LongWritable.class);
	    job.setOutputKeyClass(Text.class);    
	    job.setOutputValueClass(LongWritable.class);
	    job.setInputFormatClass(BasicNodeInputFormat.class);
	    //for (File f : eu.excitementproject.eop.distsim.util.FileUtils.getFiles(new File(confParams.get(eu.excitementproject.eop.distsim.util.Configuration.INDIR))))
	    //specific for Reuters
	    for (File cd : new File(confParams.get(eu.excitementproject.eop.distsim.util.Configuration.INDIR)).listFiles())
	    	for (File dir : cd.listFiles())
	    		FileInputFormat.addInputPath(job, new Path(dir.getPath()));
	    String sOutdir = confParams.get(eu.excitementproject.eop.distsim.util.Configuration.OUTDIR);
	    File outdir = new File(sOutdir);
	    
	    if (outdir.exists()) {
	    	for (File f : outdir.listFiles())
	    		f.delete();
	    	outdir.delete();
	    }
	    
	    FileOutputFormat.setOutputPath(job, new Path(sOutdir));
	    job.waitForCompletion(true);
	    
	    SeparateFilterAndIndexElementsFeatures.separateFilterAndIndexElementsFeatures1(confFile.getModuleConfiguration(eu.excitementproject.eop.distsim.util.Configuration.MAPRED_SEPARATE_FILTER_INDEX_ELEMENT_FEATURE_1_X),PredicateArgumentSlots.X);
	    SeparateFilterAndIndexElementsFeatures.separateFilterAndIndexElementsFeatures2(confFile.getModuleConfiguration(eu.excitementproject.eop.distsim.util.Configuration.MAPRED_SEPARATE_FILTER_INDEX_ELEMENT_FEATURE_2_X));
	    SeparateFilterAndIndexElementsFeatures.separateFilterAndIndexElementsFeatures1(confFile.getModuleConfiguration(eu.excitementproject.eop.distsim.util.Configuration.MAPRED_SEPARATE_FILTER_INDEX_ELEMENT_FEATURE_1_Y),PredicateArgumentSlots.Y);
	    SeparateFilterAndIndexElementsFeatures.separateFilterAndIndexElementsFeatures2(confFile.getModuleConfiguration(eu.excitementproject.eop.distsim.util.Configuration.MAPRED_SEPARATE_FILTER_INDEX_ELEMENT_FEATURE_2_Y));

	 }
 }
