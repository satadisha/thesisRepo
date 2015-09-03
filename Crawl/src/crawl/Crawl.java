/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawl;

 
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import com.google.gson.Gson;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.Writer;
//import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import org.jsoup.Connection;
//import java.util.Locale;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 *
 * @author satadisha
 */
public class Crawl {

    /**
     * @param args the command line arguments
     */
    /*public static String findMatch(String snippetFrag1, String fileText){
        String res="";
        String snippetFrag=snippetFrag1.trim();
        //System.out.println(snippetFrag);
        String lineText;
                BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
                iterator.setText(fileText);
                int start = iterator.first();
                for (int end = iterator.next();end != BreakIterator.DONE;start = end, end = iterator.next()) {
                    lineText=fileText.substring(start,end-1);
                    
                    //System.out.println(lineText);
                    if(lineText.contains(snippetFrag)||lineText.contentEquals(snippetFrag)){
                            //System.out.println("Match found"+lineText+"\n");
                            res=lineText;
                            //System.out.println("aaaaaaaaaa");
                            break;
                        }
                }
        return res;
    }*/
    public static String sentencePostProcess(String stringIn){
      if(stringIn.contains("-LRB- "))
          stringIn=stringIn.replaceAll("-LRB- ", "(");
      if(stringIn.contains("-LSB- "))
          stringIn=stringIn.replaceAll("-LSB- ", "[");
      if(stringIn.contains("-LCB- "))
          stringIn=stringIn.replaceAll("-LCB- ", "{");
      if(stringIn.contains(" -RCB-"))
          stringIn=stringIn.replaceAll(" -RCB-", "}");
      if(stringIn.contains(" -RRB-"))
          stringIn=stringIn.replaceAll(" -RRB-", ")");
      if(stringIn.contains(" -RSB-"))
          stringIn=stringIn.replaceAll(" -RSB-", "]");
      /*if(stringIn.endsWith("."))
          stringIn=stringIn.substring(0, stringIn.length()-2)+".";*/               
      return stringIn;  
    }
    public static String findSnippetInFile(String snippet, String filenameIn){
        String sentenceRes="";
        String sentenceSim="";
        OtherSentences other=new OtherSentences();
        try{
		
            //if(br!=null){
                String fileText="";
                List<String> snippetFragList = new ArrayList<String>();
                List<String> snippetFragTempList = new ArrayList<String>();
                /*BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
                iterator.setText(snippet);
                int start = iterator.first();*/
                if((snippet.contains(" ... "))||(snippet.contains(" ...."))){
                    snippet=snippet.replaceAll(" \\..[.]+ ","%ss%");
                    snippetFragTempList=Arrays.asList(snippet.split("%ss%"));
                    //System.out.println("Modified to "+snippet);
                }
                else{
                    snippetFragTempList.add(snippet);
                }
                  
                for(String snippetTemp: snippetFragTempList){
                    Reader reader = new StringReader(snippetTemp);
                    DocumentPreprocessor dp = new DocumentPreprocessor(reader);
                    //List<String> snippetFragList = new ArrayList<String>();

                    for (List<HasWord> snippetFrag : dp) {
                       String snippetFragString = Sentence.listToString(snippetFrag);
                       //if(snipper)
                       snippetFragString=snippetFragString.substring(0, snippetFragString.length());
                       snippetFragList.add(snippetFragString.toString());
                    }
                }

		  for (String snippetFrag : snippetFragList) {
                      //HERE
                      snippetFrag.trim();
                      //if(snippetFrag.endsWith(" ")||snippetFrag.endsWith(".")||snippetFrag.endsWith("?")||snippetFrag.endsWith("!"))
                      //   snippetFrag=snippetFrag.substring(0, snippetFrag.length()-1);
                      //snippetFrag.trim();
                      snippetFrag=sentencePostProcess(snippetFrag);
                      //System.out.println("->"+snippetFrag);
                      //System.out.println("->"+snippetFrag);
                      FileReader fr=new FileReader(filenameIn);
                      DocumentPreprocessor filedp = new DocumentPreprocessor(fr);
                      List<String> sentenceFileList = new ArrayList<String>();
                      for (List<HasWord> sentenceFile : filedp) {
                         String sentenceFileString = Sentence.listToString(sentenceFile);
                         sentenceFileList.add(sentenceFileString.toString());
                      }
                      for (String sentenceFile : sentenceFileList) {
                          sentenceFile=sentencePostProcess(sentenceFile);
                          //System.out.println("Bef: "+sentenceFile);
                          if((sentenceFile.contains(snippetFrag))||(sentenceFile.contentEquals(snippetFrag))){
                              //
                              //System.out.println("=>"+sentenceFile);
                              sentenceRes+=sentenceFile+"\n";
                              break;
                          }
                          
                        }
		  }
               
	}
	catch(FileNotFoundException E){
			System.out.println("Could not download file "+filenameIn);
                        sentenceRes="NF";
		} 
        return sentenceRes;
    }
    
