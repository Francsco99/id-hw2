package it.uniroma3.hw;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {

    public void insertQuery(String indexPath,Query query, Boolean explain) {
        try {
            Directory directory = FSDirectory.open(Paths.get(indexPath));
            // Creo un reader per accedere agli indici
            IndexReader indexReader = DirectoryReader.open(directory);
            // Creo un searcher per cercare tra gli indici
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);

            TopDocs hits = indexSearcher.search(query, 10);
            for (int i = 0; i < hits.scoreDocs.length; i++) {
                ScoreDoc scoreDoc = hits.scoreDocs[i];
                Document doc = indexSearcher.doc(scoreDoc.doc);
                System.out.println("doc"+scoreDoc.doc + ":"+ doc.get("titolo") + " (" + scoreDoc.score +")");
                if (explain) {
                    Explanation explanation = indexSearcher.explain(query, scoreDoc.doc);
                    System.out.println(explanation);
                }
            }

        }catch(IOException e) {

        }

    }

    public void consoleQuery(String indexPath) {
        Scanner scanner = new Scanner(System.in);

        //booleano per scegliere se inserire un'altra query
        boolean continuaQuery= true;

        try {
            // Directory degli indici
            Directory directory = FSDirectory.open(Paths.get(indexPath));
            // Creo un reader per accedere agli indici
            IndexReader indexReader = DirectoryReader.open(directory);
            // Creo un searcher per cercare tra gli indici
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);

            while(continuaQuery) {
                // Messaggio per l'utente
                System.out.println("Inserisci la tua query (esempio: 'titolo:pippo' o 'contenuto:pluto'):\n");
                // Input dell'utente
                String queryString = scanner.nextLine();
                // Nuovo searcher
                Searcher s = new Searcher();

                if (queryString.startsWith("titolo:")) {
                    String titolo = queryString.substring(7); // Estrai la parte dopo "titolo:"
                    QueryParser parser = new QueryParser("titolo", new WhitespaceAnalyzer());
                    Query query = parser.parse(titolo); s.insertQuery(indexPath,query,false);

                } else if (queryString.startsWith("contenuto:")) {
                    String contenuto = queryString.substring(10); // Estrai la parte dopo "contenuto:"
                    QueryParser parser = new QueryParser("contenuto", new StandardAnalyzer());
                    Query query = parser.parse(contenuto); s.insertQuery(indexPath,query,false);

                } else {
                    System.out.println("Formato non riconosciuto. Inserisci 'titolo:qualcosa' o 'contenuto:qualcosa'.");
                }

                // Ask the user if they want to enter another query
                System.out.println("Vuoi inserire un'altra query? (si/no)\n");
                String userInput = scanner.nextLine().toLowerCase();

                if (userInput.equals("no")) {
                    continuaQuery = false;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}