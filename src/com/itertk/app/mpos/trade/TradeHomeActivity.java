package com.itertk.app.mpos.trade;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.itertk.app.mpos.ExitActivity;
import com.itertk.app.mpos.PosSignInDialog;
import com.itertk.app.mpos.service.UploadOrderService;
import com.itertk.app.mpos.utility.BlueToothHelper;
import com.itertk.app.mpos.ConfirmDialog;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.MyPresentation;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.utility.Utils;
import com.itertk.app.mpos.dbhelper.Advertising;
import com.itertk.app.mpos.dbhelper.AdvertisingDao;
import com.itertk.app.mpos.dbhelper.DaoSession;
import com.itertk.app.mpos.dbhelper.Permission;
import com.itertk.app.mpos.dbhelper.PermissionDao;
import com.itertk.app.mpos.trade.more.MoreHomeActivity;
import com.itertk.app.mpos.trade.pos.PosActivity;
import com.itertk.app.mpos.trade.pos.PrintHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
*交易主界面
* */
public class TradeHomeActivity extends Activity {
    private final static String TAG = "TradeHomeActivity";
    public final static String KEY_PERMISSION_NAME = "NAME";
    public final static String KEY_CATEGORY_ID = "CATEGORY_ID";

    private DisplayImageOptions options;

    private List<Permission> mPermissions;
    private List<Advertising> mAdvertisings;
    private int mAdvIndex = 0;
    //smile
    private String scanCode;
    EditText textInputGun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_home);
        MyActionbar.setHomeActionBarLayout(this);
        scanCode = new String();
        textInputGun = (EditText) findViewById(R.id.textInputGun);
        textInputGun.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d("textInput", " enter " + event.getDevice().getName());
                if (event.getDevice().getName().contains("HID")) {
                    // Toast.makeText(TradeHomeActivity.this,"hid",Toast.LENGTH_SHORT);
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_ENTER) {
                            Log.d("textInput", " tradehome " + scanCode);

                            //   btnPay.setText("开始结算 ￥" + (new DecimalFormat("0.00")).format(saleOrderAdapter.getTotalPrice()));
//                            Message msg = Message.obtain(null, PosActivity.SET_SCANCODE, 0, 0);
//                            Bundle bd = new Bundle();
//                            bd.putString(PosActivity.SCANCODE_KEY,scanCode);
//                            msg.setData(bd);
//                            mHandler.sendMessage(msg);
                            Intent it = new Intent(TradeHomeActivity.this, PosActivity.class);
                            it.putExtra("scanCode", scanCode);
                            startActivity(it);

                            scanCode = "";
                        } else {
                            if(event.getScanCode()<10)
                                scanCode += event.getNumber();
                            else
                            {
                                scanCode += (char) event.getUnicodeChar();
                            }
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
        loadData();

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.more_service)
                .showImageForEmptyUri(R.drawable.more_service)
                .showImageOnFail(R.drawable.more_service)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        GridView listView = (GridView) findViewById(R.id.grid);
        listView.setAdapter(new ImageAdapter());

        final ImageView iv_adv = (ImageView) findViewById(R.id.iv_adv);
        Handler mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (mAdvertisings != null && mAdvertisings.size() > 0) {
                    String image_url = mAdvertisings.get(mAdvIndex).getValue();
                    ImageLoader.getInstance().displayImage(image_url, iv_adv, options);
                    mAdvIndex++;
                    if (mAdvIndex > mAdvertisings.size() - 1) {
                        mAdvIndex = 0;
                    }
                }
                sendEmptyMessageDelayed(1, 3000);
            }
        };
        mHandler.sendEmptyMessageDelayed(1, 3000);

        BlueToothHelper.enableBlueTooth();
 //       ((MPosApplication) getApplication()).initPrinter();
 //       PrintHelper.initPrinter(this);
//        BluetoothProfile.ServiceListener listener = new BluetoothProfile.ServiceListener() {
//            @Override
//            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
//
//               List<BluetoothDevice> list = bluetoothProfile.getConnectedDevices();
//                Log.i("Smile","blue device size: "+list.size());
//                for (BluetoothDevice bd : list)
//                {
//                    Log.i("Smile","blue device: "+bd.getName()+" addr: "+bd.getAddress());
//                }
//            }
//
//            @Override
//            public void onServiceDisconnected(int i) {
//
//            }
//        };
//        BlueToothHelper.getProfile(this,listener);
//        BlueToothHelper.isME30Connected();
//        BlueToothHelper.getConectedDevice(this);
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        this.registerReceiver(mReceiver, filter);
//
//        // Register for broadcasts when discovery has finished
//        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
//        this.registerReceiver(mReceiver, filter);
//
//
//        filter = new IntentFilter( BluetoothAdapter.ACTION_STATE_CHANGED );
//        this.registerReceiver( mReceiver, filter );

        if(needPosSign())
        {
            PosSignInDialog posSignInDialog =new PosSignInDialog(this, R.style.MyDialog);
            posSignInDialog.show();
        }
        //setPosSignDate(Utils.getTime());
        //loadPosSignDate();
