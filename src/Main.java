import org.apache.commons.io.FileUtils;
import support.Configuration;
import support.HelpUtils;
import support.SubFuntions;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Kir on 10.02.14.
 */
public class Main {
    private static PrintStream out = new PrintStream(System.out);
    final public static String absolutePath = "";
    private static String prefixPath = "";

    public static void main(String[] args) throws IOException {
        System.out.println(args[0]);
        getConfiguration(args[0]);
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("### ### ###\n" + Configuration.getHeader() + "\n### ### ###");
        System.out.println("[help] type \"help\" for list of commands");
        System.out.println("[log] waiting for a command");
        String inputCommand;
        prefixPath = Configuration.PATH;
        while (true) {
            inputCommand = bf.readLine();
            String[] icSplit = inputCommand.split(" ");

            //region exit
            if (inputCommand.equalsIgnoreCase("exit")) {
                break;
            }
            //endregion

            //region calc
            if (icSplit.length == 2 && icSplit[0].equalsIgnoreCase("calc")) {
                try {
                    ProteomNewickTreeBuilder.main(new String[]{icSplit[1]});
                } catch (Exception e) {
                    error();
                }
                continue;
            }
            //endregion

            //region build
            if (icSplit.length > 2 && icSplit[0].equalsIgnoreCase("build")) {
                boolean isGenom = false;
                switch (icSplit[1].toLowerCase()) {
                    case "unique":
                        buildUnique(icSplit);
                        break;
                    case "allranks":
                        buildAllRanks(icSplit);
                        break;
                    case "score":
                        buildScore(icSplit);
                        break;
                    case "genome":
                        isGenom = true;
                        buildGenom(icSplit);
                        break;
                    default:
                        error();
                        break;
                }
                continue;
            }
            //endregion

            //region list
            if (icSplit.length > 1 && icSplit[0].equalsIgnoreCase("list")) {
                if (icSplit[1].equalsIgnoreCase("built")) {
                    String[] list = dirReader(prefixPath + "\\ProteomTreesNewicks");
                    for (int i = 0; i < list.length; i++) {
                        out.println(list[i].substring(0, list[i].length() - 4));
                    }
                    out.println("total = " + list.length);
                } else {
                    if (icSplit[1].equalsIgnoreCase("data")) {
                        String[] list = dirReader(prefixPath + "\\MDProteom");
                        for (int i = 0; i < list.length; i++) {
                            out.println(list[i]);
                        }
                        out.println("total = " + list.length);
                    }
                }
                continue;
            }
            //endregion

            //region print
            if (icSplit.length > 1 && icSplit[0].equalsIgnoreCase("print")) {
                File[] resultDir = new File(prefixPath + "\\ProteomTreesNewicks\\").listFiles();
                assert resultDir != null;
                for (File f : resultDir) {
                    if (f.getName().contains(icSplit[1])) {
                        try {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                            String w = bufferedReader.readLine();
                            out.println(w);
                        } catch (IOException e) {
                            error();
                        }
                    }
                }
                continue;
            }
            //endregion

            //region arch
            if (icSplit.length > 1 && icSplit[0].equalsIgnoreCase("arch")) {
                String type = "-1";
                if (icSplit[1].equalsIgnoreCase("proteom")) {
                    type = "\\ProteomTreesNewicks";
                } else {
                    if (icSplit[1].equalsIgnoreCase("genome")) {
                        type = "\\GenomTreesNewicks";
                    }
                }
                if (type.equalsIgnoreCase("-1")) {
                    error();
                    continue;
                }
                File[] resultDir = new File(prefixPath + type).listFiles();
                assert resultDir != null;
                for (File f : resultDir) {
                    String fName = f.getName();
                    if (fName.contains(".txt") || fName.contains(".TXT")) {
                        fName = fName.substring(0, fName.lastIndexOf("."));
                    }
                    if (fName.equalsIgnoreCase(icSplit[2])) {
                        PhyShow.main(new String[]{f.getAbsolutePath(), "-arch"});
                    }
                }
                continue;
            }
            //endregion

            //region compare
            if (icSplit.length > 1 && icSplit[0].equalsIgnoreCase("compare")) {
                switch (icSplit[1].toLowerCase()) {
                    case "diff":
                        compareDiff(icSplit);
                        break;
                    case "wuf":
                        compareWeightedUniFrac(icSplit);
                        break;
                    case "totalwuf":
                        totalWeightedUniFrac();
                        break;
                    case "unwuf":
                        compareUnWeightedUniFrac(icSplit);
                        break;
                    case "totalunwuf":
                        totalUnWeightedUniFrac();
                        break;
                    case "gpwuf":
                        compareGPUniFrac(icSplit, "wuf");
                        break;
                    case "gpunwuf":
                        compareGPUniFrac(icSplit, "unwuf");
                        break;
                    default:
                        error();
                        break;
                }
                continue;
            }
            //endregion

            //region info
            if (icSplit.length > 1 && icSplit[0].equalsIgnoreCase("info")) {
                try {
                    showInfo(Integer.parseInt(icSplit[1]));
                } catch (NumberFormatException e) {
                    error();
                }
                continue;
            }
            //endregion

            //region delete
            if (icSplit.length > 1 && icSplit[0].equalsIgnoreCase("delete")) {
                switch (icSplit[1].toLowerCase()) {
                    case "all":
                        if (icSplit.length > 2) {
                            switch (icSplit[2].toLowerCase()) {
                                case "newick":
                                    deleteAll("ProteomTreesNewicks");
                                    break;
                                case "png":
                                    deleteAll("_png");
                                    break;
                                case "compare":
                                    deleteAll("resultCompare");
                                    break;
                                default:
                                    error();
                                    break;
                            }
                        }
                    default:
                        break;
                }
                continue;
            }
            //endregion

            //region png
            if (icSplit.length > 1 && icSplit[0].equalsIgnoreCase("png")) {
                png(icSplit);
                continue;
            }
            //endregion

            //region overlay
            if (icSplit.length == 3 && icSplit[0].equalsIgnoreCase("overlay")) {
                overlay(icSplit);
                continue;
            }

            if (icSplit[0].equalsIgnoreCase("help")) {
                if (icSplit.length == 1) {
                    HelpUtils.showGlobalHelp();
                } else {
                    if (icSplit.length == 2) {
                        HelpUtils.Manual(icSplit[1]);
                    } else {
                        error();
                    }
                }
                continue;
            }

            System.out.println("[help] unknown command");
            System.out.println("[help] check \"help\"");
        }
    }

