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
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.distsim.domains.relation.PredicateArgumentSlots;

/**
 * @author Meni Adler
 * @since May 26, 2013
 * 
 * Extracts elements and features from a given corpus, for a DIRT model (i.e., predicates, and arguments X and Y), based on map-reduce scheme.
 * At the first stage, the map-reduce procedure extracts counts (and filters by minimal count)the elements and the features.
 * At the second stage the extracted elements and features, are organized into the 'traditional' distsim format, the output files: elements,features, element-feature-counts, feature-elements  
 *
 */

public class ExtractAndCountBasicNodeBasedDirtElementsFeatures { 

  public static void main(String[] args) throws Exception {
	    //ConfigurationFile confFile = new ConfigurationFile(args[0]);
	    ConfigurationFile confFile = new ConfigurationFile(new ImplCommonConfig(new File(args[0])));
	    
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
	    for (File f : eu.excitementproject.eop.distsim.util.FileUtils.getFiles(new File(confParams.get(eu.excitementproject.eop.distsim.util.Configuration.INDIR))))
	    	FileInputFormat.addInputPath(job, new Path(f.getPath()));
	    //specific for Reuters
	    //for (File cd : new File(confParams.get(eu.excitementproject.eop.distsim.util.Configuration.INDIR)).listFiles())
	    	//for (File dir : cd.listFiles())
	    		//FileInputFormat.addInputPath(job, new Path(dir.getPath()));
	    String sOutdir = confParams.get(eu.excitementproject.eop.distsim.util.Configuration.OUTDIR);
	    File outdir = new File(sOutdir);
	    	    
	    if (outdir.exists()) {
		    if (outdir.isFile())
	    	for (File f : outdir.listFiles())
	    		f.delete();
	    	outdir.delete();	    	
	    }

	    FileOutputFormat.setOutputPath(job, new Path(sOutdir));
	    // Apply the first map-reduce stage, which extracts counts (and filters by minimal count) the elements and features 
	    job.waitForCompletion(true);

	    // Apply the second stage, in order to organize the extracted elements and the X features into the 'traditional' distsim format.
	    // 1.1 Generate the 'elements' and the 'features-x' output files
	    SeparateFilterAndIndexElementsFeatures.separateFilterAndIndexElementsFeatures1(confFile.getModuleConfiguration(eu.excitementproject.eop.distsim.util.Configuration.MAPRED_SEPARATE_FILTER_INDEX_ELEMENT_FEATURE_1_X),PredicateArgumentSlots.X);
	    // 1.2. Generate the 'element-feature-counts-x' and the 'feature-elements-x' output files
	    SeparateFilterAndIndexElementsFeatures.separateFilterAndIndexElementsFeatures2(confFile.getModuleConfiguration(eu.excitementproject.eop.distsim.util.Configuration.MAPRED_SEPARATE_FILTER_INDEX_ELEMENT_FEATURE_2_X));
	    // 2.1 Generate the 'features-y' output file
	    SeparateFilterAndIndexElementsFeatures.separateFilterAndIndexElementsFeatures1(confFile.getModuleConfiguration(eu.excitementproject.eop.distsim.util.Configuration.MAPRED_SEPARATE_FILTER_INDEX_ELEMENT_FEATURE_1_Y),PredicateArgumentSlots.Y);
	    // 2.2 Generate the 'element-feature-counts-y' and the 'feature-elements-y' output files
	    SeparateFilterAndIndexElementsFeatures.separateFilterAndIndexElementsFeatures2(confFile.getModuleConfiguration(eu.excitementproject.eop.distsim.util.Configuration.MAPRED_SEPARATE_FILTER_INDEX_ELEMENT_FEATURE_2_Y));
	 }
 }
