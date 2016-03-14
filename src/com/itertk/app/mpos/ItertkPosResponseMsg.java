package com.itertk.app.mpos;

import java.io.Serializable;

/**
 * Created by Administrator on 2014/11/24.
 */
public abstract class ItertkPosResponseMsg implements Serializable {
    public abstract void onData(byte[] data);
}
