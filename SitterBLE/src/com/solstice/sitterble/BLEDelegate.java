package com.solstice.sitterble;

public interface BLEDelegate {

	public void deviceConnected();

	public void deviceDisconnected();

	public void weightSensorChanged(boolean weightPresent);

	public void tempSensorChanged(float temp);
}
