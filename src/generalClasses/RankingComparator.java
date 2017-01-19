/**
 * 
 */
package generalClasses;
import java.util.Comparator;

import dataManagement.MatchingSearchDocument;
/**
 * This class is used to compare the rankings of the matched documents, used for searching.
 * @author carlosgrivera
 *
 */
public class RankingComparator implements Comparator<MatchingSearchDocument>{

	@Override
	public int compare(MatchingSearchDocument o1, MatchingSearchDocument o2) {
		// TODO Auto-generated method stub
		return (int) (o1.getRank()-o2.getRank());
	}
	
}
