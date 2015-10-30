/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawl;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.util.CoreMap;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author satadisha
 */
public class OtherSentences {
    // creates a StanfordCoreNLP object, with POS tagging, lemmatization
    public Properties props;
    StanfordCoreNLP pipeline;
    public ArrayList objMatrix= new ArrayList<ArrayList<SimIndex>>();
    
    public String lemmaPostProcess(String stringIn){
      if(stringIn.contains(("-lrb-")))
          stringIn=stringIn.replaceAll("-lrb-", "(");
      if(stringIn.contains("-lsb-"))
          stringIn=stringIn.replaceAll("-lsb-", "[");
      if(stringIn.contains("-lcb-"))
          stringIn=stringIn.replaceAll("-lcb-", "{");
      if(stringIn.contains("-rcb-"))
          stringIn=stringIn.replaceAll("-rcb-", "}");
      if(stringIn.contains("-rrb-"))
          stringIn=stringIn.replaceAll("-rrb-", ")");
      if(stringIn.contains("-rsb-"))
          stringIn=stringIn.replaceAll("-rsb-", "]");
      /*if(stringIn.endsWith("."))
          stringIn=stringIn.substring(0, stringIn.length()-2)+".";*/               
      return stringIn;  
    }
    
    public OtherSentences(){
        props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        props.put("tokenize.normalizeParentheses", "false");
        props.put("tokenize.normalizeOtherBrackets", "false");
        pipeline = new StanfordCoreNLP(props);  
    }
      
