/**
 * @author GCW van den Hoeven (Gabe)
 * student Bio-informatics, HAN University of Applied Sciences
 * 29-01-2021
 * <p>
 * Class to store a variant SNP.
 * implements the Comparable Interface.
 */
public class Variant implements Comparable<Variant> {

    private final int alleleId;
    private final String type;
    private final int position;
    private final int pathogenicity;
    private final int geneId;
    private final String alternateAllele;
    private final String disease;
    private final String referenceAllele;
    private final String chromosome;

    /**
     * Constructor for a Variant object.
     *
     * @param alleleId        - int - The Allele ID of the Variant SNP.
     * @param type            - String - What type of SNP.
     * @param position        - int - The position on the chromosome that the SNP is located.
     * @param pathogenicity   - int - whether the SNP is believed to be pathogenic.
     *                        1 for pathogenic, 0 for nonpathogenic or uncertain.
     * @param geneId          - int - The ID of the gene that the SNP is located on.
     * @param alternateAllele - String - The alternate allele that is causing the SNP
     * @param disease         - String - The disease that the SNP is causing
     * @param referenceAllele - String - The allele that is found on the same position as the alternate allele but in the reference genome
     * @param chromosome      - String - The chromosome the SNP is located on.
     */
    Variant(int alleleId, String type, int position, int pathogenicity, int geneId,
            String alternateAllele, String disease, String referenceAllele, String chromosome) {
        this.alleleId = alleleId;
        this.type = type;
        this.position = position;
        this.pathogenicity = pathogenicity;
        this.geneId = geneId;
        this.alternateAllele = alternateAllele;
        this.disease = disease;
        this.referenceAllele = referenceAllele;
        this.chromosome = chromosome;
    }

    public int getAlleleId() {
        return alleleId;
    }

    public String getType() {
        return type;
    }

    public int getPosition() {
        return position;
    }

    public int getPathogenicity() {
        return pathogenicity;
    }

    public int getGeneId() {
        return geneId;
    }

    public String getAlternateAllele() {
        return alternateAllele;
    }

    public String getDisease() {
        return disease;
    }

    public String getReferenceAllele() {
        return referenceAllele;
    }

    public String getChromosome() {
        return chromosome;
    }

    @Override
    public int compareTo(Variant v) {
        return this.chromosome.compareTo(v.chromosome);
    }

    @Override
    public String toString() {
        return "AlleleId: " + alleleId +
                ", pathogenicity: " + pathogenicity +
                ", phenotype: " + disease +
                ", referenceAllele: " + referenceAllele +
                ", alternateAllele: " + alternateAllele +
                ", chromosome: " + chromosome +
                ", position: " + position;
    }
}
