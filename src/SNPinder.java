import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * @author GCW van den Hoeven (Gabe)
 * student Bio-informatics, HAN University of Applied Sciences
 * 29-01-2021
 * <p>
 * Non-visual class that:
 * Downloads a gzip file and md5 file.
 * Checks the md5sum.
 * Unzip the gzip file.
 * Reads the file and places the content in a HashMap.
 * Reads all OpenHuman files in a specified directory.
 * Compares the SNPs found, to find disease inducing SNPs a child could carry.
 * Writes all disease inducing SNPs to a tsv file.
 */
public class SNPinder {

    private HashMap<String, Variant> pathoSNPs;
    private ArrayList<String[]> pathogenicSNPs;

    /**
     * The main function is called on when the code is run.
     *
     * @param args - NA
     */
    public static void main(String[] args) {

        SNPinder app = new SNPinder();
        app.get_md5();
        app.check_md5sum();
        app.unzip_variant_summaryFile();
        app.read_variant_summaryFile();
        app.read_OpenHumanSNPFiles();

    }

    /**
     * Downloads the variant_summary md5 file.
     */
    public void get_md5() {

        System.out.println("Downloading md5sum...");
        try {

            URL url = new URL("ftp://ftp.ncbi.nlm.nih.gov/pub/clinvar/tab_delimited/variant_summary.txt.gz.md5");
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream("variant_summary.txt.gz.md5");
            fileOutputStream.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        System.out.println("Download successful.");
    }

    /**
     * Downloads the variant_summary gzip file.
     * Calls on the check_md5sum() method to check if the file was downloaded correctly.
     */
    public void getVariant_summaryFile() {

        System.out.println("Downloading variant_summary gzip file...");
        try {

            URL url = new URL("ftp://ftp.ncbi.nlm.nih.gov/pub/clinvar/tab_delimited/variant_summary.txt.gz");
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream("variant_summary.txt.gz");
            fileOutputStream.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            System.out.println("Download successful.");
            check_md5sum();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether the md5sum of the variant_summary gzip file, matches the md5 file.
     * If the file doesn't exist or the md5sum doesn't match the gzip file is downloaded again, using the getVariant_summaryFile() method.
     */
    public void check_md5sum() {

        System.out.println("Checking md5sum.");
        try {

            Runtime rt = Runtime.getRuntime();
            String check_md5_of_zipfile = "md5sum variant_summary.txt.gz";
            String check_md5 = "cat variant_summary.txt.gz.md5";

            File data = new File("/home/gabevdh/IdeaProjects/BI-6a_eindopdracht_Gabe_van_den_Hoeven/variant_summary.txt.gz");
            if (!data.exists()) {
                System.out.println("variant_summary gzip file does not exist.");
                System.out.println("Downloading now.");
                getVariant_summaryFile();
            }

            Process process = rt.exec(check_md5_of_zipfile);
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s = input.readLine();
            String md5sum_zipfile = s.split("  ")[0];

            process = rt.exec(check_md5);
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            s = input.readLine();
            String md5 = s.split("  ")[0];

            if (!(md5sum_zipfile.equals(md5))) {
                System.out.println("md5sum does not match.");
                System.out.println("Downloading variant_summary gzip file again.");
                getVariant_summaryFile();
            }

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        System.out.println("md5sum matched.");
    }

    /**
     * Unzips the variant_summary gzip file.
     */
    public void unzip_variant_summaryFile() {

        System.out.println("Unzipping variant_summary gzip file...");
        try {
            String gzip_filepath = "/home/gabevdh/IdeaProjects/BI-6a_eindopdracht_Gabe_van_den_Hoeven/variant_summary.txt.gz";
            String unzipped_filepath = "/home/gabevdh/IdeaProjects/BI-6a_eindopdracht_Gabe_van_den_Hoeven/variant_summary.txt";

            byte[] buffer = new byte[1024];

            FileInputStream fileIn = new FileInputStream(gzip_filepath);
            GZIPInputStream gzipInputStream = new GZIPInputStream(fileIn);
            FileOutputStream fileOutputStream = new FileOutputStream(unzipped_filepath);

            int bytes_read;
            while ((bytes_read = gzipInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bytes_read);
            }
            gzipInputStream.close();
            fileOutputStream.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done.");
    }

    /**
     * Reads the content of the variant_summary text file.
     * Creates a HashMap with as the:
     * key -  The RS ID of the current SNP.
     * value - Object of the class Variant.
     */
    public void read_variant_summaryFile() {

        System.out.println("Processing variant_summary file.");
        try {
            pathoSNPs = new HashMap<>();
            BufferedReader inFile = new BufferedReader(new FileReader("/home/gabevdh/IdeaProjects/BI-6a_eindopdracht_Gabe_van_den_Hoeven/variant_summary.txt"));
            String line = inFile.readLine();
            while ((line = inFile.readLine()) != null) {
                String[] row = line.split("\t");

                String rsId = row[9];
                int alleleId = Integer.parseInt(row[0]);
                String type = row[1];
                int position = Integer.parseInt(row[31]);
                int pathogenicity = Integer.parseInt(row[7]);
                int geneId = Integer.parseInt(row[3]);
                String alternateAllele = row[33];
                String disease = row[13];
                String referenceAllele = row[32];
                String chromosome = row[18];

                Variant v = new Variant(alleleId, type, position, pathogenicity, geneId,
                        alternateAllele, disease, referenceAllele, chromosome);
                if (v.getPathogenicity() == 1) {
                    pathoSNPs.put(rsId, v);
                } else if (v.getPathogenicity() == 0) {
                    pathoSNPs.putIfAbsent(rsId, v);
                }
            }
            System.out.println("variant_summary file processed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the OpenHumanSNP files and for each file, places all SNP in a HashMap.
     * These HashMaps are then added to an ArrayList.
     */
    public void read_OpenHumanSNPFiles() {

        System.out.println("Processing OpenHuman files.");
        ArrayList<HashMap<String, OpenHumanSNP>> allFiles = new ArrayList<>();
        File dir = new File("/home/gabevdh/IdeaProjects/BI-6a_eindopdracht_Gabe_van_den_Hoeven/OpenHuman");
        File[] files = dir.listFiles();
        assert files != null;
        for (File file : files) {
            try {
                String parentID = file.getName().split(".23andme.")[0];
                HashMap<String, OpenHumanSNP> allSNPs = new HashMap<>();
                BufferedReader inFile = new BufferedReader(new FileReader(file));
                String line;
                while ((line = inFile.readLine()) != null) {
                    if (!(line.startsWith("#"))) {
                        String[] row = line.split("\t");

                        String rsId = "";
                        if (row[0].startsWith("rs")) {
                            rsId = row[0].replace("rs", "");
                        } else if (row[0].startsWith("i")) {
                            rsId = row[0].replace("i", "");
                        }
                        String chromosome = row[1];
                        int position = Integer.parseInt(row[2]);
                        String genotype = row[3];
                        OpenHumanSNP snp = new OpenHumanSNP(parentID, rsId, chromosome, position, genotype);
                        allSNPs.putIfAbsent(rsId, snp);
                    }
                }
                allFiles.add(allSNPs);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("All OpenHuman files processed.");
        System.out.println("Comparing SNPs.");
        pathogenicSNPs = new ArrayList<>();

        System.out.println("Current pair: " + allFiles.get(0).get("2772695").getParentId() + " and " + allFiles.get(1).get("2772695").getParentId());
        compare_OpenHumanSNPs(allFiles.get(0), allFiles.get(1));
        System.out.println("Current pair: " + allFiles.get(0).get("2772695").getParentId() + " and " + allFiles.get(2).get("2772695").getParentId());
        compare_OpenHumanSNPs(allFiles.get(0), allFiles.get(2));
        System.out.println("Current pair: " + allFiles.get(0).get("2772695").getParentId() + " and " + allFiles.get(3).get("2772695").getParentId());
        compare_OpenHumanSNPs(allFiles.get(0), allFiles.get(3));
        System.out.println("Current pair: " + allFiles.get(0).get("2772695").getParentId() + " and " + allFiles.get(4).get("2772695").getParentId());
        compare_OpenHumanSNPs(allFiles.get(0), allFiles.get(4));
        System.out.println("Current pair: " + allFiles.get(0).get("2772695").getParentId() + " and " + allFiles.get(5).get("2772695").getParentId());
        compare_OpenHumanSNPs(allFiles.get(0), allFiles.get(5));

        System.out.println("Current pair: " + allFiles.get(1).get("2772695").getParentId() + " and " + allFiles.get(2).get("2772695").getParentId());
        compare_OpenHumanSNPs(allFiles.get(1), allFiles.get(2));
        System.out.println("Current pair: " + allFiles.get(1).get("2772695").getParentId() + " and " + allFiles.get(3).get("2772695").getParentId());
        compare_OpenHumanSNPs(allFiles.get(1), allFiles.get(3));
        System.out.println("Current pair: " + allFiles.get(1).get("2772695").getParentId() + " and " + allFiles.get(4).get("2772695").getParentId());
        compare_OpenHumanSNPs(allFiles.get(1), allFiles.get(4));
        System.out.println("Current pair: " + allFiles.get(1).get("2772695").getParentId() + " and " + allFiles.get(5).get("2772695").getParentId());
        compare_OpenHumanSNPs(allFiles.get(1), allFiles.get(5));

        System.out.println("Current pair: " + allFiles.get(2).get("2772695").getParentId() + " and " + allFiles.get(3).get("2772695").getParentId());
        compare_OpenHumanSNPs(allFiles.get(2), allFiles.get(3));
        System.out.println("Current pair: " + allFiles.get(2).get("2772695").getParentId() + " and " + allFiles.get(4).get("2772695").getParentId());
        compare_OpenHumanSNPs(allFiles.get(2), allFiles.get(4));
        System.out.println("Current pair: " + allFiles.get(2).get("2772695").getParentId() + " and " + allFiles.get(5).get("2772695").getParentId());
        compare_OpenHumanSNPs(allFiles.get(2), allFiles.get(5));

        System.out.println("Current pair: " + allFiles.get(3).get("2772695").getParentId() + " and " + allFiles.get(4).get("2772695").getParentId());
        compare_OpenHumanSNPs(allFiles.get(3), allFiles.get(4));
        System.out.println("Current pair: " + allFiles.get(3).get("2772695").getParentId() + " and " + allFiles.get(5).get("2772695").getParentId());
        compare_OpenHumanSNPs(allFiles.get(3), allFiles.get(5));

        System.out.println("Current pair: " + allFiles.get(4).get("2772695").getParentId() + " and " + allFiles.get(5).get("2772695").getParentId());
        compare_OpenHumanSNPs(allFiles.get(4), allFiles.get(5));

        write_to_tsvFile(pathogenicSNPs);
    }

    /**
     * Compares SNPs from both files, to see if a nucleotide combination causes a disease.
     *
     * @param snpFile1 - HashMap<OpenHumanSNP> - All SNPs from the first parent.
     * @param snpFile2 - HashMap<OpenHumanSNP> - All SNPs from the second parent.
     */
    public void compare_OpenHumanSNPs(HashMap<String, OpenHumanSNP> snpFile1, HashMap<String, OpenHumanSNP> snpFile2) {

        HashMap<String, OpenHumanSNP> small = snpFile1.size() < snpFile2.size() ? snpFile1 : snpFile2;
        HashMap<String, OpenHumanSNP> big = snpFile1.size() > snpFile2.size() ? snpFile1 : snpFile2;

        for (String rsId : small.keySet()) {
            if (big.containsKey(rsId) && pathoSNPs.containsKey(rsId)) {
                OpenHumanSNP snp1 = small.get(rsId);
                OpenHumanSNP snp2 = big.get(rsId);
                Variant pathoSNP = pathoSNPs.get(rsId);
                if (pathoSNP.getPosition() == snp1.getPosition() && pathoSNP.getChromosome().equals(snp1.getChromosome()) && pathoSNP.getPathogenicity() == 1
                        && pathoSNP.getPosition() == snp2.getPosition() && pathoSNP.getChromosome().equals(snp2.getChromosome())) {

                    boolean cause_disease = false;

                    // Retrieve genotypes of the parents to compare nucleotides.
                    String[] genotype1 = new String[]{"", ""};
                    genotype1[0] = String.valueOf(snp1.getGenotype().charAt(0));
                    if (snp1.getGenotype().length() == 2) {
                        genotype1[1] = String.valueOf(snp1.getGenotype().charAt(1));
                    }
                    String[] genotype2 = new String[]{"", ""};
                    genotype2[0] = String.valueOf(snp2.getGenotype().charAt(0));
                    if (snp2.getGenotype().length() == 2) {
                        genotype2[1] = String.valueOf(snp2.getGenotype().charAt(1));
                    }

                    // Check if the alternate allele is present in the genotype of the parents.
                    String patho_genotype = "";
                    String genotype_1 = Arrays.toString(genotype1).replace("[", "").replace(", ", "").replace("]", "");
                    String genotype_2 = Arrays.toString(genotype2).replace("[", "").replace(", ", "").replace("]", "");
                    for (String allele : genotype1) {
                        if (allele.equals(pathoSNP.getAlternateAllele())) {
                            cause_disease = true;
                            patho_genotype = genotype_1;
                            break;
                        }
                    }
                    for (String allele : genotype2) {
                        if (allele.equals(pathoSNP.getAlternateAllele())) {
                            cause_disease = true;
                            patho_genotype = genotype_2;
                            break;
                        }
                    }

                    // If the alternate allele is present, it get added to an ArrayList.
                    if (cause_disease) {
                        String[] patho_snp = new String[]{rsId, patho_genotype, pathoSNP.getChromosome(),
                                genotype_1, genotype_2, snp1.getParentId(), snpFile2.get("2772695").getParentId()};
                        pathogenicSNPs.add(patho_snp);
                    }
                }
            }
        }
    }

    /**
     * Writes any disease inducing genotypes for a child between two parents to a tsv file.
     *
     * @param pathogenicSNPs - ArrayList<String[]> - An ArrayList with all SNPs between two parents in String arrays.
     *                       The String arrays contain the:
     *                       <p>
     *                       rsId - String - The RS ID of the pathogenic SNP.
     *                       patho_genotype - String - The genotype of the pathogenic SNP.
     *                       chromosome - String - The chromosome the SNP is located on.
     *                       genotype1 - String - The genotype of the first parent.
     *                       genotype2 - String - The genotype of the second parent.
     *                       parentId1 - String - The parent ID of the first parent.
     *                       parentId2 - String - The parent ID of the second parent.
     */
    public void write_to_tsvFile(ArrayList<String[]> pathogenicSNPs) {

        System.out.println("Writing pathogenic SNPs to tsv file...");
        try {
            File file = new File("/home/gabevdh/IdeaProjects/BI-6a_eindopdracht_Gabe_van_den_Hoeven/Pathogenic_SNPs.tsv");
            if (!(pathogenicSNPs.isEmpty())) {
                if (!(file.exists())) {
                    BufferedWriter write_file = new BufferedWriter(new FileWriter("Pathogenic_SNPs.tsv"));
                    String line = "RS ID\tpathogenic genotype\tchromosome\tgenotype parent 1\tgenotype parent 2\t parent ID 1\t parent ID 2\n\n";
                    write_file.write(line);
                    write_file.close();
                }

                String line = "";
                BufferedWriter writer = new BufferedWriter(new FileWriter("Pathogenic_SNPs.tsv", true));
                for (String[] snp : pathogenicSNPs) {
                    line = snp[0] + "\t" + snp[1] + "\t" + snp[2] + "\t" + snp[3] + "\t" + snp[4] + "\t" + snp[5] + "\t" + snp[6] + "\n";
                    writer.append(line);
                }
                writer.append(line);
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished writing.");
    }
}
