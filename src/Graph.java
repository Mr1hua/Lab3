import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
@SuppressWarnings({"checkstyle:Indentation", "checkstyle:WhitespaceAfter"})

public class Graph {
    private static final Map<String, Map<String, Integer>> graph = new HashMap<>();
    Random random  = new Random();

    public void readTextAndGenerateGraph(File file) throws FileNotFoundException {
        // 读取文本文件并生成有向图的代码
        InputStream inputStream = new FileInputStream(file); // 使用 FileInputStream 来读取文件
        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
        String last_word = "\0";
        String current_word = "1";
        while (scanner.hasNext()) {
            String tempword = scanner.next();
            String regex = "[^a-zA-Z]"; // 匹配所有非英文字母的单个字符
            current_word = tempword.replaceAll(regex, "");
            current_word = current_word.toLowerCase();
            //System.out.println(current_word);
            if (last_word.equals("\0")) {
                last_word = current_word;
            } else {
                if (graph.containsKey(last_word)) {
                    if (graph.get(last_word).containsKey(current_word)) {
                        Map<String, Integer> innerMap = graph.get(last_word);
                        innerMap.put(current_word,innerMap.get(current_word)+1);
                    } else {
                        Map<String, Integer> innerMap = graph.get(last_word);
                        innerMap.put(current_word,1);
                    }
                } else {
                    Map<String, Integer> tmp = new HashMap<>();
                    tmp.put(current_word, 1);
                    graph.put(last_word, tmp);
                }
                last_word = current_word;
            }

        }
        scanner.close();
    }

    public  void showDirectedGraph() throws IOException {
        // 展示有向图的代码
        DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> jGraph =
                new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);

