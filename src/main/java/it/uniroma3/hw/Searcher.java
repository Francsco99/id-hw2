package it.uniroma3.hw;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class Searcher {
    final String MESSAGGIO_ERRORE = "Stringa non valida. Deve essere nel formato 'titolo:pippo' o 'contenuto:pluto'.";
    final String MESSAGGIO_INSERIMENTO = "Inserisci la tua query (esempio: 'titolo:parole nel titolo' o 'contenuto:parole nel contenuto'):";

    public void insertQuery(String indexPath, Query query, Boolean explain) {
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
                System.out.println("doc" + scoreDoc.doc + ":" + doc.get("titolo") + " (" + scoreDoc.score + ")");
                if (explain) {
                    Explanation explanation = indexSearcher.explain(query, scoreDoc.doc);
                    System.out.println(explanation);
                }
            }

        } catch (IOException e) {
        }
    }

    public void eseguiQuery(String indexPath) {
        //Scanner per input utente
        Scanner scanner = new Scanner(System.in);

        try {
            // Directory degli indici
            Directory directory = FSDirectory.open(Paths.get(indexPath));
            // Creo un reader per accedere agli indici
            IndexReader indexReader = DirectoryReader.open(directory);
            // Creo un searcher per cercare tra gli indici
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);

            // Booleano che indica se vogliamo continuare ad inserire query o no
            Boolean uscita = true;

            // Ciclo per gestire l'input dell'utente
            do {
                String inputUtente;
                // Richiedi all'utente di inserire la query nel formato corretto
                while (true) {
                    System.out.println(MESSAGGIO_INSERIMENTO);
                    inputUtente = scanner.nextLine();
                    if (inputUtente.matches("^(titolo|contenuto):.+")) {
                        break; // L'input è nel formato corretto, esci dal ciclo
                    } else {
                        System.out.println(MESSAGGIO_ERRORE);
                    }
                }

                // Dividi la stringa in due sottostringhe separate dai :
                String[] parts = inputUtente.split(":");
                String campo = ""; // titolo o contenuto
                String testo = ""; // testo della query

                campo = parts[0].trim();
                testo = parts[1].trim();
                PhraseQuery.Builder builder = new PhraseQuery.Builder();
                for (String parola : testo.split(" ")) {
                    Term term = new Term(campo, parola);
                    builder.add(term);
                }
                PhraseQuery query = builder.build();

                // Nuovo oggetto Searcher per invocare metodo insertQuery
                Searcher s = new Searcher();
                s.insertQuery(indexPath, query, false);

                System.out.println("Vuoi eseguire un altra query? (y/n)");
                inputUtente = scanner.nextLine();
                if (inputUtente.equals("n")) {
                    uscita = false;
                }
            } while (uscita);// Cicla finchè l'utente non vuole uscire

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}