    public List<CoreMap> annotate(String sentenceText){
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(sentenceText);
        pipeline.annotate(document);
        List<CoreMap> sentences= document.get(SentencesAnnotation.class);
        return sentences;
    }
    public List<String> tokenize(List<CoreMap> sentences){
            List<String> tokens = new ArrayList<>();
            // Iterate over all of the sentences found 
            for(CoreMap sentence: sentences) {
                // Iterate over all tokens in a sentence
                for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                    // Retrieve and add the word for each word into the list of words
                    tokens.add(token.word());
                }
            }
            return tokens;
    }
    //removes stop word and lemmatizes
    public List<String> lemmatize(List<CoreMap> sentences){
        List<String> stopwordsLucene= new ArrayList<String>(Arrays.asList("a", "an", "and", "are", "as", "at", "be", "but", "by","for", "if", "in", "into", "is", "it",
                           "no", "not", "of", "on", "or", "such","that", "the", "their", "then", "there", "these","they", 
                            "this", "to", "was", "will", "with"));
        List<String> lemmas = new ArrayList<>();
            // Iterate over all of the sentences found 
            for(CoreMap sentence: sentences) {
                // Iterate over all tokens in a sentence
                for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                    // Retrieve and add the lemma for each word into the list of lemmas
                    String lemma=token.get(LemmaAnnotation.class);
                    lemma=lemmaPostProcess(lemma);
                    if(!(stopwordsLucene.contains(lemma))){
                      Pattern p = Pattern.compile("[a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                        Matcher m = p.matcher(lemma);
                        if (m.find()){
                            lemmas.add(lemma);
                        }  
                    }                                             
                }
            }
            return lemmas;
    }
    
    public void createObjMatrix(double[][] matrix){
            int row= matrix.length;
            int col= matrix[0].length;
            
          // System.out.println("matrix.length:"+matrix.length);
          // System.out.println("matrix[0].length:"+matrix[0].length);
            ArrayList<SimIndex> rowArray=null;
            for(int i=0;i<row;i++){
                rowArray=new ArrayList<SimIndex>();
                
                int maxIndex=0;
                double maxValue=0.0;
                for(int j=0;j<col;j++){
                    double value= matrix[i][j];
                    if(maxValue<value){maxValue=value;maxIndex=j;}
                    SimIndex simIndex = new SimIndex();
                    simIndex.matchValue=value;
                    simIndex.selected=false;
                    rowArray.add(simIndex);
                }
                rowArray.get(maxIndex).selected=true;
                objMatrix.add(rowArray);
               //((SimIndex)(((ArrayList)objMatrix.get(i)).get(maxIndex))).selected=true; 
            }
            
        }
    public int resolveColumnCollision(int rowIndex, int colIndex){
        int row= objMatrix.size();
        int col= ((ArrayList)objMatrix.get(0)).size();
        boolean hasCollision=false;
        double indexValue=((SimIndex)(((ArrayList)objMatrix.get(rowIndex)).get(colIndex))).matchValue;
        for(int i=0;i<row;i++){
            if(i!=rowIndex){
                    double currentValue=((SimIndex)(((ArrayList)objMatrix.get(i)).get(colIndex))).matchValue;
                    boolean currentselected=((SimIndex)(((ArrayList)objMatrix.get(i)).get(colIndex))).selected;
                    if(indexValue<currentValue && currentselected==true){
                    ((SimIndex)(((ArrayList)objMatrix.get(rowIndex)).get(colIndex))).matchValue=-1.0;
                    ((SimIndex)(((ArrayList)objMatrix.get(rowIndex)).get(colIndex))).selected=false;
                    hasCollision=true;
                    break;
                    }  
            }
            
        }
        //fixing the row now.
        if(hasCollision){
            double maxValue=0.0;
            int maxIndex=0;     
                for(int i=0;i<col;i++){
                  double currentValue=((SimIndex)(((ArrayList)objMatrix.get(rowIndex)).get(i))).matchValue;  
                  if(currentValue>=maxValue)
                  {
                   maxValue=currentValue;
                   maxIndex=i;
                  }
                }
               ((SimIndex)(((ArrayList)objMatrix.get(rowIndex)).get(maxIndex))).selected=true; 
               //newMaxCol=maxIndex;
               //System.out.println();
               return resolveColumnCollision(rowIndex,maxIndex);
        }
       return colIndex; 
    }
    
     public double getScore(){
         
         
         double score=0.0;
            int row= objMatrix.size();
            int col= ((ArrayList)objMatrix.get(0)).size();
            System.out.println("=>"+row+"  "+col);
            
            for(int i=0;i<row;i++){ 
                for(int j=0;j<col;j++){
                  if(((SimIndex)(((ArrayList)objMatrix.get(i)).get(j))).selected==true){
                      //if(ColumnHasColllision(row, col))
                      int maxCol=resolveColumnCollision(i, j);
                      score+=((SimIndex)(((ArrayList)objMatrix.get(i)).get(maxCol))).matchValue;
                      
                  } 
                }
            }
            score=score/(row*(col-row+1));   
            return score;
        }
    
    public double getSimilar(String compare, String compareWith){
    SemanticSim similarity = new SemanticSim();
    
 // for the first sentence
    List<CoreMap> sentence1= annotate(compare);
    //List<String> words1=tokenize(sentence1);
    List<String> lemma1=lemmatize(sentence1);
    /*for(String token: words1){
    	System.out.print(token+"||");
    }*/
    System.out.println();
    for(String lemma: lemma1){
        //lemma=lemmaPostProcess(lemma);
    	System.out.print(lemma+", ");
    }
    System.out.println();
    
// for the second sentence    
    List<CoreMap> sentence2= annotate(compareWith);
    //List<String> words2=tokenize(sentence2);
    List<String> lemma2=lemmatize(sentence2);
    /*for(String words: words2){
    	System.out.print(words+"||");
    }*/
    System.out.println();
    for(String lemma: lemma2){
    	System.out.print(lemma+", ");
    }
    System.out.println();
    
    //calling buildMatrix for a print
    double[][] matrix=similarity.buildMatrix(lemma1, lemma2);
    
    similarity.printMatrix(matrix);
    createObjMatrix(matrix);
    double score= getScore();
    //System.out.println(score);
    return score;
    }
}
