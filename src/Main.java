import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.io.File;
import java.util.List;

import com.mxgraph.layout.*;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxCellRenderer;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.*;
import javax.imageio.ImageIO;


import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.graph.DefaultWeightedEdge;
import javax.swing.*;


public class Main {
    private static Map<String, Map<String, Integer>> graph = new HashMap<>();
    private static Map<String, Integer> wordFrequency;

    public static void main(String[] args) {
        // 实验要求main函数接收用户输入文件，生成图，并允许用户选择后续各项功能

        if (args.length > 0) {
            // args[0] 是第一个参数，这里我们假设它是一个文件名
            String fileName = args[0];
            // 接下来可以根据文件名进行操作，例如读取文件等
            System.out.println("File name provided: " + fileName);

            // 确保文件存在
            File file = new File(fileName);
            if (file.exists()) {
                System.out.println("The file exists: " + fileName);
                try {
                    readTextAndGenerateGraph(file);
                } catch (FileNotFoundException e) {
                    System.out.println("The file does not exist or is a directory: " + fileName);
                }
            } else {
                System.out.println("The file does not exist or is a directory: " + fileName);
                System.exit(0);
            }
        } else {
            System.out.println("Please provide a file name as a command line argument.");
            System.exit(0);
        }
        Scanner read_select = new Scanner(System.in);
        Scanner in = new Scanner(System.in);
        System.out.println("已完成数据读入并构建图");
        while(true){
            System.out.println("请选择功能");
            System.out.println("0：退出程序");
            System.out.println("1：展示图结构");
            System.out.println("2：查询桥接词");
            System.out.println("3：通过桥接词添加文本");
            System.out.println("4：计算最短路径");
            System.out.println("5：随机游走");
            int function_select = 0;

            function_select = read_select.nextInt();


            switch (function_select){
                case 0:System.exit(0);
                case 1:
                     try{showDirectedGraph();}catch(java.io.IOException e){
                         System.out.println("io异常");
                     }
                    break;
                case 2:
                    System.out.println("进入桥接词查询：");
                    in = new Scanner(System.in);
                    System.out.println("输入word1");
                    String word1 = in.next();
                    System.out.println("输入word2");
                    String word2 = in.next();
                    List BridgeWords_res = queryBridgeWords(word1,word2);
                    if(BridgeWords_res == null){
                        System.out.printf("No \"%s\" or \"%s\" in the graph!\n", word1, word2);
                    }
                    else if(BridgeWords_res.isEmpty()){
                        System.out.printf("No bridge words from \"%s\" to \"%s\"!\n",word1,word2);
                    }
                    else{
                        int size = BridgeWords_res.size();
                        System.out.printf("The bridge words from \"%s\" to \"%s\" are:",word1,word2);
                        for(int i = 0;i<size;i++){
                            System.out.print(BridgeWords_res.get(i));
                            if(i!=size-1)System.out.print(",");
                            else System.out.println(".");
                        }
                    }
                    break;
                case 3:
//                    if (in.hasNextLine()) {
//                        // 读取并丢弃换行符
//                        in.nextLine();
//                    }
                    System.out.println("输入要添加桥接词的文本");
                    String inputText = in.nextLine();
                    String res ;
                    res = generateNewText(inputText);
                    System.out.println(res);
                    break;
                case 4:
                    System.out.println("进入查找最短距离");
                    System.out.println("输入开始单词与结束单词（以空格分割）");
                    word1 = in.next();word2 = in.next();
                    res = calcShortestPath(word1,word2);
                    if(res.isEmpty()){
                        System.out.println("两点不可达");
                    }
                    else{
                        System.out.println(res);
                    }
                    break;
                case 5:
                    System.out.println("开始随机游走");
                    res = randomWalk();
                    System.out.println(res);
                    break;
                default:
                    System.out.println("输入非法");
                    break;
            }
        }

    }

    private static void readTextAndGenerateGraph(File file) throws FileNotFoundException {
        // 读取文本文件并生成有向图的代码
        Scanner scanner;
        scanner = new Scanner(file);
        String last_word = "\0";
        String current_word = "1";
        while (scanner.hasNext()) {
            String temp_word = scanner.next();
            String regex = "[^a-zA-Z]"; // 匹配所有非英文字母的单个字符
            current_word = temp_word.replaceAll(regex, "");
            current_word = current_word.toLowerCase();
            //System.out.println(current_word);
            if(last_word.equals("\0")){
                last_word = current_word;
            }
            else{
                if(graph.containsKey(last_word)){
                    if(graph.get(last_word).containsKey(current_word)){
                        Map<String, Integer> innerMap = graph.get(last_word);
                        innerMap.put(current_word,innerMap.get(current_word)+1);
                    }
                    else{
                        Map<String, Integer> innerMap = graph.get(last_word);
                        innerMap.put(current_word,1);
                    }
                }
                else{
                    Map<String,Integer> tmp = new HashMap<>();
                    tmp.put(current_word,1);
                    graph.put(last_word,tmp);
                }
                last_word = current_word;
            }

        }
        scanner.close();
    }

