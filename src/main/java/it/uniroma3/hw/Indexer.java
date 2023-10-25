package it.uniroma3.hw;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Indexer {
    // Metodo per indicizzare un singolo file
    private void indexFile(IndexWriter writer, File file) {

        try {
            // Creare un documento Lucene
            Document doc = new Document();

            // Indicizzare il nome del file come TextField
            String nomeFile = file.getName().replaceFirst("\\.txt$", ""); // Rimuovo quello che è dopo il .txt
            Field titolo = new TextField("titolo", nomeFile, Field.Store.YES);
            doc.add(titolo);

            // Indicizzare il contenuto del file come TextField
            Field contenuto = new TextField("contenuto", new FileReader(file));
            doc.add(contenuto);

            // Aggiungere il documento all'indice
            writer.addDocument(doc);
            writer.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Legge tutti i file nella cartella dataPath
    private void readDirectory(String dataPath, IndexWriter writer) {
        File fileDaLeggere = new File(dataPath);
        if (fileDaLeggere.isDirectory()) {
            File[] files = fileDaLeggere.listFiles();
            System.out.println(files.length);

            if (files != null) {
                for (File file : files) {
                    long file_Start_Time = System.currentTimeMillis(); // faccio partire il tempo
                    if (file.getName().endsWith(".txt")) {
                        indexFile(writer, file);
                        long file_End_Time = System.currentTimeMillis(); // fermo il tempo
                        System.out.println("Letto file: " + file.getName() + " " + (file_End_Time - file_Start_Time) + " ms");

                    }
                }
            }
        }
    }

    public void readFile(String indexPath, String dataPath) {

        try {
            // Mappa con analyzer specifici
            Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
            perFieldAnalyzers.put("contenuto", new StandardAnalyzer());
            perFieldAnalyzers.put("titolo", new WhitespaceAnalyzer());

            // Wrapper per usare analyzer specifici a seconda dei fields
            Analyzer analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), perFieldAnalyzers);
            IndexWriterConfig config = new IndexWriterConfig(analyzer);

            // Creare un oggetto Directory per la gestione dell'indice su disco
            Directory directory = FSDirectory.open(Paths.get(indexPath));

            IndexWriter writer = new IndexWriter(directory, config);

            // Cancella tutto quello che era presente nella cartella degli indici
            writer.deleteAll();

            // Itera su tutti i file della cartella
            readDirectory(dataPath, writer);

            // Chiudere l'IndexWriter
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}