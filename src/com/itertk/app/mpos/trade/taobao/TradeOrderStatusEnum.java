package com.itertk.app.mpos.trade.taobao;

/**
 * Created by jz on 2015/4/8.
 * 订货订单状态
 */
public enum TradeOrderStatusEnum {
    /**
     * 未付款
     */
    BUYER_PAY_FAILURE(-1, "付款失败"),
    WAIT_BUYER_PAY(0, "等待买家付款"),
    /*
     * 等待卖家发货,即:买家已付款
     */
    WAIT_SELLER_SEND_GOODS(1, "等待卖家发货"),
    /*
     * 等待买家确认收货,即:卖家已发货
     */
    WAIT_BUYER_CONFIRM_GOODS(2, "等待买家确认收货"),
    /*
     * 超过一定时间未付款，系统自动关闭订单
     */
    TRADE_CLOSED_BY_LINKEA(3, "超时交易关闭"),
    /*
     * 交易关闭
     */
    TRADE_CLOSED(4, "交易关闭"),
    /*
     * 交易成功
     */
    TRADE_FINISHED(5, "交易成功");

    private int value;
    private String message = null;

    private TradeOrderStatusEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 通过枚举<code>value</code>获得枚举
     *
     * @param value
     * @return
     */
    public static TradeOrderStatusEnum getByValue(int value) {
        for (TradeOrderStatusEnum statusEnum : values()) {
            if (statusEnum.getValue() == value) {
                return statusEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value + "|" + message;
    }

}
