package it.uniroma3.hw;

public class Main {

    public static void main(String[] args) throws org.apache.lucene.queryparser.classic.ParseException{
        // Specifica il percorso in cui verranno creati gli indici
        String indexPath = System.getProperty("user.dir")+"/index";
        // Specifica il percorso della directory contenente i file .txt da indicizzare
        String dataPath = System.getProperty("user.dir")+"/data";

        Indexer i = new Indexer();
        i.readFile(indexPath,dataPath);
		/*
		 Searcher s = new Searcher();

		 s.consoleQuery(indexPath);
		 /*
		 * QueryParser parser = new QueryParser("contenuto", new WhitespaceAnalyzer());
		 * Query query = parser.parse("luogo"); s.insertQuery(indexPath,query,false);
		 */

    }
}