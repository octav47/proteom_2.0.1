import org.apache.commons.io.FileUtils;
import org.forester.io.parsers.PhylogenyParser;
import org.forester.io.parsers.util.ParserUtils;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyMethods;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Kir on 29.01.14.
 */
public class testing {
    private static HashSet<String> uniquePeptides = new HashSet<>();
    private static HashSet<String> ignoredPeptides = new HashSet<>();


    public static void main(String[] args) throws IOException {
//        parseGradient();
//        downloadFiles();
//        genFiles();
//        remakeGenomTree();
//        readGenusBG();
//        remakeCSVGenom();
//        remakeEcoli();
//        checkNullBranches();
//        remakeMDS();
//        gen4som();
//        buildUNFRAC();
//        getOSProperties();
        returnDirStruct();
    }

    private static void returnDirStruct() {
        File root = new File("C:\\Users\\Kir\\IdeaProjects\\PhyBuilder\\");
        File[] dirs = root.listFiles();
        System.out.println(root.getAbsolutePath());
        assert dirs != null;
        recDirTmp(root, "");
        System.out.println(root.getAbsolutePath());
    }

    private static void recDirTmp(File dir, String s) {
        System.out.println(s + dir.getName());
        File[] files = dir.listFiles();
        if (files.length != 0) {
            for (File f : files) {
                if (f.isDirectory() && !f.getName().contains("$")) {
                    recDirTmp(f, s + "  ");
                }
            }
        }
    }

    private static void getOSProperties() {
        System.out.println(System.getProperty("os.name"));
    }

    private static void remakeMDS() throws IOException {
        String fileName = "C:\\Users\\Kir\\Desktop\\new_allranks_weighted.txt";
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        PrintWriter pw = new PrintWriter("C:\\Users\\Kir\\Desktop\\1_new_allranks_weighted.txt");
        String curBf;
        LinkedHashMap<PairP<String, String>, Double> result = new LinkedHashMap<>();
        while ((curBf = bufferedReader.readLine()) != null) {
            if (!curBf.contains("675") && !curBf.contains("693") && !curBf.contains("697")) {
                String[] ch = curBf.split("\t");
                PairP<String, String> pair = new PairP<>(ch[0], ch[1]);
                result.put(pair, Double.parseDouble(ch[2]));
            }
        }
        pw.close();
    }

