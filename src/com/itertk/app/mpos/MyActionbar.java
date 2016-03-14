package com.itertk.app.mpos;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.account.AccountHomeActivity;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.config.ConfigHomeActivity;
import com.itertk.app.mpos.trade.TradeHomeActivity;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

/**
 * Created by Administrator on 2014/7/11.
 * 导航条
 */
public class MyActionbar {
    private final static String TAG = "MyActionbar";

    static final public void setLoginActionBarLayout(Context context, int resourceId, String title) {
        ActionBar actionBar = ((Activity) context).getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);

            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflator.inflate(R.layout.actionbar_login, null);

            TextView titleTxt = (TextView) v.findViewById(R.id.titleTxt);
            titleTxt.setText(title);
            ImageView imageView = (ImageView) v.findViewById(R.id.login_title);
            imageView.setImageResource(resourceId);

            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
            actionBar.setCustomView(v, layout);
        }
    }

    static final public void setLoginActionBarLayout(Context context, String title) {
        ActionBar actionBar = ((Activity) context).getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);

            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflator.inflate(R.layout.actionbar_login, null);

            TextView titleTxt = (TextView) v.findViewById(R.id.titleTxt);
            titleTxt.setText(title);

            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
            actionBar.setCustomView(v, layout);
        }
    }

    static final public void setRegisterActionBarLayout(Context context, int resourceId, String title) {
        final Context ctx = context;
        ActionBar actionBar = ((Activity) context).getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);

            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflator.inflate(R.layout.actionbar_register, null);

            RelativeLayout backLayout = (RelativeLayout) v.findViewById(R.id.itemBack);
            backLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) ctx).finish();
                    ((Activity) ctx).overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                }
            });

            TextView titleTxt = (TextView) v.findViewById(R.id.titleTxt);
            titleTxt.setText(title);
            ImageView imageView = (ImageView) v.findViewById(R.id.login_title);
            imageView.setImageResource(resourceId);

            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
            actionBar.setCustomView(v, layout);
        }
    }

    static final public void setNormalActionBarLayout(Activity context) {
        final Context ctx = context;
        ActionBar actionBar = ((Activity) ctx).getActionBar();
        if (null != actionBar) {
            ((Activity) ctx).getActionBar().setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);

            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflator.inflate(R.layout.actionbar_normal, null);

            RelativeLayout backLayout = (RelativeLayout) v.findViewById(R.id.itemBack);
            backLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) ctx).finish();
                    ((Activity) ctx).overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                }
            });

            RelativeLayout homeLayout = (RelativeLayout) v.findViewById(R.id.itemHome);
            homeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent it = new Intent(ctx, TradeHomeActivity.class);
                    ctx.startActivity(it);
                    ((Activity) ctx).overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                }
            });

            RelativeLayout posLayout = (RelativeLayout) v.findViewById(R.id.itemPos);
            posLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPosSigninDialog(v);
                }
            });

            RelativeLayout setLayout = (RelativeLayout) v.findViewById(R.id.itemSet);
            setLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent it = new Intent(ctx, ConfigHomeActivity.class);
                    ctx.startActivity(it);
                    ((Activity) ctx).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
            });

            RelativeLayout accountLayout = (RelativeLayout) v.findViewById(R.id.itemAccount);
            if(((MPosApplication)context.getApplication()).getCurUser()!=null&&
                    ((MPosApplication)context.getApplication()).getCurUser().getType()==0L
                    &&((MPosApplication)context.getApplication()).getMember()!=null) {
                accountLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent it = new Intent(ctx, AccountHomeActivity.class);
                        ctx.startActivity(it);
                        ((Activity) ctx).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    }
                });
            }else {
                accountLayout.setVisibility(View.GONE);
            }


            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
            actionBar.setCustomView(v, layout);
        }
    }

    static final public void setHomeActionBarLayout(Activity context) {
        final Context ctx = context;
        ActionBar actionBar = ((Activity) ctx).getActionBar();
        if (null != actionBar) {
            ((Activity) ctx).getActionBar().setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);

            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflator.inflate(R.layout.actionbar_home, null);

            RelativeLayout homeLayout = (RelativeLayout) v.findViewById(R.id.itemHome);
            homeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent it = new Intent(ctx, TradeHomeActivity.class);
                    ctx.startActivity(it);
                    ((Activity) ctx).overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                }
            });

            RelativeLayout posLayout = (RelativeLayout) v.findViewById(R.id.itemPos);
            posLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPosSigninDialog(v);
                }
            });

            RelativeLayout setLayout = (RelativeLayout) v.findViewById(R.id.itemSet);
            setLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent it = new Intent(ctx, ConfigHomeActivity.class);
                    ctx.startActivity(it);
                    ((Activity) ctx).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
            });

            RelativeLayout accountLayout = (RelativeLayout) v.findViewById(R.id.itemAccount);
            if(((MPosApplication)context.getApplication()).getCurUser()!=null&&
            ((MPosApplication)context.getApplication()).getCurUser().getType()==0L
                    &&((MPosApplication)context.getApplication()).getMember()!=null) {
                accountLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent it = new Intent(ctx, AccountHomeActivity.class);
                        ctx.startActivity(it);
                        ((Activity) ctx).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    }
                });
            }else {
                accountLayout.setVisibility(View.GONE);
            }


            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
            actionBar.setCustomView(v, layout);
        }
    }

    public static void showPosSigninDialog(View v) {
        PosSignInDialog posSignInDialog =new PosSignInDialog(v.getContext(), R.style.MyDialog);
        posSignInDialog.show();
    }

    static final public void setConfigActionBarLayout(Activity context) {
        final Context ctx = context;
        ActionBar actionBar = ((Activity) ctx).getActionBar();
        if (null != actionBar) {
            ((Activity) ctx).getActionBar().setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);

            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflator.inflate(R.layout.actionbar_config, null);

            RelativeLayout backLayout = (RelativeLayout) v.findViewById(R.id.itemBack);
            backLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) ctx).finish();
                    ((Activity) ctx).overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                }
            });

            RelativeLayout homeLayout = (RelativeLayout) v.findViewById(R.id.itemHome);
            homeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent it = new Intent(ctx, TradeHomeActivity.class);
                    ctx.startActivity(it);
                    ((Activity) ctx).overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                }
            });

            RelativeLayout posLayout = (RelativeLayout) v.findViewById(R.id.itemPos);
            posLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPosSigninDialog(v);
                }
            });

            RelativeLayout setLayout = (RelativeLayout) v.findViewById(R.id.itemSet);
            setLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent it = new Intent(ctx, ConfigHomeActivity.class);
                    ctx.startActivity(it);
                    ((Activity) ctx).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
            });

            RelativeLayout accountLayout = (RelativeLayout) v.findViewById(R.id.itemAccount);
            if(((MPosApplication)context.getApplication()).getCurUser()!=null&&
                    ((MPosApplication)context.getApplication()).getCurUser().getType()==0L
                    &&((MPosApplication)context.getApplication()).getMember()!=null) {
                accountLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent it = new Intent(ctx, AccountHomeActivity.class);
                        ctx.startActivity(it);
                        ((Activity) ctx).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    }
                });
            }else {
                accountLayout.setVisibility(View.GONE);
            }


            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
            actionBar.setCustomView(v, layout);
        }
    }

    static final public void setAccountActionBarLayout(Activity context) {
        final Context ctx = context;
        ActionBar actionBar = ((Activity) ctx).getActionBar();
        if (null != actionBar) {
            ((Activity) ctx).getActionBar().setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);

            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflator.inflate(R.layout.actionbar_account, null);

            RelativeLayout backLayout = (RelativeLayout) v.findViewById(R.id.itemBack);
            backLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) ctx).finish();
                    ((Activity) ctx).overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                }
            });

            RelativeLayout homeLayout = (RelativeLayout) v.findViewById(R.id.itemHome);
            homeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent it = new Intent(ctx, TradeHomeActivity.class);
                    ctx.startActivity(it);
                    ((Activity) ctx).overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                }
            });

            RelativeLayout posLayout = (RelativeLayout) v.findViewById(R.id.itemPos);
            posLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPosSigninDialog(v);
                }
            });

            RelativeLayout setLayout = (RelativeLayout) v.findViewById(R.id.itemSet);
            setLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent it = new Intent(ctx, ConfigHomeActivity.class);
                    ctx.startActivity(it);
                    ((Activity) ctx).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
            });

            RelativeLayout accountLayout = (RelativeLayout) v.findViewById(R.id.itemAccount);
            if(((MPosApplication)context.getApplication()).getCurUser()!=null&&
                    ((MPosApplication)context.getApplication()).getCurUser().getType()==0L
                    &&((MPosApplication)context.getApplication()).getMember()!=null) {
                accountLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent it = new Intent(ctx, AccountHomeActivity.class);
                        ctx.startActivity(it);
                        ((Activity) ctx).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    }
                });
            }else {
                accountLayout.setVisibility(View.GONE);
            }


            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
            actionBar.setCustomView(v, layout);
        }
    }

}