        // 将嵌套Map转换为JGraphT图
        for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
            String source = entry.getKey();
            jGraph.addVertex(source);
            for (Map.Entry<String, Integer> edge : entry.getValue().entrySet()) {
                String target = edge.getKey();
                jGraph.addVertex(target);
                DefaultWeightedEdge e = jGraph.addEdge(source, target);
                jGraph.setEdgeWeight(e, edge.getValue());
            }
        }
        JGraphXAdapter<String, DefaultWeightedEdge> graphAdapter = new JGraphXAdapter<>(jGraph) {
            @Override
            public String convertValueToString(Object cell) {
                if (cell instanceof mxCell) {
                    mxCell mxCell = (mxCell) cell;
                    Object value = mxCell.getValue();
                    if (mxCell.isEdge()) {
                        DefaultWeightedEdge edge = (DefaultWeightedEdge) value;
                        return String.valueOf(jGraph.getEdgeWeight(edge));
                    }
                }
                return super.convertValueToString(cell);
            }
        };

        mxIGraphLayout layout = new mxFastOrganicLayout(graphAdapter);

        layout.execute(graphAdapter.getDefaultParent());
        BufferedImage image =
                mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File("graph.png");
        ImageIO.write(image, "PNG", imgFile);

        graphAdapter.setCellsEditable(false);
        graphAdapter.setAllowDanglingEdges(false);
        graphAdapter.setCellsDisconnectable(false);
        graphAdapter.setEdgeLabelsMovable(false);

        JFrame frame = new JFrame();
        frame.getContentPane().add(new mxGraphComponent(graphAdapter));
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }

    public  String queryBridgeWords(String word1, String word2) {
        // 查询桥接词的代码
        String res = "";
        boolean flag = false;
        if (!graph.containsKey(word2)) {
            for (Map<String, Integer> x : graph.values()) {
                if (x.containsKey(word2)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                return null;
            }
        }
        if (!graph.containsKey(word1)) {
            return null;
        } else {
            Map<String, Integer> temp1 = graph.get(word1);
            for (String s : temp1.keySet()) {
                Map<String, Integer> temp2 = graph.get(s);
                if (temp2 != null && temp2.containsKey(word2)) {
                    res += s;
                    res += ",";
                }
            }
        }
        res =res.length()>1? res.substring(0,res.length()-1):res;
        return res;
    }




    public String generateNewText(String inputText) {
        // 根据bridge word生成新文本的代码
        String res = "";
        String bridgewords;
        String[] words = inputText.split("\\s+");
        String last_word = "\0";
        String current_word;
        int i = 0;
        while (i < words.length) {
            current_word = words[i];
            current_word = current_word.toLowerCase();
            //System.out.println(current_word);
            if(i == 0 ) {
                last_word = current_word;
            } else {
                bridgewords = queryBridgeWords(last_word, current_word);

                if (bridgewords == null || bridgewords.isEmpty()) {
                    res += last_word+" ";
                } else {
                    String[] bridgewords_array = bridgewords.split(",");
                    res += last_word+" ";
                    res += bridgewords_array[random.nextInt(bridgewords_array.length)]+" ";
                }
                last_word = current_word;
            }
            i++;
        }
        res += last_word;
        return res;
    }

    public String calcShortestPath(String word1, String word2) {
        Map<String, String> predecessors = new HashMap<>();
        Map<String, Integer> distances = new HashMap<>();
        Set<String> visited = new HashSet<>();
        distances.put(word1, 0);
        while (!visited.contains(word2)) {
            String current = null;
            int currentMinDistance = Integer.MAX_VALUE;
            for (String node : graph.keySet()) {
                if (!visited.contains(node) && distances.getOrDefault(node, Integer.MAX_VALUE) < currentMinDistance) {// 选择当前最小距离的节点
                    current = node;
                    currentMinDistance = distances.get(node);
                }
            }
            if (current == null) { break;} // 没有找到从start到end的路径
            visited.add(current);
            if (current.equals(word2)) {break;} // 找到最短路径
            for (Map.Entry<String, Integer> entry : graph.get(current).entrySet()) {
                String neighbor = entry.getKey();
                int edgeWeight = entry.getValue();
                int newDistance = currentMinDistance + edgeWeight;
                if (newDistance < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distances.put(neighbor, newDistance);
                    predecessors.put(neighbor, current);
                }
            }
        }
        int distance = distances.getOrDefault(word2, Integer.MAX_VALUE);
        String res = "";
        if (distance != Integer.MAX_VALUE) {
            List<String> path = new ArrayList<>();
            for (String node = word2; node != null; node = predecessors.get(node)) {
                path.add(0, node);
            }
            for (String x : path) {
                res += x;
                res += "->";
            }
            res = res.substring(0, res.length() - 2);
        }
        return res;
    }

    public String randomWalk() {
        // 随机游走的代码
        int size = graph.size();
        int random_num = random.nextInt(size);
        String res = "";
        String current_word = "", last_word = "";
        int i = 0;
        //        Scanner if_continue = new Scanner(System.in);
        for (String x : graph.keySet()) {
            if (i == random_num) {
                current_word = x;
                break;
            }
            i++;
        }
        Map<String, String> visited = new HashMap<>();
        boolean stop_flag = false;
        while (!stop_flag) {
            last_word = current_word;
            if (!graph.containsKey(current_word)) {
                res += current_word;
                break;
            }
            random_num = random.nextInt(graph.get(current_word).size());
            i = 0;
            for (String x: graph.get(current_word).keySet()) {
                if (i == random_num) {
                    if (visited.containsKey(last_word) && visited.get(last_word).equals(x)) {
                        res += last_word + "->" + x;
                        stop_flag = true;
                        break;
                    }
                    current_word = x;
                    res += last_word+"->";
                    break;
                }
                i++;
            }
            visited.put(last_word, current_word);
//            System.out.println("是否停止游走（输入c继续，其他停止）");
//            String input = if_continue.next();
//            if(input.equals("c"))break;
//            else{
//                stop_flag = true;
//                res+=current_word;
            //            }
        }
        //        if_continue.close();
        return res;
    }
}