    private static void error() {
        out.println("smth goes wrong, please, check your request");
    }

    private static String[] dirReader(String path) {
        File fatherDir = new File(absolutePath + path);
        File[] subDirs = fatherDir.listFiles();
        assert subDirs != null;
        String[] exps = new String[subDirs.length];
        int k = 0;
        for (File file : subDirs) {
            exps[k] = file.getName();
            k++;
        }
        return exps;
    }

    private static void buildUnique(String[] icSplit) {
        if (icSplit[2].equalsIgnoreCase("all")) {
            try {
                String[] exps = dirReader(prefixPath + "\\MDProteom");
                for (String exp : exps) {
                    ProteomNewickTreeBuilder.main(new String[]{exp, "-onlyBuildUnique"});
                    RemakeTree.main(new String[]{exp, Boolean.toString(false)});
                }
                out.println("done!");
            } catch (Exception e) {
                error();
            }
        } else {
            try {
                ProteomNewickTreeBuilder.main(new String[]{icSplit[2], "-onlyBuildUnique"});
                RemakeTree.main(new String[]{icSplit[2], Boolean.toString(false)});
                out.println("done!");
            } catch (Exception e) {
                error();
            }
        }
    }

    private static void buildAllRanks(String[] icSplit) {
        if (icSplit[2].equalsIgnoreCase("all")) {
            try {
                String[] exps = dirReader(prefixPath + "\\MDProteom");
                for (String exp : exps) {
                    ProteomNewickTreeBuilder.main(new String[]{exp, "-onlyBuildAllRanks"});
                    RemakeTree.main(new String[]{exp, Boolean.toString(false)});
                }
                out.println("done!");
            } catch (Exception e) {
                error();
            }
        } else {
            try {
                ProteomNewickTreeBuilder.main(new String[]{icSplit[2], "-onlyBuildAllRanks"});
                RemakeTree.main(new String[]{icSplit[2], Boolean.toString(false)});
                out.println("done!");
            } catch (Exception e) {
                error();
            }
        }
    }

