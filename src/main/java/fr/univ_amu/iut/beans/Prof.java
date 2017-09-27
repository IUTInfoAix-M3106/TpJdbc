package fr.univ_amu.iut.beans;

public class Prof {
    private int numProf;
    private String nomProf;
    private String prenomProf;
    private String adrProf;
    private String cpProf;
    private String villeProf;
    private Module matSpec;

    public Prof() {
    }

    public String getAdrProf() {
        return adrProf;
    }

    public void setAdrProf(String adrProf) {
        this.adrProf = adrProf;
    }

    public String getCpProf() {
        return cpProf;
    }

    public void setCpProf(String cpProf) {
        this.cpProf = cpProf;
    }

    public Module getMatSpec() {
        return matSpec;
    }

    public void setMatSpec(Module matSpec) {
        this.matSpec = matSpec;
    }

    public String getNomProf() {
        return nomProf;
    }

    public void setNomProf(String nomProf) {
        this.nomProf = nomProf;
    }

    public int getNumProf() {
        return numProf;
    }

    public void setNumProf(int numProf) {
        this.numProf = numProf;
    }

    public String getPrenomProf() {
        return prenomProf;
    }

    public void setPrenomProf(String prenomProf) {
        this.prenomProf = prenomProf;
    }

    public String getVilleProf() {
        return villeProf;
    }

    public void setVilleProf(String villeProf) {
        this.villeProf = villeProf;
    }

    @Override
    public String toString() {
        return "Prof [numProf=" + numProf + ", "
                + (nomProf != null ? "nomProf=" + nomProf + ", " : "")
                + (prenomProf != null ? "prenomProf=" + prenomProf + ", " : "")
                + (adrProf != null ? "adrProf=" + adrProf + ", " : "")
                + (cpProf != null ? "cpProf=" + cpProf + ", " : "")
                + (villeProf != null ? "villeProf=" + villeProf + ", " : "")
                + (matSpec != null ? "matSpec=" + matSpec.getCode() : "") + "]";
    }
}
