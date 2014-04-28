package parsers;

import matrix_science.msparser.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Created by Kir on 31.01.14.
 */
public class ParserUniquePeptides {
    static {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                File lib = new File("lib/native/msparserj.dll");
                System.load(lib.getAbsolutePath());
            } else {
                File lib = new File("lib/native/libmsparserj.so");
                System.load(lib.getAbsolutePath());
            }
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load. "
                    + "Is library on the path?\n" + e);
            System.exit(0);
        }
    }

    private static LinkedHashMap<String, LinkedHashSet<String>> peptides2proteins;

    private static String dir = "MDProteom/";

    public static void main(String[] args) {
        peptides2proteins = new LinkedHashMap<>();
        long before = System.nanoTime();
        String filePrefix;
        if (args.length == 1) {
            filePrefix = dir + args[0] + "/";
        } else {
            filePrefix = "dat/";
        }
        File dir = new File(filePrefix);
        File[] files1 = dir.listFiles();
        ArrayList<String> files = new ArrayList<String>();
        if (files1 != null || files.size() != 0) {
            for (int t = 0; t < files1.length; t++) {
                if (files1[t].getName().contains(".dat")) {
                    files.add(files1[t].getName());
                }
            }
            for (String file_t : files) {
                ms_mascotresfile file = new ms_mascotresfile(filePrefix + file_t);
                if (file.isValid()) {
                    if (file.isMSMS()) {
                        System.out.println("#started " + filePrefix + file_t + " at time = " + (System.nanoTime() - before) / 1000000000);
                        showResults(file, 30000, 44);
                    }
                }
            }
//            printLHM1("tmp20/peptidesToProteins.txt", peptides2proteins);
            LinkedHashMap<String, String> uniquePeptides2proteins = calcUniquePeptides(peptides2proteins);
//            printLHM2("tmp20/uniquePeptidesToProteins.txt", uniquePeptides2proteins);
            LinkedHashMap<String, LinkedHashSet<String>> proteins2peptides = makeTableProteins2peptides(uniquePeptides2proteins);
            printLHM1("tmp20/parseProtPept.txt", proteins2peptides);
            peptides2proteins.clear();
            proteins2peptides.clear();
            proteins2peptides = null;
            peptides2proteins = null;
        }
    }


    private static void showResults(ms_mascotresfile file, int maxHits, double minIonsScore) {

        ms_mascotresults results;
        int hit;
        ms_protein prot;
        int num_peps;
        int i;
        int query;
        int p;
        ms_peptide pep;
        System.out.println("$showResults started");
        results = new ms_peptidesummary(file,
                ms_mascotresults.MSRES_GROUP_PROTEINS |
                        ms_mascotresults.MSRES_SHOW_SUBSETS,
                0,
                maxHits,
                null,
                minIonsScore,
                0,
                null);
        System.out.println("$results is made");
        hit = 1;
        prot = results.getHit(hit);
        System.out.println("$results.getNumberOfHits() = " + results.getNumberOfHits());

        while (hit <= results.getNumberOfHits()) {
            if (isBactProtein(prot)) {
                num_peps = prot.getNumPeptides();
                for (i = 1; i <= num_peps; i++) {
                    query = prot.getPeptideQuery(i);
                    p = prot.getPeptideP(i);
                    if ((p != -1) && (query != -1) && (prot.getPeptideDuplicate(i) != ms_protein.DUPE_DuplicateSameQuery)) {
                        pep = results.getPeptide(query, p);
                        if (pep != null) {
                            checkPeptide(pep, prot);
                        }
                    }
                }
            }
            hit++;
            prot = results.getHit(hit);
        }
    }

    private static void checkPeptide(ms_peptide peptide, ms_protein protein) {
        if (peptide.getRank() == 1) {
            if (protein.getAccession().startsWith("lcl")) {
                System.out.println(protein.getAccessionStr());
            }
            String pepName = peptide.getPeptideStr();
            String protName = protein.getAccession();
            LinkedHashSet<String> lhs;
            if (peptides2proteins.containsKey(pepName)) {
                lhs = peptides2proteins.get(pepName);

            } else {
                lhs = new LinkedHashSet<>();
            }
            lhs.add(protName);
            peptides2proteins.put(pepName, lhs);
        }
    }

    private static boolean isBactProtein(ms_protein protein) {
        return protein.getAccession().startsWith("GL") || protein.getAccession().startsWith("lcl");
    }

    private static void printLHM1(String fileName, LinkedHashMap<String, LinkedHashSet<String>> lhm) {
        try {
            try {
                FileUtils.forceDelete(new File("tmp20/parseProtPept.txt"));
            } catch (IOException ignored) {
            }
            PrintWriter pw = new PrintWriter(fileName);
            for (Map.Entry<String, LinkedHashSet<String>> entry : lhm.entrySet()) {
                StringBuilder ans = new StringBuilder("");
                ans.append(entry.getKey());
                for (String s : entry.getValue()) {
                    ans.append(";").append(s);
                }
                pw.println(ans.toString().toLowerCase());
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void printLHM2(String fileName, LinkedHashMap<String, String> lhm) {
        try {
            PrintWriter pw = new PrintWriter(fileName);
            for (Map.Entry<String, String> entry : lhm.entrySet()) {
                pw.println(entry.getKey() + ";" + entry.getValue());
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String OneElementLHStoString(LinkedHashSet<String> lhs) {
        if (lhs.size() == 1) {
            for (String s : lhs) {
                return s;
            }
        }
        return "error";
    }

    private static LinkedHashMap<String, String> calcUniquePeptides(LinkedHashMap<String, LinkedHashSet<String>> lhm) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, LinkedHashSet<String>> entry : lhm.entrySet()) {
            if (entry.getValue().size() == 1) {
                result.put(entry.getKey(), OneElementLHStoString(entry.getValue()));
            }
        }
        return result;
    }

    private static LinkedHashMap<String, LinkedHashSet<String>> makeTableProteins2peptides(LinkedHashMap<String, String> lhm) {
        LinkedHashMap<String, LinkedHashSet<String>> result = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : lhm.entrySet()) {
            if (result.containsKey(entry.getValue())) {
                LinkedHashSet<String> l1 = result.get(entry.getValue());
                l1.add(entry.getKey());
                result.put(entry.getValue(), l1);
            } else {
                LinkedHashSet<String> l1 = new LinkedHashSet<>();
                l1.add(entry.getKey());
                result.put(entry.getValue(), l1);
            }
        }
        return result;
    }
}
