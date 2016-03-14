package com.itertk.app.mpos.trade.more;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.utility.Utils;
import com.itertk.app.mpos.dbhelper.Permission;
import com.itertk.app.mpos.dbhelper.PermissionDao;
import com.itertk.app.mpos.trade.TradeHomeActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
/*
* 交易主界面的次级菜单
* */
public class MoreHomeActivity extends Activity  {
    private final static String TAG = "MoreHomeActivity";

    List<Permission> mPermissions ;
    DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_home);
        MyActionbar.setNormalActionBarLayout(this);

        int panel_visibility = View.VISIBLE;
        int grid_visibility = View.INVISIBLE;

        Bundle bundle =  getIntent().getExtras();
        String permissionName = getIntent().getStringExtra(TradeHomeActivity.KEY_PERMISSION_NAME);
        long categoryId = getIntent().getLongExtra(TradeHomeActivity.KEY_CATEGORY_ID, -1);

        mPermissions = loadData(permissionName,categoryId);

        if(mPermissions != null && mPermissions.size() > 0){
            panel_visibility =  View.INVISIBLE;
            grid_visibility = View.VISIBLE;
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
        }

        findViewById(R.id.panel_more).setVisibility(panel_visibility);
        findViewById(R.id.grid).setVisibility(grid_visibility);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MoreHomeActivity", "on destroy");
    }
    private List<Permission> loadData(String permissionName,long categoryId) {
        PermissionDao permissionDao =  MPosApplication.getInstance().getDataHelper().getDaoSession().getPermissionDao();
        return permissionDao.queryBuilder().
                where(PermissionDao.Properties.PermissionType.notEq("MAIN_PERMISSION"), PermissionDao.Properties.CategoryId.eq(categoryId)
                ).orderAsc(PermissionDao.Properties.Order).list();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MPosApplication.getInstance().getMyActivityManager().setBackToActivityClass(TradeHomeActivity.class);
    }
    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        class ViewHolder {
            ImageView iv_image;
            TextView tv_name;
            FrameLayout btn_header;
        }

        ImageAdapter() {
            inflater = LayoutInflater.from(MoreHomeActivity.this);
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
                holder.tv_name =  (TextView) view.findViewById(R.id.tv_name);
                holder.btn_header = (FrameLayout)view.findViewById(R.id.btn_header);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            final Permission permission = getItem(position);
            holder.tv_name.setText(permission.getName());

            holder.btn_header.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent it = new Intent(MoreHomeActivity.this, Utils.getPermissionClassMaps().get(permission.getName()));
                    startActivity(it);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
            });

            String image_url = permission.getPictureUrl();
            ImageLoader.getInstance() .displayImage(image_url, holder.iv_image, options);
            return view;
        }
    }
}
