/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawl;

/**
 *
 * @author satadisha
 */
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.util.MatrixCalculator;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import java.util.List;
//Given token of two sentences gives the pairwise semantic similarity between them in the form of a matrix
public class SemanticSim {
	private static ILexicalDatabase db;
	private static RelatednessCalculator rc;
	
	public SemanticSim(){
		db = new NictWordNet();
		rc = new WuPalmer(db);
	}
	
	public void getSemanticSim(String word1, String word2){
		WS4JConfiguration.getInstance().setMFS(true);
		double sim = rc.calcRelatednessOfWords(word1, word2);
        System.out.println( "Similarity Class: "+rc.getClass().getName()+"\t"+"Similarity Score: "+sim );
	}
	
	public double[][] buildMatrix( List<String> lemma1, List<String> lemma2 ) {
        //return MatrixCalculator.getSynonymyMatrix( words1, words2 );
            String[] words1= new String[lemma1.size()];
            words1= lemma1.toArray(words1);
            String[] words2= new String[lemma2.size()];
            words2= lemma2.toArray(words2);
            return MatrixCalculator.getNormalizedSimilarityMatrix( words1, words2,rc );
	}
	
	public void printMatrix(double[][] matrix){
		int i,j;
		for(i=0;i<matrix.length;i++){
			for(j=0;j<matrix[i].length;j++){
				System.out.print(matrix[i][j]+"\t");
			}
			System.out.println();
		}
	}
}


