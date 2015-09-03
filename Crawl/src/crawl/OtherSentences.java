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
    public List<String> lemmatize(List<CoreMap> sentences){
        List<String> lemmas = new ArrayList<>();
            // Iterate over all of the sentences found 
            for(CoreMap sentence: sentences) {
                // Iterate over all tokens in a sentence
                for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                    // Retrieve and add the lemma for each word into the list of lemmas
                    String lemma=token.get(LemmaAnnotation.class);
                    lemma=lemmaPostProcess(lemma);
                    Pattern p = Pattern.compile("[a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                    Matcher m = p.matcher(lemma);
                    if (m.find()){
                        lemmas.add(lemma);
                    }
                         
                }
            }
            return lemmas;
    }
    public void getSimilar(String compare, String compareWith){
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
    similarity.printMatrix(similarity.buildMatrix(lemma1, lemma2));  
    }
}