//        Intent uploadService = new Intent(this, UploadOrderService.class);
//        uploadService.setAction(UploadOrderService.UPLOAD);
//        startService(uploadService);
    }

    private void loadData() {

        MPosApplication mPosApplication = MPosApplication.getInstance();

        DaoSession session = mPosApplication.getDataHelper().getDaoSession();
        PermissionDao permissionDao = session.getPermissionDao();

        QueryBuilder qb = permissionDao.queryBuilder();
        qb = qb.where(PermissionDao.Properties.PermissionType.eq("MAIN_PERMISSION"));
        if (!Utils.isMemberLogin()) {
            List<String> items = new ArrayList<String>();
            String[] excludePermissions = new String[]{"便民服务", "订货系统"};
            for(String permission : excludePermissions){
                items.add(permission);
            }
            qb.where(PermissionDao.Properties.Name.notIn(items));
        }
        mPermissions = qb.orderAsc(PermissionDao.Properties.Order).list();

        AdvertisingDao advertisingDao = session.getAdvertisingDao();
        mAdvertisings = advertisingDao.loadAll();
    }

    private boolean needPosSign() {
        Date pre = Utils.loadPosSignDate(this);
        Date now = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calpre = Calendar.getInstance();
        calpre.setTime(pre);
        int preyear = calpre.get(Calendar.YEAR);
        int premonth = calpre.get(Calendar.MONTH) + 1;
        int preday = calpre.get(Calendar.DATE);

        calpre.setTime(now);
        int year = calpre.get(Calendar.YEAR);
        int month = calpre.get(Calendar.MONTH) + 1;
        int day = calpre.get(Calendar.DATE);
        Log.i("smile", year + " " + month + " " + day);
        Log.i("smile", preyear + " " + premonth + " " + preday);
        if ((year > preyear) ||
                (year == preyear && month > premonth) ||
                (year == preyear && month == premonth && day > preday)
                ) {
            return true;
        }
        return false;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.trade_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
//        try {
//            PrintHelper.disConectDevice(this);
//        }catch(Exception e)
//        {
//
//        }
        super.onDestroy();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ConfirmDialog confirmDialog = new ConfirmDialog(this, R.style.MyDialog, "退出旺铺宝？");
            confirmDialog.setOnClickButtonOKListener(new ConfirmDialog.OnClickButtonOKListener() {
                @Override
                public void onClickButtonOK() {
                    // TradeHomeActivity.this.finish();
                    Intent i = new Intent(TradeHomeActivity.this, ExitActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                }
            });
            confirmDialog.show();
        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.d("dispatchTouchEvent", ev.toString());
//        if (ev.getDeviceId() == 3) return false;
//        return super.dispatchTouchEvent(ev);
//    }
//
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        Log.d("dispatchKeyEvent", event.toString());
//        if(event.getDeviceId() == 3) return false;
//        return super.dispatchKeyEvent(event);
//    }
//
//    @Override
//    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
//        Log.d("dispatchKeyShortcutEvent", event.toString());
//        if(event.getDeviceId() == 3) return false;
//        return super.dispatchKeyShortcutEvent(event);
//    }


    MyPresentation myPresentation;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        myPresentation = ((MPosApplication) getApplication()).showExternalAd(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        ((MPosApplication) getApplication()).destroyExternalAD(myPresentation);
    }


    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        class ViewHolder {
            ImageView iv_image;
            TextView tv_name;
            FrameLayout btn_header;
        }

        ImageAdapter() {
            inflater = LayoutInflater.from(TradeHomeActivity.this);
        }

        @Override
        public int getCount() {
            return mPermissions.size();
        }

        @Override
        public Permission getItem(int position) {
            return mPermissions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.grid_item_trade_home, parent, false);
                holder = new ViewHolder();
                holder.iv_image = (ImageView) view.findViewById(R.id.iv_image);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.btn_header = (FrameLayout) view.findViewById(R.id.btn_header);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            final Permission permission = getItem(position);
            holder.tv_name.setText(permission.getName());

            holder.btn_header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PermissionDao permissionDao = MPosApplication.getInstance().getDataHelper().getDaoSession().getPermissionDao();
                    List<Permission> subPermissions = permissionDao.queryBuilder().
                            where(PermissionDao.Properties.PermissionType.notEq("MAIN_PERMISSION"), PermissionDao.Properties.CategoryId.eq(permission.getCategoryId())
                            ).list();

                    Class<?> clazz = Utils.getPermissionClassMaps().get(permission.getName());
                    if (subPermissions != null && subPermissions.size() > 0 && !permission.getName().equals("超级收银台")) {
                        clazz = MoreHomeActivity.class;
                    }
                    Intent it = new Intent(TradeHomeActivity.this, clazz);
                    it.putExtra(KEY_PERMISSION_NAME, permission.getName());
                    it.putExtra(KEY_CATEGORY_ID, permission.getCategoryId());
                    startActivity(it);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
            });
            String image_url = permission.getPictureUrl();
            ImageLoader.getInstance().displayImage(image_url, holder.iv_image, options);
            return view;
        }
    }


}