    private static void buildScore(String[] icSplit) {
        if (icSplit[2].equalsIgnoreCase("all")) {

            try {
                String[] exps = dirReader(prefixPath + "\\MDProteom");
                for (String exp : exps) {
                    ProteomNewickTreeBuilder.main(new String[]{exp, "-onlyBuildScore"});
                    RemakeTree.main(new String[]{exp, Boolean.toString(false)});
                }
                out.println("done!");
            } catch (Exception e) {
                error();
                e.printStackTrace();
            }
        } else {
            try {
                ProteomNewickTreeBuilder.main(new String[]{icSplit[2], "-onlyBuildScore"});
                RemakeTree.main(new String[]{icSplit[2], Boolean.toString(false)});
                out.println("done!");
            } catch (Exception e) {
                error();
            }
        }
    }

    private static void buildGenom(String[] icSplit) {
        String exp;
        switch (icSplit[2].toLowerCase()) {
            case "679":
            case "680":
            case "688":
                exp = "BD_D1";
                break;
            case "681":
            case "683":
            case "685":
                exp = "BD_M1";
                break;
            case "682":
                exp = "BD_BD";
                break;
            case "697":
                exp = "BD_ChP";
                break;
            case "689":
            case "695":
                exp = "BD_ChK";
                break;
            case "all":
                exp = "-1";
                String[] experiments = SubFuntions.getGenomExperiments();
                for (String experiment : experiments) {
                    try {
                        GenomeNewickTreeBuilder.main(new String[]{experiment});
//                        RemakeTree.main(new String[]{exp, Boolean.toString(true)});
                    } catch (IOException e) {
                        error();
                    }
                }

            default:
                exp = "-1";
                break;
        }
        if (!exp.equals("-1")) {
            try {
                GenomeNewickTreeBuilder.main(new String[]{exp});
//                RemakeTree.main(new String[]{exp, Boolean.toString(true)});
            } catch (IOException e) {
                error();
            }
        }
    }

    private static void showInfo(int experiment) {
        boolean isAvailable = false;
        boolean isBuilt = false;
        String[] exps = dirReader(prefixPath + "\\MDProteom");
        for (String exp1 : exps) {
            if (exp1.equalsIgnoreCase(String.valueOf(experiment))) {
                isAvailable = true;
                break;
            }
        }
        if (isAvailable) {
            exps = dirReader(prefixPath + "\\ProteomTreesNewicks");
            for (String exp : exps) {
                if (exp.contains(String.valueOf(experiment))) {
                    isBuilt = true;
                    break;
                }
            }
        }
        System.out.println("experiment = " + experiment);
        System.out.println("isAvailable = " + isAvailable);
        System.out.println("isBuilt = " + isBuilt);
    }

    private static void compareDiff(String[] icSplit) {
        try {
            CompareTreesDiff.main(new String[]{icSplit[2], icSplit[3]});
        } catch (IOException e) {
            error();
        }
        System.out.println("done!");
    }

    private static void compareWeightedUniFrac(String[] icSplit) {
        try {
            WeightedUniFracCompare.main(new String[]{icSplit[2], icSplit[3]});
        } catch (IOException e) {
            error();
        }
        System.out.println("done!");
    }

