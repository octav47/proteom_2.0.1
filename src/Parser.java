/*
##############################################################################
# file: resfile_peptidesum.java                                              #
# 'msparser' toolkit                                                         #
# Test harness / example code                                                #
##############################################################################
# COPYRIGHT NOTICE                                                           #
# Copyright 1998-2003 Matrix Science Limited  All Rights Reserved.           #
#                                                                            #
##############################################################################
#    $Archive:: /Mowse/ms_mascotresfile/test_java/test_peptidesummary.java $ #
#     $Author: davidc $ #
#       $Date: 2004-12-23 14:27:35 $ #
#   $Revision: 1.2 $ #
# $NoKeywords::                                                            $ #
##############################################################################
*/
// Note - uses java.util.regex - some lines will need to be commented out
// before this will work on JDK versions older than 1.4.  There are marked 
// in the code

import matrix_science.msparser.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class Parser {

    private static final HashMap<String, ArrayList<String>> protoPeps = new HashMap<>();
    private static ArrayList<String> tmp;
    private static int underScore = 0;

    private static LinkedHashSet<String> uniquePeptidesSet = new LinkedHashSet<>();
    private static LinkedHashMap<String, Integer> pepsCount = new LinkedHashMap<>();
    private static LinkedHashMap<String, LinkedHashSet<String>> pepsCountViaProteins = new LinkedHashMap<>();

    static {
        try {
            System.load("C:\\Users\\Kir\\IdeaProjects\\DatParser\\lib\\native\\msparserj.dll");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load. "
                    + "Is msparserj.dll on the path?\n" + e);
            System.exit(0);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        //time start
        long before = System.nanoTime();
        String filePrefix = "dat2/";
        File dir = new File(filePrefix);
        File[] files1 = dir.listFiles();
        assert files1 != null;
        String[] files = new String[files1.length];
        for (int t = 0; t < files1.length; t++) {
            if (files1[t].getName().contains(".dat")) {
                files[t] = files1[t].getName();
            } else {
                files[t] = "ignored";
            }
        }
        for (int t = 0; t < files.length; t++) {
            if (!files[t].contains("ignored")) {
                ms_mascotresfile file = new ms_mascotresfile(filePrefix + files[t], 0, "");
                if (file.isValid()) {

                    if (file.isMSMS()) {
                        System.out.println("#started " + filePrefix + files[t] + " at time = " + (System.nanoTime() - before) / 1000000000);
                        showResults(file,
                                true,
                                ms_mascotresults.MSRES_GROUP_PROTEINS |
                                        ms_mascotresults.MSRES_SHOW_SUBSETS,
                                0,     // minProteinProb
                                10000,     // maxHits
                                null,  // Unigene file name
                                0,     // minIonsScore
                                0);    // minPepLenInPepSummary
                        System.out.println("showResults(" + file.getFileName() + ")");
                    } else {
                        System.out.println("Not an MS_MS results file - cannot show peptide summary");
                    }
                } else {
                    System.out.println("Error number: " + file.getLastError());
                    System.out.println("Error string: " + file.getLastErrorString());
                    System.exit(0);
                }
            }
        }
        for (String s : protoPeps.keySet()) {
            ArrayList<String> a = protoPeps.get(s);
            a = deleteUnUnique(a);
            protoPeps.put(s, a);
        }

        //peptides -> proteins[]
        TreeMap<String, ArrayList<String>> pepsProt = new TreeMap<>();
        //peptides -> count of use
//        TreeMap<String, Integer> pepsCountUsed = new TreeMap<String, Integer>();

        //похоже вечерами мой код бухает вместо меня
        PrintWriter pw = new PrintWriter("tmp/parseProtPept.txt");
        for (Map.Entry<String, ArrayList<String>> s : protoPeps.entrySet()) {
            StringBuilder ans = new StringBuilder(s.getKey());
            ArrayList<String> a = s.getValue();
            for (String anA : a) {

//                if (uniquePeptides.contains(anA)) {
                ans.append(";").append(anA);
//                }
                if (!pepsProt.containsKey(anA)) {
                    ArrayList<String> b = new ArrayList<>();
                    b.add(s.getKey());
                    pepsProt.put(anA, b);
                } else {
                    ArrayList<String> b = pepsProt.get(anA);
                    b.add(s.getKey());
                    pepsProt.put(anA, b);
                }
            }
            pw.println(ans.toString());
        }
        pw.close();
        pw = new PrintWriter("tmp/parsePeptProt.txt");
        for (Map.Entry<String, ArrayList<String>> s : pepsProt.entrySet()) {
            StringBuilder ans = new StringBuilder(s.getKey());
            ArrayList<String> a = s.getValue();
            if (a.size() > 1) {
                int asd;
                asd = 0;  //ахтунг, если сюда зайдёт
            }
//            if (a.size() == 1 && uniquePeptides.contains(s.getKey())) {
//            if (a.size() == 1) {
//                ans.append(";").append(a.get(0));  //костыль
//                pw.println(ans.toString());
//            }
            for (String s1 : a) {
                ans.append(";").append(s1);
            }
            pw.println(ans.toString());
        }
        pw.close();
        System.out.println("Time = " + (System.nanoTime() - before) / 1000000000);
        System.out.println(protoPeps.size());
        System.out.println(pepsProt.size());
        System.out.println(uniquePeptides.size());
        System.out.println(ignoredPeptides.size());
        System.out.println("peptides under rank count : " + underScore);
//        System.out.println(kk);
        pw = new PrintWriter("tmp/out.3");
        for (String s : uniquePeptides) {
            pw.println(s);
        }
        pw.close();
        pw = new PrintWriter("tmp/out.4");
        for (Map.Entry<String, Integer> entry : pepsCount.entrySet()) {
            pw.println(entry.getKey() + " " + entry.getValue());
        }
        pw.close();
        pw = new PrintWriter("tmp/out.5");
        for (Map.Entry<String, LinkedHashSet<String>> entry : pepsCountViaProteins.entrySet()) {
            pw.print(entry.getKey());
            for (String s : entry.getValue()) {
                pw.print(";" + s);
            }
            pw.println();
        }
        pw.close();
    }

    private static void showResults(ms_mascotresfile file,
                                    boolean pepSum,
                                    int flags,
                                    double minProteinProb,
                                    int maxHits,
                                    String unigeneFile,
                                    double minIonsScore,
                                    int minPepLenInPepSummary) {

        ms_mascotresults results;
        int hit;
        ms_protein prot;
        String accession;
        int num_peps;
        int i;
        int query;
        int p;
        ms_peptide pep;
        System.out.println("$showResults started");
        results = (pepSum) ? new ms_peptidesummary(file,
                flags,
                minProteinProb,
                maxHits,
                unigeneFile,
                minIonsScore,
                minPepLenInPepSummary,
                null) : new ms_proteinsummary(file,
                flags,
                minProteinProb,
                maxHits,
                null,
                null);
        System.out.println("$results is made");
        hit = 1;
        prot = results.getHit(hit);

        System.out.println("$results.getNumberOfHits() = " + results.getNumberOfHits());
        while (hit <= results.getNumberOfHits()) {
            accession = prot.getAccession();

            if (accession.contains("GL0115805_MH0055_")) {
                int a = 5;
                System.out.println("!!! " + prot.getScore());
            }

            num_peps = prot.getNumPeptides();
            tmp = new ArrayList<>();
            for (i = 1; i <= num_peps; i++) {
                query = prot.getPeptideQuery(i);
                p = prot.getPeptideP(i);
                if ((p != -1) && (query != -1) && (prot.getPeptideDuplicate(i) != ms_protein.DUPE_DuplicateSameQuery)) {
                    pep = results.getPeptide(query, p);
                    if (pep != null) {
//                        displayPeptideInfo(pep);
                        if (results.isPeptideUnique(query, hit)) {
                            if (pep.getPeptideStr().toUpperCase().contains("DNAFVNTISTR")) {
                                System.out.println("$stop");
                            }
                            String curPep = pep.getPeptideStr();
                            if (pep.getRank() == 1) {
                                if (pepsCountViaProteins.containsKey(curPep)) {
                                    LinkedHashSet<String> ls = pepsCountViaProteins.get(curPep);
                                    ls.add(prot.getAccession());
                                    pepsCountViaProteins.put(curPep, ls);
                                } else {
                                    LinkedHashSet<String> ls = new LinkedHashSet<>();
                                    ls.add(prot.getAccession());
                                    pepsCountViaProteins.put(curPep, ls);
                                }

                                if (pepsCount.containsKey(curPep)) {
                                    int b = pepsCount.get(curPep);
                                    pepsCount.put(curPep, b + 1);
                                } else {
                                    pepsCount.put(curPep, 1);
                                }

                                tmp.add(curPep);
                                if (!uniquePeptides.contains(curPep) && !ignoredPeptides.contains(curPep)) {
                                    uniquePeptides.add(curPep);
                                } else {
                                    if (uniquePeptides.contains(curPep) && !ignoredPeptides.contains(curPep)) {
                                        uniquePeptides.remove(curPep);
                                        ignoredPeptides.add(curPep);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (protoPeps.containsKey(accession)) {
                ArrayList<String> opl = protoPeps.get(accession);
                opl.addAll(tmp);
                protoPeps.put(accession, opl);
            } else {
                protoPeps.put(accession, tmp);
            }
            tmp = null;
            hit++;

            prot = results.getHit(hit);
        }
    }

    private static HashSet<String> uniquePeptides = new HashSet<>();
    private static HashSet<String> ignoredPeptides = new HashSet<>();

    private static void displayPeptideInfo(ms_peptide p) {
//        System.out.println("$get " + p.getPeptideStr());
//        System.out.println(p.getAnyMatch());
        if (p.getAnyMatch()) {
            String curPep = p.getPeptideStr();
            if (p.getIonsScore() < 45) {
                underScore++;
            }
            if (p.getRank() == 1) {
                tmp.add(curPep);
            }
            if (!uniquePeptides.contains(curPep) && !ignoredPeptides.contains(curPep)) {
                uniquePeptides.add(curPep);
            } else {
                if (uniquePeptides.contains(curPep) && !ignoredPeptides.contains(curPep)) {
                    uniquePeptides.remove(curPep);
                    ignoredPeptides.add(curPep);
                }
            }

        }
    }

    private static ArrayList<String> deleteUnUnique(ArrayList<String> a) {
        ArrayList<String> b = new ArrayList<>();
        LinkedHashMap<String, Integer> count = new LinkedHashMap<>();
        for (String s : a) {
            if (!count.containsKey(s)) {
                count.put(s, 1);
            } else {
                int c = count.get(s);
                count.put(s, c + 1);
            }
        }
        for (String s : a) {
            if (count.get(s) == 1) {
                b.add(s);
            }
        }
        return b;
    }
}
