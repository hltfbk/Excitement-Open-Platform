package eu.excitementproject.eop.distsim.util;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.common.utilities.sort.DiskSortIOException;
import eu.excitementproject.eop.common.utilities.sort.OsSort;
import eu.excitementproject.eop.distsim.builders.cooccurrence.GeneralCooccurrenceExtractor;
import eu.excitementproject.eop.distsim.scoring.ElementsSimilarityMeasure;
import eu.excitementproject.eop.distsim.storage.BasicMap;
import gnu.trove.iterator.TIntDoubleIterator;
import gnu.trove.map.TIntDoubleMap;


//import ac.biu.nlp.nlp.general.OS;
//import ac.biu.nlp.nlp.general.immutable.ImmutableIterator;
//import ac.biu.nlp.nlp.general.sort.DiskSortIOException;
//import ac.biu.nlp.nlp.general.sort.OsSort;

/**
 * 
 * Various sort utilities
 * 
 * <P>
 * Immutable, Thread-safe
 * 
 * @author Meni Adler
 * @since 21/07/2011
 * 
 */

public class SortUtil {

	private static final Logger logger = Logger.getLogger(GeneralCooccurrenceExtractor.class);

	
	/**
	 * Sort a given {@link Map} by its values
	 * 
	 * @param map
	 *            in map
	 * @param descending
	 *            determine the order of the sort: true - descending, false -
	 *            ascending
	 * @return out sorted-by-values map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static LinkedHashMap sortMapByValue(Map map, final boolean descending) {
		List<Map.Entry> list  = new LinkedList<Map.Entry>(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				int ret = ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
				if (descending)
					ret *= -1;
				return ret;
			}
		});

		LinkedHashMap result = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	
	/**
	 * Sort a given {@link TIntDoubleMap} by its values
	 * 
	 * @param map
	 *            in map
	 * @param descending
	 *            determine the order of the sort: true - descending, false -
	 *            ascending
	 * @return out sorted-by-values map
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static LinkedHashMap<Integer,Double> sortMapByValue(TIntDoubleMap map, final boolean descending) {
		List<Pair<Integer,Double>> list = new LinkedList<Pair<Integer,Double>>();
		TIntDoubleIterator it1 = map.iterator();
		while (it1.hasNext()) {
			it1.advance();
			list.add(new Pair<Integer, Double>(it1.key(),it1.value()));
		}
		Collections.sort(list, new Comparator() {
		public int compare(Object o1, Object o2) {
				int ret = ((Comparable) ((Pair) (o1)).getSecond())
						.compareTo(((Pair) (o2)).getSecond());
				if (descending)
					ret *= -1;
				return ret;
			}
		});

		LinkedHashMap<Integer,Double> result = new LinkedHashMap<Integer,Double>();
		Iterator<Pair<Integer,Double>> it2 = list.iterator();
		while (it2.hasNext()) {
			Pair<Integer,Double> pair = it2.next();
			result.put(pair.getFirst(), pair.getSecond());
		}
		return result;
	}
	
	
	/**
	 * Sort a given {@link BasicMap} by its values
	 * 
	 * @param map
	 *            in map
	 * @param descending
	 *            determine the order of the sort: true - descending, false -
	 *            ascending
	 * @return out sorted-by-values map
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static LinkedHashMap<Integer,Double> sortMapByValue(BasicMap<Integer,Double> map, final boolean descending) {
		List<Pair<Integer,Double>> list = new LinkedList<Pair<Integer,Double>>();
		ImmutableIterator<Pair<Integer, Double>> it1 = map.iterator();
		while (it1.hasNext()) {
			Pair<Integer, Double> pair = it1.next();
			list.add(new Pair<Integer, Double>(pair.getFirst(),pair.getSecond()));
		}
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				int ret = ((Comparable) ((Pair) (o1)).getSecond())
						.compareTo(((Pair) (o2)).getSecond());
				if (descending)
					ret *= -1;
				return ret;
			}
		});

		LinkedHashMap<Integer,Double> result = new LinkedHashMap<Integer,Double>();
		Iterator<Pair<Integer,Double>> it2 = list.iterator();
		while (it2.hasNext()) {
			Pair<Integer,Double> pair = it2.next();
			result.put(pair.getFirst(), pair.getSecond());
		}
		return result;
	}
	
	
	/**
	 * Sort a given file by applying the sort command of the operating system. 
	 * 
	 * @param infile a file to be sorted
	 * @param outfile the resulted sorted file
	 * @param parameters extra parameters for 'sort' OS command line 
	 * @throws DiskSortIOException
	 */
	public static void sortFile(File infile, File outfile, boolean bNumeric) throws DiskSortIOException {
		logger.info("Start soring " + infile + " file");
		OsSort.sortStatic(infile,outfile, false, bNumeric);
		logger.info("Finish soring " + infile + " file");
	}
	
	/**
	 * Sort a given file by applying the sort command of the operating system. The resulted sorted file will replace the given file
	 * 
	 * @param file a file to be sorted
	 * @param parameters extra parameters for 'sort' OS command line 
	 * @throws DiskSortIOException
	 */

	public static void sortFile(String file, boolean bNumeric) throws DiskSortIOException {
		File infile = new File(file);
		File sortedfile = new File(file + ".sorted.tmp");
		sortFile(infile,sortedfile,bNumeric);
		infile.delete();
		sortedfile.renameTo(infile);
	}
	
	
	/**
	 * Sort a given {@link BasicMap} by its values
	 * 
	 * @param map
	 *            in map
	 * @param descending
	 *            determine the order of the sort: true - descending, false -
	 *            ascending
	 * @return out sorted-by-values map
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void sortSimilarityRules(List<ElementsSimilarityMeasure> list, final boolean descending) {
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				int ret = (int) (descending ?
					((ElementsSimilarityMeasure)o2).getSimilarityMeasure() - ((ElementsSimilarityMeasure)o1).getSimilarityMeasure()
					:
					((ElementsSimilarityMeasure)o1).getSimilarityMeasure() - ((ElementsSimilarityMeasure)o2).getSimilarityMeasure()
					);
				return ret;
			}
		});
	}
}
