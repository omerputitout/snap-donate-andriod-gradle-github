package com.snapdonate.app.interfaces;

public interface OnDataSendCompletion {
	public void onStartSendingDataToServer();
	public void onCompleteSendingDataToServer(String response);
}
