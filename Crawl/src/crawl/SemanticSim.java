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
import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.Relatedness;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.util.WordSimilarityCalculator;
import edu.cmu.lti.ws4j.util.MatrixCalculator;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
//Given token of two sentences gives the pairwise semantic similarity between them in the form of a matrix
public class SemanticSim {
	private static ILexicalDatabase db;
	private static RelatednessCalculator rc;
	
        private int getIndex(double[] array, double element){
            int i;
            for(i=0; i<array.length;i++){
                if(array[i]==element)
                    break;
            }
            return i;
        }
        
	public SemanticSim(){
		db = new NictWordNet();
		rc = new WuPalmer(db);
	}
	
	public double getSemanticSimWords(String word1, String word2){
		WS4JConfiguration.getInstance().setMFS(true);
		//double sim = rc.calcRelatednessOfWords(word1, word2);
                
                List<POS[]> posPairs = rc.getPOSPairs();
                double maxScore = -1D;
                for(POS[] posPair: posPairs) {
                List<Concept> synsets1 = (List<Concept>)db.getAllConcepts(word1, posPair[0].toString());
                List<Concept> synsets2 = (List<Concept>)db.getAllConcepts(word2, posPair[1].toString());

                for(Concept ss1: synsets1) 
                {
                    for (Concept ss2: synsets2) {

                        Relatedness relatedness = rc.calcRelatednessOfSynset(ss1, ss2);
                        double score = relatedness.getScore();
                        if (score > maxScore) { 
                                 maxScore = score;
                        }
                        String p1 = ss1.getPos().toString();
                        String p2 = ss2.getPos().toString();
                    }
                }} if (maxScore == -1D) {
                maxScore = 0.0;}
                double similar=maxScore;
                //WordSimilarityCalculator wc= new WordSimilarityCalculator();
                //double similar= wc.calcRelatednessOfWords(word1, word2, rc);
        //System.out.println( "Similarity Class: "+rc.getClass().getName()+"\t"+"Similarity Score: "+similar );
                return similar;
	}
	
	public double[][] buildMatrix( List<String> lemma1, List<String> lemma2 ) {
        
            System.out.println("lemma1:"+lemma1);
            System.out.println("lemma2:"+lemma2);
            String[] words1=null,words2=null; 
            if(lemma1.size()<=lemma2.size()){
                 words1= new String[lemma1.size()];
                 words1= lemma1.toArray(words1);
                 words2= new String[lemma2.size()];
                 words2= lemma2.toArray(words2);
            }
            else{
               words1= new String[lemma2.size()];
               words1= lemma2.toArray(words1);
               words2= new String[lemma1.size()];
               words2= lemma1.toArray(words2);
            }            
            double[][] result = new double[words1.length][words2.length];
                for ( int i=0; i<words1.length; i++ ) {
                        for ( int j=0; j<words2.length; j++ ) {
                                double score = getSemanticSimWords(words1[i], words2[j]);
                                result[i][j] = score;
                        }
                }
                return result;
            //return MatrixCalculator.getNormalizedSimilarityMatrix( words1, words2,rc );
	}
	
	public void printMatrix(double[][] matrix){
		int i,j;
		for(i=0;i<matrix.length;i++){
			for(j=0;j<matrix[i].length;j++){
				System.out.print(matrix[i][j]+"\t");
			}
			System.out.println();
		}
                //System.out.println(matrix.length);
	}
        
        
        
       
}


