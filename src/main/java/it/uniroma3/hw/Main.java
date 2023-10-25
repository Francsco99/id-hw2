package it.uniroma3.hw;

public class Main {
    // Specifica il percorso in cui verranno creati gli indici
   final static String INDEX_PATH = System.getProperty("user.dir")+"/index";
    // Specifica il percorso della directory contenente i file .txt da indicizzare
   final static String DATA_PATH = System.getProperty("user.dir")+"/data";
    public static void main(String[] args) throws org.apache.lucene.queryparser.classic.ParseException{

        Indexer i = new Indexer();
        i.readFile(INDEX_PATH, DATA_PATH);

        Searcher s = new Searcher();
        s.eseguiQuery(INDEX_PATH);

    }
}