    private static void totalWeightedUniFrac() {
        LinkedHashMap<PairP<String, String>, Double> pairDistance = new LinkedHashMap<>();
        String[] exps = dirReader(prefixPath + "\\ProteomTreesNewicks");
        for (String si : exps) {
            for (String sj : exps) {
                if (!si.equals(sj)) {
                    try {
                        String dsi = si.substring(0, si.length() - 4);
                        String dsj = sj.substring(0, sj.length() - 4);
                        WeightedUniFracCompare.main(new String[]{dsi, dsj});
                        if (WeightedUniFracCompare.result == -1) {
                            System.out.println("stop");
                        }
                        if (!Double.isNaN(WeightedUniFracCompare.result) && WeightedUniFracCompare.result != -1.0)
                            pairDistance.put(new PairP<>(dsi, dsj), WeightedUniFracCompare.result);
                    } catch (IOException e) {
                        System.out.println("-->can't make compare " + si + " " + sj);
                    }
                }
            }
        }
        System.out.println("done!");
        try {
            PrintWriter pw = new PrintWriter(prefixPath + "\\tmp20\\out.5");
            for (Map.Entry<PairP<String, String>, Double> entry : pairDistance.entrySet()) {
                pw.println(entry.getKey().getKey() + "\t" + entry.getKey().getValue() + "\t" + entry.getValue());
            }
            pw.close();
        } catch (FileNotFoundException ignored) {
        }

    }

    private static void compareUnWeightedUniFrac(String[] icSplit) {
        try {
            UnWeightedUniFracCompare.main(new String[]{icSplit[2], icSplit[3]});
        } catch (IOException e) {
            error();
        }
        System.out.println("done!");
    }

    private static void totalUnWeightedUniFrac() {
        LinkedHashMap<PairP<String, String>, Double> pairDistance = new LinkedHashMap<>();
        String[] exps = dirReader(prefixPath + "\\ProteomTreesNewicks");
        for (String si : exps) {
            for (String sj : exps) {
                if (!si.equals(sj)) {
                    try {
                        String dsi = si.substring(0, si.length() - 4);
                        String dsj = sj.substring(0, sj.length() - 4);
                        UnWeightedUniFracCompare.main(new String[]{dsi, dsj});
                        if (UnWeightedUniFracCompare.result != -1) {
                            pairDistance.put(new PairP<>(dsi, dsj), UnWeightedUniFracCompare.result);
                        }
                    } catch (IOException e) {
                        System.out.println("-->can't make compare " + si + " " + sj);
                    }
                }
            }
        }
        System.out.println("done!");
        try {
            PrintWriter pw = new PrintWriter(prefixPath + "\\tmp20\\out.5");
            for (Map.Entry<PairP<String, String>, Double> entry : pairDistance.entrySet()) {
                pw.println(entry.getKey().getKey() + "\t" + entry.getKey().getValue() + "\t" + entry.getValue());
            }
            pw.close();
        } catch (FileNotFoundException ignored) {
        }

    }

    private static void compareGPUniFrac(String[] icSplit, String type) {
        String exp;
        switch (icSplit[2].toLowerCase()) {
            case "679":
            case "680":
            case "688":
                exp = "BD_D1";
                break;
            case "681":
            case "683":
            case "685":
                exp = "BD_M1";
                break;
            case "682":
                exp = "BD_BD";
                break;
            case "697":
                exp = "BD_ChP";
                break;
            case "689":
            case "695":
                exp = "BD_ChK";
                break;
            case "all":
                exp = "-1";
                LinkedHashMap<PairP<String, String>, Double> pairDistance = new LinkedHashMap<>();
                LinkedHashMap<String, String> exps = subfFillExps();
                for (String e : exps.keySet()) {
                    try {
                        CompareGenomeProteom.main(new String[]{exps.get(e), e, type});
                        if (CompareGenomeProteom.result != -1) {
                            pairDistance.put(new PairP<>(e, exps.get(e)), CompareGenomeProteom.result);
                        }
                        System.out.println("done!\nWriting to a file...");
                        try {
                            PrintWriter pw = new PrintWriter(prefixPath + "\\tmp20\\gpcompare.5");
                            for (Map.Entry<PairP<String, String>, Double> entry : pairDistance.entrySet()) {
                                pw.println(entry.getKey().getKey() + "\t" + entry.getKey().getValue() + "\t" + entry.getValue());
                            }
                            pw.close();
                            System.out.println("done!");
                        } catch (FileNotFoundException ignored) {
                        }
                    } catch (Exception e1) {
                        error();
                    }
                }
            default:
                exp = "-1";
                break;
        }
        if (!exp.equals("-1")) {
            try {
                CompareGenomeProteom.main(new String[]{exp, icSplit[2], type});
            } catch (Exception e) {
                error();
            }
        }
        System.out.println("done!");
    }

