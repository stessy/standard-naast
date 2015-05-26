package standardNaast.types;

public enum AccountingType {
	ENTRY("Entrée"), EXIT("Sortie");

	private String value;

	private AccountingType(final String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}
}
