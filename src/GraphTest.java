import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileNotFoundException;
import static org.junit.jupiter.api.Assertions.*;
class GraphTest {
    Graph my_graph = new Graph();
    File file = new File("test.txt");
    public GraphTest(){
        try{
            my_graph.readTextAndGenerateGraph(file);
        }
        catch(FileNotFoundException e){
            System.out.println("文件不存在");
        }
    }
    @Test
    void queryBridgeWords_allnormal() {
        String res = my_graph.queryBridgeWords("in","legends");
        assertEquals(res,"apex");
        res = my_graph.queryBridgeWords("in","apex");
        assertEquals(res,"");
        res = my_graph.queryBridgeWords("ad","legends");
        assertNull(res);
        res = my_graph.queryBridgeWords("in","wd");
        assertNull(res);
        res = my_graph.queryBridgeWords("ad","wd");
        assertNull(res);
        res = my_graph.queryBridgeWords("in","in");
        assertEquals(res,"");
    }
    @Test
    void queryBridgeWords_word1normal_and_word2null(){
        String res = my_graph.queryBridgeWords("in","");
        assertNull(res);
    }
    @Test
    void queryBridgeWords_word1null_and_word2normal(){
        String res = my_graph.queryBridgeWords("","legends");
        assertNull(res);
    }
    @Test
    void queryBridgeWords_word1invalid_and_word2normal(){
        String res = my_graph.queryBridgeWords("44in","legends");
        assertNull(res);
    }
    @Test
    void queryBridgeWords_word1normal_and_word2invalid(){
        String res = my_graph.queryBridgeWords("in","44legends");
        assertNull(res);
    }

}