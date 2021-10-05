package practica5;

import java.util.LinkedList;

public class BroadcastMessage implements Msg {

	private int id;
	private LinkedList<Integer> sensors;
	
	public BroadcastMessage(int id, Integer...s) {
		this.id = id;
		this.sensors = new LinkedList<Integer>();
		for (Integer integer : s) {
			sensors.add(integer);
		}
	}
	
	public int getId() {
		return id;
	}

	public LinkedList<Integer> getSensors() {
		return new LinkedList<Integer>(sensors);
	}

	public void showMessage(String format) {
		String sensores = format + "<ServidorId=" + this.id + ">";
		if (sensors.size() == 6) {
			sensores = sensores.concat("<Temperatura=" + sensors.get(0) + ">");
			sensores = sensores.concat("<Viento=" + sensors.get(1) + ">");
			sensores = sensores.concat("<Humedad(%)=" + sensors.get(2) + ">");
			sensores = sensores.concat("<PPM=" + sensors.get(3) + ">");
			sensores = sensores.concat("<SO2(%)=" + sensors.get(4) + ">");
			sensores = sensores.concat("<O3(%)=" + sensors.get(5) + ">");
		} 
		else if (sensors.size() == 5) {
			sensores = sensores.concat("<Temperatura=" + sensors.get(0) + ">");
			sensores = sensores.concat("<Viento=" + sensors.get(1) + ">");
			sensores = sensores.concat("<Humedad(%)=" + sensors.get(2) + ">");
			sensores = sensores.concat("<PPM=" + sensors.get(3) + ">");
			sensores = sensores.concat("<SO2(%)=" + sensors.get(4) + ">");
		}
		else if (sensors.size() == 4) {
			sensores = sensores.concat("<Temperatura=" + sensors.get(0) + ">");
			sensores = sensores.concat("<Viento=" + sensors.get(1) + ">");
			sensores = sensores.concat("<Humedad(%)=" + sensors.get(2) + ">");
			sensores = sensores.concat("<PPM=" + sensors.get(3) + ">");
		}
		else if (sensors.size() == 3) {
			sensores = sensores.concat("<Temperatura=" + sensors.get(0) + ">");
			sensores = sensores.concat("<Viento=" + sensors.get(1) + ">");
			sensores = sensores.concat("<Humedad(%)=" + sensors.get(2) + ">");
		}
		else if (sensors.size() == 2) {
			sensores = sensores.concat("<Temperatura=" + sensors.get(0) + ">");
			sensores = sensores.concat("<Viento=" + sensors.get(1) + ">");					
		}
		else {
			sensores = sensores.concat("<Temperatura=" + sensors.get(0) + ">");
		}
		System.out.println(sensores);		
	}
	
	public String getMessage(String format) {
		String sensores = format + "\rServidorId=" + this.id + "\r";
		if (sensors.size() == 6) {
			sensores = sensores.concat("Temperatura=" + sensors.get(0)+ "\r");
			sensores = sensores.concat("Viento=" + sensors.get(1)+ "\r");
			sensores = sensores.concat("Humedad(%)=" + sensors.get(2)+ "\r");
			sensores = sensores.concat("PPM=" + sensors.get(3)+ "\r");
			sensores = sensores.concat("SO2(%)=" + sensors.get(4)+ "\r");
			sensores = sensores.concat("O3(%)=" + sensors.get(5)+ "\r");
		} 
		else if (sensors.size() == 5) {
			sensores = sensores.concat("Temperatura=" + sensors.get(0)+ "\r");
			sensores = sensores.concat("Viento=" + sensors.get(1)+ "\r");
			sensores = sensores.concat("Humedad(%)=" + sensors.get(2)+ "\r");
			sensores = sensores.concat("PPM=" + sensors.get(3)+ "\r");
			sensores = sensores.concat("SO2(%)=" + sensors.get(4)+ "\r");
		}
		else if (sensors.size() == 4) {
			sensores = sensores.concat("<Temperatura=" + sensors.get(0)+ "\r");
			sensores = sensores.concat("Viento=" + sensors.get(1)+ "\r");
			sensores = sensores.concat("Humedad(%)=" + sensors.get(2)+ "\r");
			sensores = sensores.concat("PPM=" + sensors.get(3)+ "\r");
		}
		else if (sensors.size() == 3) {
			sensores = sensores.concat("Temperatura=" + sensors.get(0)+ "\r");
			sensores = sensores.concat("Viento=" + sensors.get(1)+ "\r");
			sensores = sensores.concat("Humedad(%)=" + sensors.get(2)+ "\r");
		}
		else if (sensors.size() == 2) {
			sensores = sensores.concat("Temperatura=" + sensors.get(0)+ "\r");
			sensores = sensores.concat("Viento=" + sensors.get(1)+ "\r");					
		}
		else {
			sensores = sensores.concat("Temperatura=" + sensors.get(0)+ "\r");
		}
		return sensores;		
	}
	
}