    public static void main(String[] args) throws IOException {
        int result_count=0;
       List <String> result = new ArrayList<String>();
        //for (int i = 0; i < 4; i = i + 4) {
            
            //String address = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&start="+i+"&q=";
            /*
            Calories in a Big Mac
            Who invented Electric Guitar
            Fastest Swimmer in the World
           */
            String query = "Calories in a Big Mac";
            String charset = "UTF-8";
            String request = "https://www.google.com/search?q=" + query + "&num=100";
            
            Document doc = Jsoup.connect(request).userAgent("Chrome(compatible; Googlebot/2.1; +http://www.google.com/bot.html)").timeout(5000).get();
            String doctitle = doc.title();
            //System.out.println(doc.toString());
            File file=new File(doctitle+".html");
            String text=doc.html();
            Writer writer= new BufferedWriter(new FileWriter(file));
            writer.write(text);
            writer.close();
            Elements snippets= new Elements();
            Elements classes = doc.select("li[class=g]");
            Elements resultUrls= new Elements();
		for (Element gclass : classes) {
                    Elements resultLinks = gclass.select("h3.r > a");
                    //Elements resultLinks = gclass.getElementsByAttribute("h3.r > a");
                    for(Element resultlink: resultLinks){
                        Elements links = resultlink.getElementsByAttribute("href");
                                    //String temp = link.attr("href");	
                                    for(Element link: links){
                                        
                                        String temp= link.attr("href");
                                        if((temp.startsWith("/url?q="))){
                                            
                                            //System.out.println(result_count+"->"+temp);
                                            resultUrls.add(link);
                                         for(Element snip: gclass.select("span[class=st]")) // gclass.select("span[class=st]");
                                         snippets.add(snip);   
                                        }
                                        
                                    }
                        }
                   
            }
            //System.out.println(resultUrls.size());
            //System.out.println(snippets.size());
            /*
            for(int i=0;i<resultUrls.size();i++){
                try{
                    //System.out.println("In loop "+i);
                    //System.out.println((i+1)+". "+resultUrls.get(i).absUrl("href")+"->"+snippets.get(i).text());
                    //System.out.println((27)+". "+resultUrls.get(27).absUrl("href")+"->"+snippets.get(27).text());
                    //URL u = new URL(resultUrls.get(0).absUrl("href"));
                    Connection.Response cr= Jsoup.connect(resultUrls.get(i).absUrl("href")).userAgent("Chrome(compatible; Googlebot/2.1; +http://www.google.com/bot.html)").timeout(30000).execute();
                    Document resdoc = Jsoup.parse(cr.body(), "ISO-8859-1");
                    //System.out.println(resultUrls.get(i).absUrl("href"));
                    //System.out.println("\n\n"+resdoc.title());
                    File resfile=new File(query+i+".txt");
                    Writer reswriter= new BufferedWriter(new FileWriter(resfile));
                    reswriter.write(resdoc.title());
                    reswriter.write(resdoc.text());
                    reswriter.flush();
                    reswriter.close();
                    //System.out.println("Trying again "+i);
            }
                catch(IOException E){System.out.println("Exception caught");
                //i++;
                }
            }
            
            List <String> snippetArray=new ArrayList<String>();
            List <String> originalSentenceArray=new ArrayList<String>();
            System.out.println(snippets.size());
            for(int i=0;i<snippets.size();i++){
                String snippetText=snippets.get(i).text();
                //System.out.println((i+1)+". "+snippets.get(i).text());
                if(snippets.get(i).text().endsWith("...")){
                    //System.out.println(snippets.get(i).text().indexOf("..."));
                    snippetText=snippets.get(i).text().substring(0,snippets.get(i).text().lastIndexOf("...")-4);
                }
                snippetArray.add(snippetText);
                //System.out.println(snippetText);
            }
            //System.out.println(snippetArray.size());
            
            for(int i=0;i<snippetArray.size();i++){
                System.out.println((i)+". "+snippetArray.get(i));
                System.out.println("--->");
                String originalSentence=findSnippetInFile(snippetArray.get(i),query+i+".txt");
                if(!(originalSentence.contentEquals("NF"))){
                    System.out.println(originalSentence);
                    originalSentenceArray.add(originalSentence);
                }
                    
            }
        */
     //-----------------Snippets and and original sentences computed. Now moving on to forming the text base------------------------
        //String compare= "There are 549 calories in 1 burger (7.6 oz) of McDonald's Big Mac Burger.";
        String compare= "The text that tells you the name of an item -- say , a Big Mac -- is about double the size of that telling you the number of calories (in this case , 550) .";
        String compareWith= "`` Two all beef patties , special sauce , lettuce , cheese , pickles , and onions on a sesame seed bun '' proclaims the advertisements , but what the advertisements do n't proclaim is how many calories are in a Big Mac .\n" +
"";
        OtherSentences others=new OtherSentences();
        others.getSimilar(compare, compareWith);
    }
}
