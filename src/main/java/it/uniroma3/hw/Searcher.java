package it.uniroma3.hw;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
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

    public void eseguiQuery(String indexPath){
        Scanner scanner = new Scanner(System.in);
        try{
            // Directory degli indici
            Directory directory = FSDirectory.open(Paths.get(indexPath));
            // Creo un reader per accedere agli indici
            IndexReader indexReader = DirectoryReader.open(directory);
            // Creo un searcher per cercare tra gli indici
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);

            // Messaggio per l'utente
            System.out.println("Inserisci la tua query (esempio: 'titolo:pippo' o 'contenuto:pluto'):\n");
            // Input dell'utente
            String inputUtente = scanner.nextLine();

            //Dividi la stringa in due sottostringhe separate dai :
            String[] parts = inputUtente.split(":");
            String campo = new String();
            String frase = new String();

            Searcher s = new Searcher();

            if(parts.length == 2){
                campo = parts[0].trim();
                frase =parts[1].trim();
            }
            else {
                System.out.println("Stringa non valida. Deve essere nel formato 'titolo:pippo' o 'contenuto:pluto'.");
            }
            PhraseQuery.Builder builder = new PhraseQuery.Builder();
            for(String parola : frase.split(" ")){
                Term term = new Term(campo,parola);
                builder.add(term);
            }
            PhraseQuery query = builder.build();
            s.insertQuery(indexPath,query,false);



        }catch(Exception e){
            e.printStackTrace();
        }
    }
}