package be.Lombardi.pojo;

public enum CategoryType {
	VTT_DESCENDEUR,
	VTT_RANDONNEUR,
	VTT_TRIALISTE,
	CYCLO;

    @Override
    public String toString() {
        return name();
    }
}