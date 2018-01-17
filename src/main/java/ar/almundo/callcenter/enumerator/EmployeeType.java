package ar.almundo.callcenter.enumerator;

public enum EmployeeType {
	OPERATOR(3), SUPERVISOR(2), DIRECTOR(1);
	
	
	private int level;

	EmployeeType(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
    
	public String toLowerCase(){
		return toString().toLowerCase();
	}
	
	public String toUpperCase(){
		return toString().toUpperCase();
	}
	
}
