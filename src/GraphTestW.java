import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class GraphTestW {
    Graph my_graph = new Graph();
    File file = new File("test.txt");
    public GraphTestW(){
        try{
            my_graph.readTextAndGenerateGraph(file);
        }
        catch(FileNotFoundException e){
            System.out.println("文件不存在");
        }
    }
    @Test
    void calcShortestPath_1() {
        String res = my_graph.calcShortestPath("in","players");
        assertEquals(res,"in->apex->legends->players");
    }
    @Test
    void calcShortestPath_2() {
        String res = my_graph.calcShortestPath("in","in");
        assertEquals(res,"in");
    }
    @Test
    void calcShortestPath_3() {
        String res = my_graph.calcShortestPath("ad","in");
        assertEquals(res,"");
    }
    @Test
    void calcShortestPath_4() {
        String res = my_graph.calcShortestPath("cosmetic","items");
        assertEquals(res,"cosmetic->items");
    }
    @Test
    void calcShortestPath_5() {
        String res = my_graph.calcShortestPath("in","ad");
        assertEquals(res,"");
    }
}