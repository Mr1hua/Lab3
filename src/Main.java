import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;





public class Main {
    static Graph my_graph = new Graph();

    @SuppressWarnings("checkstyle:Indentation")
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
                    my_graph.readTextAndGenerateGraph(file);
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
        while (true) {
            System.out.println("请选择功能");
            System.out.println("0：退出程序");
            System.out.println("1：展示图结构");
            System.out.println("2：查询桥接词");
            System.out.println("3：通过桥接词添加文本");
            System.out.println("4：计算最短路径");
            System.out.println("5：随机游走");
            int function_select;
            function_select = read_select.nextInt();
            switch (function_select) {
                case 0:
                    System.exit(0);
                    break;
                case 1:
                    try {
                        my_graph.showDirectedGraph();
                    } catch (java.io.IOException e) {
                        System.out.println("io异常");
                    }
                    break;
                case 2:
                    System.out.println("进入桥接词查询：");
                    in = new Scanner(System.in);
                    System.out.println("输入word1");
                    String word1 = in.nextLine();
                    System.out.println("输入word2");
                    String word2 = in.nextLine();
                    String BridgeWords_res = my_graph.queryBridgeWords(word1, word2);
                    if (BridgeWords_res == null) {
                        System.out.printf("No \"%s\" or \"%s\" in the graph!%n", word1, word2);
                    } else if (BridgeWords_res.isEmpty()) {
                        System.out.printf("No bridge words from \"%s\" to \"%s\"!%n", word1, word2);
                    } else {
                        int size = BridgeWords_res.length();
                        System.out.printf("The bridge words from \"%s\" to \"%s\" are:", word1, word2);
                        System.out.println(BridgeWords_res);
                    }
                    break;
                case 3:
//                    if (in.hasNextLine()) {
//                        // 读取并丢弃换行符
//                        in.nextLine();
//                    }
                    System.out.println("输入要添加桥接词的文本");
                    String inputText = in.nextLine();
                    String res;
                    res = my_graph.generateNewText(inputText);
                    System.out.println(res);
                    break;
                case 4:
                    System.out.println("进入查找最短距离");
                    System.out.println("输入开始单词与结束单词（以空格分割）");
                    word1 = in.next();
                    word2 = in.next();
                    res = my_graph.calcShortestPath(word1, word2);
                    if (res.isEmpty()) {
                        System.out.println("两点不可达");
                    } else {
                        System.out.println(res);
                    }
                    break;
                case 5:
                    System.out.println("开始随机游走");
                    res = my_graph.randomWalk();
                    System.out.println(res);
                    break;
                default:
                    System.out.println("输入非法");
                    break;
            }
        }

    }
}



