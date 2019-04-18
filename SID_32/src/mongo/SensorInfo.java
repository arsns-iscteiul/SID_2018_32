package mongo;

import java.util.Date;

public class SensorInfo {
	private double temp;
	private double hum;
	private String dat;
	private String tim;
	private int cell;
	private String sens;
	private boolean exported;
	
	public SensorInfo(String [] s) {
		super();
		
	}

	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}

	public double getHum() {
		return hum;
	}

	public void setHum(double hum) {
		this.hum = hum;
	}

	public String getDat() {
		return dat;
	}

	public void setDat(String dat) {
		this.dat = dat;
	}

	public String getTim() {
		return tim;
	}

	public void setTim(String tim) {
		this.tim = tim;
	}

	public int getCell() {
		return cell;
	}

	public void setCell(int cell) {
		this.cell = cell;
	}

	public String getSens() {
		return sens;
	}

	public void setSens(String sens) {
		this.sens = sens;
	}

	public boolean isExported() {
		return exported;
	}

	public void setExported(boolean exported) {
		this.exported = exported;
	}
	
	
}
