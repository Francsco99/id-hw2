import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.lucene.document.Document;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class QueryTest {
    // Specifica il percorso in cui verranno creati gli indici
    final static String INDEX_PATH = System.getProperty("user.dir")+"/index";
    private IndexSearcher searcher;
    private Directory indexDirectory;

    @Before
    public void setup() throws Exception{
        this.indexDirectory = FSDirectory.open(Paths.get(INDEX_PATH));
        this.searcher = new IndexSearcher(DirectoryReader.open(indexDirectory));
    }

    @After
    public void end() throws Exception{
        indexDirectory.close();
    }
    @Test
    public void termQueryAllDocs() throws Exception{
        TermQuery query = new TermQuery(new Term("contenuto","luogo"));
        TopDocs hits = searcher.search(query,10);

        assertEquals(DirectoryReader.open(indexDirectory).maxDoc(),hits.scoreDocs.length);
    }

    @Test
    public void termQuerySingleDoc() throws Exception{
        TermQuery query = new TermQuery(new Term("contenuto","italia"));
        TopDocs hits = searcher.search(query,10);

        assertEquals(1,hits.scoreDocs.length);
        Document match = searcher.doc(hits.scoreDocs[0].doc);

        assertEquals("L'italia",match.get("titolo"));
    }

    @Test
    public void termQueryNoDoc() throws Exception{
        TermQuery query = new TermQuery(new Term("contenuto","pipistrello"));
        TopDocs hits = searcher.search(query,10);

        assertEquals(0,hits.scoreDocs.length);
    }

    @Test
    public void phraseQuerySingleDoc() throws Exception{
        PhraseQuery query = new PhraseQuery.Builder()
                .add(new Term("contenuto","una"))
                .add(new Term("contenuto","sinfonia"))
                .add(new Term("contenuto","unica"))
                .build();

        TopDocs hits = searcher.search(query,10);
        assertEquals(1,hits.scoreDocs.length);

        Document match = searcher.doc(hits.scoreDocs[0].doc);

        assertEquals("Città di notte",match.get("titolo"));
    }

    @Test
    public void phraseQueryMultipleDocs() throws Exception{
        PhraseQuery query = new PhraseQuery.Builder()
                .add(new Term("contenuto","punti"))
                .add(new Term("contenuto","di"))
                .add(new Term("contenuto","luce"))
                .build();
        TopDocs hits = searcher.search(query,10);
        assertEquals(2,hits.scoreDocs.length);

        Document firstMatch = searcher.doc(hits.scoreDocs[0].doc);
        Document secondMatch = searcher.doc(hits.scoreDocs[1].doc);

        assertEquals("Notte silenziosa",firstMatch.get("titolo"));
        assertEquals("Atmosfera di magia",secondMatch.get("titolo"));
    }

}
