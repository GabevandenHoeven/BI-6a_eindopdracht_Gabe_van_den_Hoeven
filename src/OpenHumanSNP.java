/**
 * @author GCW van den Hoeven (Gabe)
 * student Bio-informatics, HAN University of Applied Sciences
 * 29-01-2021
 * <p>
 * Class to store a SNP of an OpenHuman file.
 */
public class OpenHumanSNP {

    private final String parentId;
    private final String rsId;
    private final String chromosome;
    private final int position;
    private final String genotype;

    /**
     * Constructor for an OpenHumanSNP object.
     *
     * @param parentId   - String - The ID found in the filename referring to the person the SNP list belongs to.
     * @param rsId       - String - The RS ID is the unique ID given to this specific SNP.
     * @param chromosome - String - The chromosome the SNP is located on.
     * @param position   - int - The position on the chromosome that the SNP is located.
     * @param genotype   - String - The genotype found at the position of the SNP.
     */
    OpenHumanSNP(String parentId, String rsId, String chromosome, int position, String genotype) {
        this.parentId = parentId;
        this.rsId = rsId;
        this.chromosome = chromosome;
        this.position = position;
        this.genotype = genotype;
    }

    public String getParentId() {
        return parentId;
    }

    public String getRsId() {
        return rsId;
    }

    public String getChromosome() {
        return chromosome;
    }

    public int getPosition() {
        return position;
    }

    public String getGenotype() {
        return genotype;
    }

    @Override
    public String toString() {
        return "RSId: " + rsId;
    }
}