    public static void showDirectedGraph() throws IOException {
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
                jGraph.setEdgeWeight(e,edge.getValue());
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
        frame.setSize(400, 320);
        frame.setVisible(true);
    }

    public static List queryBridgeWords(String word1, String word2) {
        // 查询桥接词的代码
        List<String> res = new ArrayList<>() ;
        boolean flag = false;
        if(!graph.containsKey(word2)){
            for(String x:graph.keySet()){
                if(graph.get(x).containsKey(word2))flag = true;
            }
            if(!flag)return null;
        }
        if(!graph.containsKey(word1)   ){

            return null;
        }
        else{
            Map<String,Integer> temp1 = graph.get(word1);
            for(String s: temp1.keySet()){
                Map<String,Integer> temp2 = graph.get(s);
                if(temp2!=null && temp2.containsKey(word2)){
                    res.add(s);
                }
            }
        }
        return res;
    }


    public static String generateNewText(String inputText) {
        // 根据bridge word生成新文本的代码
        List<String> res = new ArrayList<>();
        List temp = new ArrayList<>();
        String[] words = inputText.split("\\s+");
        String last_word = "\0";
        String current_word;
        int i = 0;
        Random random = new Random();
        while (i<words.length) {
            current_word = words[i];
            current_word = current_word.toLowerCase();
            //System.out.println(current_word);
            if (last_word.equals("\0")) {
                last_word = current_word;
            } else {
                temp = queryBridgeWords(last_word,current_word);
                if(temp == null || temp.isEmpty()){
                    res.add(last_word);
                }else{

                    int random_num = random.nextInt(temp.size());
                    res.add(last_word);
                    res.add((String) temp.get(random_num));
                }
                last_word = current_word;
            }
            i++;
        }
        res.add(last_word);
        return res.toString();
    }

    public static String calcShortestPath(String word1, String word2) {
        // 计算两个单词之间的最短路径的代码
        Map<String, String> predecessors = new HashMap<>();
        int distance = dijkstra(word1, word2, predecessors);
        String res = "";
        if (distance != Integer.MAX_VALUE) {
            List<String> path = new ArrayList<>();
            for (String node = word2; node != null; node = predecessors.get(node)) {
                path.add(0, node);
            }

            for(String x:path){
                res += x;
                res += "->";
            }
            res = res.substring(0,res.length()-2);
        }
        return res;
    }

    private static int dijkstra(String start, String end, Map<String, String> predecessors) {
        Map<String, Integer> distances = new HashMap<>();
        Set<String> visited = new HashSet<>();
        distances.put(start, 0);

        while (!visited.contains(end)) {
            String current = null;
            int currentMinDistance = Integer.MAX_VALUE;

            // 选择当前最小距离的节点
            for (String node : graph.keySet()) {
                if (!visited.contains(node) && distances.getOrDefault(node, Integer.MAX_VALUE) < currentMinDistance) {
                    current = node;
                    currentMinDistance = distances.get(node);
                }
            }

            if (current == null) break; // 没有找到从start到end的路径

            visited.add(current);

            if (current.equals(end)) break; // 找到最短路径

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

        return distances.getOrDefault(end, Integer.MAX_VALUE);
    }


    public static String randomWalk() {
        // 随机游走的代码
        Random random = new Random();
        int size = graph.size();
        int random_num = random.nextInt(size);
        String res = "";
        String current_word = "",last_word = "";
        int i = 0;
//        Scanner if_continue = new Scanner(System.in);
        for(String x: graph.keySet()){
            if(i == random_num){
                current_word = x;
                break;
            }
            i++;
        }
        Map<String,String> visited = new HashMap<>();
        boolean stop_flag = false;
        while(!stop_flag){
            last_word = current_word;
            if(!graph.containsKey(current_word)){
                res+=current_word;
                break;
            }
            random_num = random.nextInt(graph.get(current_word).size());
            i = 0;
            for(String x: graph.get(current_word).keySet()){
                if(i == random_num){
                    if(visited.containsKey(last_word) && visited.get(last_word).equals(x)){
                        res+=last_word+"->"+x;
                        stop_flag = true;
                        break;
                    }
                    current_word = x;
                    res+=last_word+"->";
                    break;
                }
                i++;
            }
            visited.put(last_word,current_word);
//            System.out.println("是否停止游走（输入c继续，其他停止）");
//            String input = if_continue.next();
//            if(input.equals("c"));
//            else{
//                stop_flag = true;
//                res+=current_word;
//            }
        }
//        if_continue.close();
        return res;
    }


}