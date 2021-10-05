package practica5;

public class ControlMessage implements Msg {

	String action;
	
	public ControlMessage(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	@Override
	public void showMessage(String string) {
		System.out.println(string + action);
	}
	
}
