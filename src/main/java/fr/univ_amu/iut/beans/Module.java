package fr.univ_amu.iut.beans;

public class Module {
    private String code;
    private String libelle;
    private int hCoursPrev;
    private int hCoursRea;
    private int hTpPrev;
    private int hTpRea;
    private String discipline;
    private int coefTest;
    private int coefCc;
    private Prof responsable;
    private Module pere;

    public Module() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCoefCc() {
        return coefCc;
    }

    public void setCoefCc(int coefCc) {
        this.coefCc = coefCc;
    }

    public int getCoefTest() {
        return coefTest;
    }

    public void setCoefTest(int coeffTest) {
        this.coefTest = coeffTest;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public int gethCoursPrev() {
        return hCoursPrev;
    }

    public void sethCoursPrev(int hCoursPrev) {
        this.hCoursPrev = hCoursPrev;
    }

    public int gethCoursRea() {
        return hCoursRea;
    }

    public void sethCoursRea(int hCoursRea) {
        this.hCoursRea = hCoursRea;
    }

    public int gethTpPrev() {
        return hTpPrev;
    }

    public void sethTpPrev(int hTpPrev) {
        this.hTpPrev = hTpPrev;
    }

    public int gethTpRea() {
        return hTpRea;
    }

    public void sethTpRea(int hTpRea) {
        this.hTpRea = hTpRea;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Module getPere() {
        return pere;
    }

    public void setPere(Module pere) {
        this.pere = pere;
    }

    public Prof getResponsable() {
        return responsable;
    }

    public void setResponsable(Prof responsable) {
        this.responsable = responsable;
    }

    @Override
    public String toString() {
        return "Module [" + (code != null ? "code=" + code + ", " : "")
                + (libelle != null ? "libelle=" + libelle + ", " : "")
                + "hCoursPrev=" + hCoursPrev + ", hCoursRea=" + hCoursRea
                + ", hTpPrev=" + hTpPrev + ", hTpRea=" + hTpRea + ", "
                + (discipline != null ? "discipline=" + discipline + ", " : "")
                + "coeffTest=" + coefTest + ", coefCc=" + coefCc + ", "
                + (responsable != null ? "responsable=" + responsable.getNomProf() + ", " : "")
                + (pere != null ? "pere=" + pere.getCode() : "") + "]";
    }
}
