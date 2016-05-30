package recipeIntegration;

class Step {
	private int Number;
	private String instruction;
	
	public Step(int Number, String instruction){
		this.Number = Number;
		this.instruction = instruction;
	}

	public int getNumber() {
		return Number;
	}

	public void setNumber(int number) {
		Number = number;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

}
