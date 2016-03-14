package com.itertk.app.mpos;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.bbpos.wisepad.WisePadController;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Administrator on 2014/10/14.
 * Bbpos刷卡器
 */
public class BBPos implements WisePadController.WisePadControllerListener{
    final static private String TAG = "BBPos";
    private WisePadController wisePadController;
    BluetoothDevice bluetoothDevice;

    private boolean isConnected;

    public BBPos(Context context){
        wisePadController = new WisePadController(context, this);

        isConnected  = false;

        Object[] pairedObjects = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
        if (pairedObjects.length <= 0){
            return;
        }
        bluetoothDevice = (BluetoothDevice)pairedObjects[0];

    }

    public void resetDevice(){
        wisePadController.resetWisePadController();
    }


    private ConnectDeviceListener connectDeviceListener;

    public boolean isConnected() {
        return isConnected;
    }

    public interface ConnectDeviceListener{
        public void onConnectDeviceListener(boolean success);
    }
    public void connectDevice(ConnectDeviceListener connectDeviceListener){
        this.connectDeviceListener = connectDeviceListener;
        if(bluetoothDevice != null)
            wisePadController.startBTv2(bluetoothDevice);
    }

    public void disconnectDevice(){
        wisePadController.stopBTv2();
    }


    private CheckCardListener checkCardListener;
    public interface CheckCardListener{
        public void onCheckCardListener(boolean cancel, WisePadController.CheckCardResult checkCardResult, Hashtable<String, String> stringStringHashtable);
    }

    public void checkCard(CheckCardListener checkCardListener){
        this.checkCardListener = checkCardListener;
        wisePadController.checkCard();
    }

    public void cancelCheckCard(){
        wisePadController.cancelCheckCard();
    }

    private StartPinEntryListener startPinEntryListener;
    public interface StartPinEntryListener{
        public void onStartPinEntryListener(WisePadController.PinEntryResult pinEntryResult, String epb, String ksn);
    }

    public void startPinEntry(StartPinEntryListener startPinEntryListener){
        this.startPinEntryListener = startPinEntryListener;
        wisePadController.startPinEntry();
    }

    @Override
    public void onWaitingForCard() {
        Log.d(TAG, "onWaitingForCard ");
    }

    @Override
    public void onBTv2Detected() {

    }

    @Override
    public void onBTv2Connected(BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "onBTv2Connected " + bluetoothDevice.getName());
        isConnected = true;
        connectDeviceListener.onConnectDeviceListener(true);
    }

    @Override
    public void onBTv2Disconnected() {
        Log.d(TAG, "onBTv2Disconnected ");
        isConnected = false;
    }

    @Override
    public void onBTv4DeviceListRefresh(List<BluetoothDevice> bluetoothDevices) {

    }

    @Override
    public void onBTv4Connected() {

    }

    @Override
    public void onBTv4Disconnected() {

    }

    @Override
    public void onBTv4ScanStopped() {

    }

    @Override
    public void onBTv4ScanTimeout() {

    }

    @Override
    public void onReturnCheckCardResult(WisePadController.CheckCardResult checkCardResult, Hashtable<String, String> stringStringHashtable) {
        Log.d(TAG, "onReturnCheckCardResult " + checkCardResult.toString());
        if(checkCardListener != null)
            checkCardListener.onCheckCardListener(false, checkCardResult, stringStringHashtable);
    }

    @Override
    public void onReturnCancelCheckCardResult(boolean b) {
        Log.d(TAG, "onReturnCancelCheckCardResult " + b);
        if(checkCardListener != null)
            checkCardListener.onCheckCardListener(true, null, null);
    }

    @Override
    public void onReturnStartEmvResult(WisePadController.StartEmvResult startEmvResult, String s) {

    }

    @Override
    public void onReturnDeviceInfo(Hashtable<String, String> stringStringHashtable) {
        Log.d(TAG, "onReturnDeviceInfo " + stringStringHashtable.toString());
    }

    @Override
    public void onReturnTransactionResult(WisePadController.TransactionResult transactionResult) {

    }

    @Override
    public void onReturnTransactionResult(WisePadController.TransactionResult transactionResult, Hashtable<String, String> stringStringHashtable) {

    }

    @Override
    public void onReturnBatchData(String s) {

    }

    @Override
    public void onReturnTransactionLog(String s) {

    }

    @Override
    public void onReturnReversalData(String s) {

    }

    @Override
    public void onReturnAmountConfirmResult(boolean b) {

    }

    @Override
    public void onReturnPinEntryResult(WisePadController.PinEntryResult pinEntryResult, String s, String s2) {
        Log.d(TAG, "onReturnPinEntryResult " + pinEntryResult.toString() + " Encrypted PIN block=" + s + " Key Serial Number=" + s2);
        if(startPinEntryListener != null)
            startPinEntryListener.onStartPinEntryListener(pinEntryResult, s, s2);
    }

    @Override
    public void onReturnPrinterResult(WisePadController.PrinterResult printerResult) {

    }

    @Override
    public void onReturnAmount(String s, String s2) {

    }

    @Override
    public void onReturnUpdateTerminalSettingResult(WisePadController.TerminalSettingStatus terminalSettingStatus) {

    }

    @Override
    public void onReturnReadTerminalSettingResult(WisePadController.TerminalSettingStatus terminalSettingStatus, String s) {

    }

    @Override
    public void onReturnEnableInputAmountResult(boolean b) {

    }

    @Override
    public void onReturnDisableInputAmountResult(boolean b) {

    }

    @Override
    public void onReturnPhoneNumber(WisePadController.PhoneEntryResult phoneEntryResult, String s) {

    }

    @Override
    public void onReturnEmvCardDataResult(boolean b, String s) {

    }

    @Override
    public void onReturnEncryptDataResult(String s, String s2) {

    }

    @Override
    public void onRequestSelectApplication(ArrayList<String> strings) {

    }

    @Override
    public void onRequestSetAmount() {

    }

    @Override
    public void onRequestPinEntry() {
        Log.d(TAG, "onRequestPinEntry");
    }

    @Override
    public void onRequestCheckServerConnectivity() {

    }

    @Override
    public void onRequestOnlineProcess(String s) {

    }

    @Override
    public void onRequestTerminalTime() {

    }

    @Override
    public void onRequestDisplayText(WisePadController.DisplayText displayText) {

    }

    @Override
    public void onRequestClearDisplay() {

    }

    @Override
    public void onRequestReferProcess(String s) {

    }

    @Override
    public void onRequestAdviceProcess(String s) {

    }

    @Override
    public void onRequestFinalConfirm() {

    }

    @Override
    public void onRequestVerifyID(String s) {

    }

    @Override
    public void onRequestInsertCard() {

    }

    @Override
    public void onRequestPrinterData(int i, boolean b) {

    }

    @Override
    public void onPrinterOperationEnd() {

    }

    @Override
    public void onBatteryLow(WisePadController.BatteryStatus batteryStatus) {

    }

    @Override
    public void onBTv2DeviceNotFound() {

    }

    @Override
    public void onAudioDeviceNotFound() {

    }

    @Override
    public void onDevicePlugged() {

    }

    @Override
    public void onDeviceUnplugged() {

    }

    @Override
    public void onError(WisePadController.Error error) {
        Log.d(TAG, "onError " + error.toString());

        if (error == WisePadController.Error.FAIL_TO_START_BTV2)
        {
            if(connectDeviceListener != null){
                connectDeviceListener.onConnectDeviceListener(false);
            }
        }
    }
}
