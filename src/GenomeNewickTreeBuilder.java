import java.io.*;
import java.util.*;

/**
 * Created by Kir on 15.01.14.
 */
public class GenomeNewickTreeBuilder {

    private static PrintWriter pwn;
    //protein -> peptide[]
    private static LinkedHashMap<String, ArrayList<String>> pro2pepList;
    //protein -> double score
    private static LinkedHashMap<String, Double> bact2weight;
    //белок -> узел
//    private static LinkedHashMap<String, ArrayList<String>> rawTreeListOfProteins = new LinkedHashMap<String, ArrayList<String>>();
    //узел -> список белков
    private static LinkedHashMap<String, Double> rawTreeListOfNodes;
    //peptides -> protein[]
//    private static TreeMap<String, String> pep2protList = new TreeMap<String, String>();
    //префикс для тестирования
    private static String prefix = "";
    private static boolean test = false;

    public static void main(String[] args) throws FileNotFoundException {
        pwn = null;
        pro2pepList = new LinkedHashMap<>();
        bact2weight = new LinkedHashMap<>();
        rawTreeListOfNodes = new LinkedHashMap<>();
        boolean onlyBuildUnique = false;
        boolean onlyBuildAllRanks = false;
        boolean onlyBuildScore = false;
        String command;
        String fName = "";
        switch (args.length) {
            case 1:
                command = "MDGenom/" + args[0] + ".txt";
                fName = args[0] + ".txt";
                break;
            default:
                command = "MDGenom/BD_A4.txt";
                fName = "BD_A4.txt";
                break;
        }
//        if (args.length == 1) {
//            command = "masterDat/" + args[0] + "/";
//        } else {
//            if (args.length == 2 && args[1].equalsIgnoreCase("-onlyBuild")) {
//                command = args[0];
//                onlyBuild = true;
//            } else {
//                command = "dat/";
//            }
//        }
        Logger.logStart();
//        try {
//            FileUtils.forceDelete(new File("tmp20/parseProtPept.txt"));
//        } catch (IOException ignored) {
//        }
//        if (test) {
//            ParserUniquePeptides.main(new String[]{command});
//        }
//        if (onlyBuildUnique) {
//            ParserUniquePeptides.main(new String[]{command});
//        }
//        if (onlyBuildAllRanks) {
//            ParserPeptidesAllRanks.main(new String[]{command});
//        }
//        if (onlyBuildScore) {
//            ParserPeptidesScore.main(new String[]{command});
//        }

//        String fileName = "half_proteins.csv";
//        if (prefix.length()<2) Parse.main(new String[]{});
        String fileName = prefix + "tree/_genus.csv";
//        long before = System.currentTimeMillis();
//        String fileName = "input.txt";
//        String fileName = "test/proteins_tree.csv";
        BufferedReader bf;
        PrintWriter pw = new PrintWriter("tmp20/out.6");

        Tree<String> tree = new Tree<>("root");
        TreeSet<String> rooted = new TreeSet<>();

        try {
//            String pws = "test/out/output.txt";
//            String pws = prefix + "tmp/out.1";
//            pw = new PrintWriter(pws);
            bf = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            String bfCurLine;
//            pw.println(bfCurLine);
            while ((bfCurLine = bf.readLine()) != null) {
                String[] ch = bfCurLine.split("\";\"");
                for (int i = 0; i < ch.length; i++) {
                    //TODO костыли!
//                    ch[i] = ch[i].substring(1, ch[i].length() - 1);
                    ch[i] = ch[i].replace("\'", "");
                    ch[i] = ch[i].replace("\"", "");
                    ch[i] = ch[i].replace(":", "_");
                }
                int lastIndexToAdd = ch.length - 1;
                for (int i = 2; i < ch.length; i++) {
                    String curLeaf = ch[i];
                    if (curLeaf.equalsIgnoreCase("")) {
                        lastIndexToAdd = i - 1;
                        break;
                    }
                    String curRoot = ch[i - 1];
                    if (!rooted.contains(curLeaf)) {
                        tree.addLeaf(curRoot, curLeaf);
                        rooted.add(curLeaf);
                    }
                }
//                if (!rawTreeListOfProteins.containsKey(ch[0])) {
//                    ArrayList<String> a = new ArrayList<String>();
//                    a.add(ch[lastIndexToAdd]);
//                    rawTreeListOfProteins.put(ch[0], a);
//                } else {
//                    ArrayList<String> a = rawTreeListOfProteins.get(ch[0]);
//                    a.add(ch[lastIndexToAdd]);
//                    rawTreeListOfProteins.put(ch[0], a);
//                }
//                if (!rawTreeListOfNodes.containsKey(ch[lastIndexToAdd])) {
//                    double a = ;
//                    rawTreeListOfNodes.put(ch[lastIndexToAdd], a);
//                } else {
//                    double a = rawTreeListOfNodes.get(ch[lastIndexToAdd]);
//                    a.add(ch[0]);
//                    rawTreeListOfNodes.put(ch[lastIndexToAdd], a);
//                }
//                tree.addLeaf(ch[lastIndexToAdd], "|->> " + ch[0]);
                tree.addLeaf(ch[lastIndexToAdd], ch[0]);
            }
//            bf.reset();
            bf.close();
            rooted.clear();
            rooted = null;
            pw.print(tree.toString());
            pw.close();
            pw = null;

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Raw tree has been created");
        try {
//            bf = new BufferedReader(new InputStreamReader(new FileInputStream(prefix + "tmp/parsePeptProt.txt")));
//            String curLine;
//            while ((curLine = bf.readLine()) != null) {
//                String[] ch = curLine.split(";");
//                if (ch.length < 2)
//                    pep2protList.put(ch[0], ch[1]);//лажа какая-то
//            }
            String curLine;
            String curPath = command;
//            if (onlyBuildScore) {
//                curPath = prefix + "tmp20/parseProtScore.txt";
//            } else {
//                curPath = prefix + "tmp20/parseProtPept.txt";
//            }
            bf = new BufferedReader(new InputStreamReader(new FileInputStream(curPath)));
//            if (onlyBuildScore) {
            while ((curLine = bf.readLine()) != null) {
                String[] ch = curLine.split("~");
                String protAccession = ch[0];
                double protScore = Double.parseDouble(ch[1]);
                bact2weight.put(protAccession, protScore);
//                }
//            } else {
//                while ((curLine = bf.readLine()) != null) {
//                    String[] ch = curLine.split(";");
//                    ArrayList<String> a = new ArrayList<String>();
//                    a.addAll(Arrays.asList(ch).subList(1, ch.length));
//                    pro2pepList.put(ch[0], a);//нормально, без проверки
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            newick("GenomTreesNewicks/" + fName, tree, false, onlyBuildScore);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        try {
//            print(rawTreeListOfNodes);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

//        try {
//            newick(prefix + "intensity_tree.txt", tree, true);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        Logger.logEnd();
        Logger.showEnding();
//        rawTreeListOfProteins.clear();
        rawTreeListOfNodes.clear();
        pwn = null;
        pro2pepList = null;
        bact2weight = null;
        rawTreeListOfNodes = null;
        if (test) {
            PhyShow.main(new String[]{});
        }
    }

    private static void print(TreeMap<String, ArrayList<String>> a) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter("tmp/out.2");
        for (Map.Entry<String, ArrayList<String>> entry : a.entrySet()) {
            pw.println(entry.getKey());
            ArrayList<String> b = entry.getValue();
            for (String c : b) {
                pw.print("\t" + c);
            }
            pw.println();
        }
        pw.close();
    }

    //    private static boolean flag4Intesity = false;
//    private static double maxWeight = 0;

    private static void newick(String fileName, Tree<String> tree, boolean isIntensity, boolean onlyBuildScore) throws FileNotFoundException {
        TreeSet<String> used = new TreeSet<>();
//        flag4Intesity = isIntensity;
        pwn = new PrintWriter(fileName);
        dfs(tree, used, onlyBuildScore);
        pwn.print(";");
        pwn.close();
        pwn = null;
        used.clear();
        used = null;
//        flag4Intesity = false;
    }

    //узлы -> веса
    private static TreeMap<String, Double> weights = new TreeMap<>();

    private static void dfs(Tree<String> tree, TreeSet<String> used, boolean onlyBuildScore) {
        int tl = tree.getSubTrees().size();
        if (tl > 0) pwn.print("(");
        String w = tree.getHead();
        used.add(w);
        Collection<Tree<String>> st = tree.getSubTrees();
        int i = 0;
        for (Tree<String> ct : st) {
            if (!used.contains(ct.getHead())) {
                dfs(ct, used, onlyBuildScore);
            }
            if (i != st.size() - 1) pwn.print(",");
            i++;
        }
        if (tl > 0) {
            pwn.print(")");
        }
        pwn.print(w);
        double curw;
        if (onlyBuildScore) {
            curw = countWeightViaScore(w);
        } else {
            curw = countWeight(w);
        }
//        if (!flag4Intesity)
        pwn.print(":" + Double.toString(curw));
//        else
//        pwn.print(":" + Double.toString(countIntensity(curw, maxWeight)));
    }

    //for intensity
    private static double countIntensity(double weight, double max_weight) {
        return (weight * 100) / max_weight;
    }

    private static double countWeight(String node) {
//        double tmp = 0.0;
//        if (!rawTreeListOfNodes.containsKey(node)) return 0;
//        по узлу беру протеины
//        ArrayList<String> a = rawTreeListOfNodes.get(node);
//        for (String s : a) {
//            if (pro2pepList.containsKey(s)) {
//                ArrayList<String> asd = pro2pepList.get(s);
//                if (asd.size() == 1) {
//                    tmp++;
//                }
//            }
//        }
        //удалить, если не нужен параметр "интенсивность"
//        if (tmp > maxWeight) maxWeight = tmp;
//        return tmp;
        if (!bact2weight.containsKey(node)) {
            return 0.0;
        } else {
            return bact2weight.get(node);
        }
    }

    private static double countWeightViaScore(String node) {
        double tmp = 0.0;
//        if (!rawTreeListOfNodes.containsKey(node)) return 0;
//        по узлу беру протеины
//        ArrayList<String> a = rawTreeListOfNodes.get(node);
//        Collections.sort(a);
//        for (String s : a) {
//            if (bact2weight.containsKey(s)) {
//                double asd = bact2weight.get(s);
//                tmp += asd;
//            }
//        }
        //удалить, если не нужен параметр "интенсивность"
//        if (tmp > maxWeight) maxWeight = tmp;
        return tmp;
    }
}