    private static void buildUNFRAC() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\Kir\\Desktop\\new_score_unweighted.txt")));
        double[][] a = new double[1000][1000];
        String curBf;
        int maxi = 0;
        int maxj = 0;
        LinkedHashSet<Integer> exps = new LinkedHashSet<>();
        while ((curBf = bufferedReader.readLine()) != null) {
            String[] ch = curBf.split("\t");
            int i = Integer.parseInt(ch[0]);
            int j = Integer.parseInt(ch[1]);
            exps.add(i);
            exps.add(j);
            a[i][j] = Double.parseDouble(ch[2]);
            if (i > maxi) {
                maxi = i;
            }
            if (j > maxj) {
                maxj = j;
            }
        }
        System.out.println(maxi);
        System.out.println(maxj);
        PrintWriter pw = new PrintWriter("C:\\Users\\Kir\\Desktop\\unifrac\\score_uw.txt");
        pw.print("name ");
        for (int i = 674; i <= 700; i++) {
            pw.print("distTo_" + i + " ");
        }
        pw.println();
        for (int i = 674; i <= 700; i++) {
//            if (exps.contains(i)) {
            pw.print(i + " ");
            for (int j = 674; j <= 700; j++) {
//                    if (exps.contains(j))
                pw.print(a[i][j] + " ");
            }
            pw.println();
//            }
        }
        pw.close();
    }

    private static void genFiles() throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream("tmp30/kron2.csv")));
        String curBf = bf.readLine();
        while ((curBf = bf.readLine()) != null) {
            String[] ch = curBf.split(";");
            ch[0] = ch[0].replace("\"", "");
            ch[4] = ch[4].replace("\"", "");
            ch[4] = ch[4].replace(", \t", "\n");
            ch[4] = ch[4].replace(",", "");
            PrintWriter pw = new PrintWriter("tmp30/ToDownload/" + Integer.parseInt(ch[0]) + ".txt");
            pw.println(ch[0] + "\n" + ch[4]);
            pw.close();
        }
    }

    private static void parseGradient() throws IOException {
        BufferedImage im = ImageIO.read(new File("C:\\Users\\Kir\\Desktop\\gradient3.png"));
        int w = im.getWidth();
        int h = im.getHeight();
        int[][] pixel = new int[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                pixel[i][j] = im.getRGB(i, j);
            }
        }
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                System.out.print(pixel[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static void dirReader() {
        File[] subDirs = null;
        File fatherDir = new File("C:\\Users\\Kir\\IdeaProjects\\PhyBuilder");
        subDirs = fatherDir.listFiles();
        if (subDirs != null) {
            for (File file : subDirs) {
                if (file.isDirectory() && file.getName().contains("dat")) {
                    System.out.println(file.getAbsolutePath());
                }
            }
        }
    }

    private static ArrayList<String> files2download = new ArrayList<>();
    private static LinkedHashMap<String, Boolean> isDownloaded = new LinkedHashMap<>();
    private static String projectPath = "MDProteom/";

    private static void downloadFiles() throws IOException {
//        File dir = new File("tmp30/ToDownload");
//        File[] dirArray = dir.listFiles();
//        ArrayList<String> names = new ArrayList<>();
//        for (File f : dirArray) {
        BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream("tmp30/ToDownload/747.txt")));
        String cs = bf.readLine();
        int projectNumber = Integer.parseInt(cs);
        projectPath += projectNumber;
        FileUtils.forceMkdir(new File(projectPath));
        while ((cs = bf.readLine()) != null) {
            files2download.add(cs);
        }
        downloadFilesDirectly("\\\\kenny.ripcm.com\\data\\");
        downloadFilesDirectly("\\\\cluster.ripcm.com\\kennyfs\\mascot_backup");
        downloadFilesDirectly("\\\\cluster.ripcm.com\\kennyfs\\mascot_backup\\data2");

        checkDownloads();
        isDownloaded.clear();
        files2download.clear();
        projectPath = "MDProteom/";
//        }

    }

    private static void downloadFilesDirectly(String path) throws IOException {
        File fatherDir = new File(path);
        File[] subDirs = fatherDir.listFiles();
        assert subDirs != null;
        for (File curFile : subDirs) {
            if (!curFile.isDirectory()) continue;
            String a = curFile.getAbsolutePath();
            String[] ch = a.split("\\\\");
            if (contains(files2download, ch[ch.length - 1])) {
                File curDir = new File(a);

                File[] files = curDir.listFiles();
                assert files != null;
                for (File f : files) {
//                    System.out.println(f.getName());
                    if (contains(files2download, f.getName())) {
                        System.out.println(f.getName());
                        isDownloaded.put(f.getName(), Boolean.TRUE);
                        File out = new File(projectPath + "/" + f.getName());
                        Path path1 = Paths.get(out.getAbsolutePath());
                        if (!Files.exists(path1)) {
                            FileUtils.copyFile(f, out);
                        }
                    }
                }
            }
        }
    }

    private static boolean contains(ArrayList<String> a, String s) {
        for (String cs : a) {
            if (cs.contains(s)) {
                return true;
            }
        }
        return false;
    }

    private static void checkDownloads() {
        System.out.println("Not downloaded:");
        int k = 0;
        for (String f : files2download) {
            String[] ch = f.split("\\\\");
            if (!isDownloaded.containsKey(ch[2])) {
                System.out.println(f);
                k++;
            }
        }
        System.out.println("Total: " + k);
    }

    private static void remakeGenomTree() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("tree/genus_path.txt")));
        PrintWriter pw = new PrintWriter(new File("tree/genus.csv"));
        String curBf;
        TreeSet<Integer> opl = new TreeSet<>();
        while ((curBf = bufferedReader.readLine()) != null) {
            curBf = curBf.replace("\t", ";");
            curBf = curBf.replace("(", "[");
            curBf = curBf.replace(")", "]");
            curBf = curBf.replace(" ", "_");
            curBf = curBf.replace(";", "\";\"");
            curBf = "\"" + curBf + "\"";
            int k = 0;
            for (int i = 0; i < curBf.length(); i++) {
                if (curBf.charAt(i) == ';') {
                    k++;
                    if (k > 8) {
                        System.out.println(curBf);
                    }
                }
            }
            opl.add(k);
            pw.println(curBf);
        }
        pw.close();
        for (Integer i : opl) {
            System.out.println(i);
        }
    }

    private static void readGenusBG() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("tmp20/genomBG.csv")));
        LinkedHashMap<PairP<String, String>, Double> lhm = new LinkedHashMap<>();
        PrintWriter pw = new PrintWriter(new File("tmp20/_genomBG.txt"));
        String curBf = bufferedReader.readLine();
        String[] headers = curBf.split("\t");
        curBf = curBf.replace("\t", ";");
        while ((curBf = bufferedReader.readLine()) != null) {
            String[] currentSplit = curBf.split("\t");
            String curHeader = currentSplit[0];
            curBf = curBf.replace(",", ".");
//            curBf = curBf.replace("\t", ";");
            for (int i = 1; i < currentSplit.length; i++) {
                currentSplit[i] = currentSplit[i].replace(",", ".");
                PairP<String, String> pair = new PairP<>(headers[i], curHeader);
                lhm.put(pair, Double.parseDouble(currentSplit[i]));
            }
        }
        //эксперимент -> HashMap <бактерия, double>
        LinkedHashMap<String, HashMap<String, Double>> exps = new LinkedHashMap<>();
        for (Map.Entry<PairP<String, String>, Double> entry : lhm.entrySet()) {
            pw.println(entry.getKey().getKey() + "~" + entry.getKey().getValue() + "~" + entry.getValue());
            HashMap<String, Double> h;
            if (exps.containsKey(entry.getKey().getValue())) {
                h = exps.get(entry.getKey().getValue());
            } else {
                h = new HashMap<>();
            }
            h.put(entry.getKey().getKey(), entry.getValue());
            exps.put(entry.getKey().getValue(), h);
        }
        pw.close();
        for (Map.Entry<String, HashMap<String, Double>> entry : exps.entrySet()) {
            StringBuilder s = new StringBuilder();
            TreeMap<String, Double> res = new TreeMap<>();
            for (Map.Entry<String, Double> e : entry.getValue().entrySet()) {
                s.append(e.getKey()).append("\t").append(e.getValue()).append("\n");
                res.put(e.getKey(), e.getValue());
            }
            System.out.println("===\n" + entry.getKey() + "\n" + s.toString());

            String fileName = "MDGenom/" + entry.getKey() + ".txt";
            try {
                FileUtils.forceDelete(new File(fileName));
            } catch (IOException ignored) {
            }
            pw = new PrintWriter(fileName);
            for (Map.Entry<String, Double> e : res.entrySet()) {
                pw.println(e.getKey() + "~" + e.getValue());
            }
            pw.close();
        }
    }

    private static void remakeCSVGenom() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("tree/genus.csv")));
        String curBf;
        PrintWriter pw = new PrintWriter("tree/_genus.csv");
        while ((curBf = bufferedReader.readLine()) != null) {
            System.out.println(curBf);
            String[] ch = curBf.split(";");
            String s = "";
            for (int i = 0; i < ch.length - 1; i++) {
                s += ch[i] + ";";
            }
            s = s.substring(0, s.length() - 1);
            System.out.println(s);
            pw.println(s);
        }
        pw.close();
    }

    private static void remakeEcoli() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\Kir\\Downloads\\LF82_.txt")));
        PrintWriter pw = new PrintWriter("tree/ecoli.txt");
        String curBf = bufferedReader.readLine();
        while ((curBf = bufferedReader.readLine()) != null) {
            String[] ch = curBf.split("\t");
            ch[2] = ch[2].substring(1);
            StringBuilder s = new StringBuilder();
            s.append("\"").append(ch[1]).append("\";").append(ch[2]);
            pw.println(s.toString());
        }
        pw.close();
    }

    private static void checkNullBranches() throws IOException {
        File treeFile = new File("_tests/png_test.txt");
        PhylogenyParser parser = ParserUtils.createParserDependingOnFileType(treeFile, true);
        Phylogeny phy = PhylogenyMethods.readPhylogenies(parser, treeFile)[0];
        List<PhylogenyNode> extNodes = phy.getExternalNodes();
        PriorityQueue<PhylogenyNode> queue = new PriorityQueue<>();
        for (PhylogenyNode n : extNodes) {
            queue.add(n);
        }
        LinkedHashSet<PhylogenyNode> excludedNodes = new LinkedHashSet<>();
        while (queue.size() != 0) {
            PhylogenyNode n = queue.poll();
            if (n.getDistanceToParent() != 0) {
                List<PhylogenyNode> list = getAllNodesInPathToRoot(n);
                for (PhylogenyNode n1 : list) {
                    excludedNodes.add(n1);
                }
            } else {
                PhylogenyNode parent = n.getParent();
                if (!excludedNodes.contains(parent)) {
                    queue.add(n.getParent());
                }
            }
        }
        for (PhylogenyNodeIterator it = phy.iteratorPreorder(); it.hasNext(); ) {
            PhylogenyNode n = it.next();
            if (!excludedNodes.contains(n)) {
                try {
                    phy.deleteSubtree(n, false);
                } catch (Exception ignored) {
                }
            }
        }
        System.out.println(phy.toString());
        PrintWriter pw = new PrintWriter("test_without_nulls.txt");
        pw.println(phy.toString());
        pw.close();
    }

    private static List<PhylogenyNode> getAllNodesInPathToRoot(PhylogenyNode node) {
        List<PhylogenyNode> pathNodes = new LinkedList<>();
        rec(node, pathNodes);
        return pathNodes;
    }

    private static void rec(PhylogenyNode node, List<PhylogenyNode> list) {
        list.add(node);
        PhylogenyNode parent = node.getParent();
        if (parent != null) {
            rec(parent, list);
        }
    }


    private static void gen4som() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter("C:\\Users\\Kir\\Desktop\\1.txt");
        int n = 3;
        int m = 10000;
        for (int i = 0; i < m; i++) {
            Random r = new Random(System.nanoTime());
            for (int j = 0; j < n; j++) {
                double tmp = r.nextDouble();
                if (i % 2 == 0) tmp *= 2;
                if (i % 3 == 0) tmp *= 3;
                if (i % 5 == 0) tmp *= 5;
                if (i % 7 == 0) tmp *= 7;
                pw.print(tmp + " ");
            }
            pw.println();
        }
        pw.close();
    }

}