    private static void deleteAll(String dir) {
        String[] exps = dirReader(dir);
        for (String s : exps) {
            try {
                FileUtils.forceDelete(new File(dir + "/" + s));
            } catch (IOException ignored) {
            }
        }
    }

    private static void png(String[] icSplit) {
        switch (icSplit[1].toLowerCase()) {
            case "all": {
                String[] exps = dirReader(prefixPath + "\\ProteomTreesNewicks");
                for (String s : exps) {
                    try {
                        if (s.contains(".txt")) s = s.replace(".txt", "");
                        PhyShow.main(new String[]{s, "-png"});
                    } catch (Exception e) {
                        System.out.println("can't make png for " + s);
                    }
                }
                break;
            }
            case "current": {
                try {
                    PhyShow.main(new String[]{icSplit[2], "-png"});
                } catch (Exception e) {
                    error();
                }
                break;
            }
            case "genome": {
                try {
                    PhyShow.main(new String[]{icSplit[2], "-gpng"});
                } catch (Exception e) {
                    error();
                }
                break;
            }
            case "test": {
                PhyShow.main(new String[]{"test", "-pngtest"});
            }
        }
        System.out.println("done!");
    }

    private static void overlay(String[] icSplit) {
        try {
            OverlayTrees.main(new String[]{icSplit[1], icSplit[2], "false"});
            System.out.println("done");
        } catch (IOException e) {
            error();
        }
    }

    //exp_proteom -> exp_genome
    private static LinkedHashMap<String, String> subfFillExps() {
        LinkedHashMap<String, String> a = new LinkedHashMap<>();
        a.put("679", "BD_D1");
        a.put("680", "BD_D1");
        a.put("688", "BD_D1");
        a.put("681", "BD_M1");
        a.put("683", "BD_M1");
        a.put("685", "BD_M1");
        a.put("682", "BD_BD");
        a.put("697", "BD_ChP");
        a.put("689", "BD_ChK");
        a.put("695", "BD_ChK");
        return a;
    }

    public static void getConfiguration(String path) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            String bfCur;
            while ((bfCur = bufferedReader.readLine()) != null) {
                String[] ch = bfCur.split("=");
                switch (ch[0].toLowerCase()) {
                    case "nativeslibs": {
                        Configuration.NATIVE_LIBS_PATH = ch[1];
                        break;
                    }
                    case "path": {
                        Configuration.PATH = ch[1];
                        File dirs = new File(ch[1]);
                        for (File f : dirs.listFiles()) {
                            switch (f.getName().toLowerCase()) {
                                case "tree": {
                                    Configuration.TREE_PATH = f.getAbsolutePath() + "\\";
                                    break;
                                }
                                case "tmp": {
                                    Configuration.TMP_PATH = f.getAbsolutePath() + "\\";
                                    break;
                                }
                                case "tmp20": {
                                    Configuration.TMP20_PATH = f.getAbsolutePath() + "\\";
                                    break;
                                }
                                case "ProteomTreesNewicks": {
                                    Configuration.PTNEWICKS_PATH = f.getAbsolutePath() + "\\";
                                    break;
                                }
                            }
                        }
                    }

                }
            }

        } catch (IOException e) {
            System.out.println("[err] Can't read main.conf!");
        }
    }
}
