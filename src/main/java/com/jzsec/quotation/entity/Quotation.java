package com.jzsec.quotation.entity;

import java.util.Date;

/**
 * Created by caodaoxi on 17-7-17.
 */
public class Quotation {
    //数据时间
    private Date time;
    //名称
    private String name = "";
    //证券代码
    private String code;
    //前日收盘价
    private float preClosePrice;
    //收盘价
    private float closePrice;
    //今日开盘价
    private float openPrice;
    //今日最高价
    private float highPrice;
    //今日最低价
    private float lowPrice;
    //最新价
    private float currentPrice;
    //总成交额
    private double totalMoney;
    //总成交量
    private long totalAmount;
    //市盈率；指数：平盘家数
    private float revenue;
    //股票：现手
    private int curAmount;
    //股票：外盘
    private int outAmount;
    //股票：内盘
    private int inAmount;
    //股票：买入价1；指数：涨数
    private float buyPrice1;
    //买入量1；指数：总申买量
    private long buyAmount1;

    //股票：买入价2；指数：涨数
    private float buyPrice2;
    //买入量2；指数：总申买量
    private long buyAmount2;

    //股票：买入价3；指数：涨数
    private float buyPrice3;
    //买入量3；指数：总申买量
    private long buyAmount3;

    //股票：买入价4；指数：涨数
    private float buyPrice4;
    //买入量4；指数：总申买量
    private long buyAmount4;

    //股票：买入价5；指数：涨数
    private float buyPrice5;
    //买入量5；指数：总申买量
    private long buyAmount5;

    //股票：卖出价1；指数： 跌数
    private float sellPrice1;
    //卖出量1；指数：总申卖量
    private long sellAmount1;

    //卖出价2
    private float sellPrice2;
    //卖出量2
    private long sellAmount2;

    //卖出价3
    private float sellPrice3;
    //卖出量3
    private long sellAmount3;

    //卖出价4
    private float sellPrice4;
    //卖出量4
    private long sellAmount4;

    //卖出价5
    private float sellPrice5;
    //卖出量5
    private long sellAmount5;

    //旧总成交额（只用于生成5分钟数据）
    private double oldTotalMoney;

    //旧总成交量（只用于生成5分钟数据）
    private long oldTotalAmount;

    //现价大于买1 B, 现价小于卖1 S,否则为空
    private String pricrStatus;

    //产品所处交易阶段,0：集合竞价,1：连续竞价,E: 已收盘
    private String tradingPhaseCode;

    //是否停盘,0：正常状态, 1：临时停排, 2：全天停牌
    private String isSuspension;


    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public float getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(float closePrice) {
        this.closePrice = closePrice;
    }

    public float getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(float openPrice) {
        this.openPrice = openPrice;
    }

    public float getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(float highPrice) {
        this.highPrice = highPrice;
    }

    public float getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(float lowPrice) {
        this.lowPrice = lowPrice;
    }

    public float getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(float currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public float getRevenue() {
        return revenue;
    }

    public void setRevenue(float revenue) {
        this.revenue = revenue;
    }

    public int getCurAmount() {
        return curAmount;
    }

    public void setCurAmount(int curAmount) {
        this.curAmount = curAmount;
    }

    public int getOutAmount() {
        return outAmount;
    }

    public void setOutAmount(int outAmount) {
        this.outAmount = outAmount;
    }

    public int getInAmount() {
        return inAmount;
    }

    public void setInAmount(int inAmount) {
        this.inAmount = inAmount;
    }

    public float getBuyPrice1() {
        return buyPrice1;
    }

    public void setBuyPrice1(float buyPrice1) {
        this.buyPrice1 = buyPrice1;
    }

    public long getBuyAmount1() {
        return buyAmount1;
    }

    public void setBuyAmount1(long buyAmount1) {
        this.buyAmount1 = buyAmount1;
    }

    public float getBuyPrice2() {
        return buyPrice2;
    }

    public void setBuyPrice2(float buyPrice2) {
        this.buyPrice2 = buyPrice2;
    }

    public long getBuyAmount2() {
        return buyAmount2;
    }

    public void setBuyAmount2(long buyAmount2) {
        this.buyAmount2 = buyAmount2;
    }

    public float getBuyPrice3() {
        return buyPrice3;
    }

    public void setBuyPrice3(float buyPrice3) {
        this.buyPrice3 = buyPrice3;
    }

    public long getBuyAmount3() {
        return buyAmount3;
    }

    public void setBuyAmount3(long buyAmount3) {
        this.buyAmount3 = buyAmount3;
    }

    public float getBuyPrice4() {
        return buyPrice4;
    }

    public void setBuyPrice4(float buyPrice4) {
        this.buyPrice4 = buyPrice4;
    }

    public long getBuyAmount4() {
        return buyAmount4;
    }

    public void setBuyAmount4(long buyAmount4) {
        this.buyAmount4 = buyAmount4;
    }

    public float getBuyPrice5() {
        return buyPrice5;
    }

    public void setBuyPrice5(float buyPrice5) {
        this.buyPrice5 = buyPrice5;
    }

    public long getBuyAmount5() {
        return buyAmount5;
    }

    public void setBuyAmount5(long buyAmount5) {
        this.buyAmount5 = buyAmount5;
    }

    public float getSellPrice1() {
        return sellPrice1;
    }

    public void setSellPrice1(float sellPrice1) {
        this.sellPrice1 = sellPrice1;
    }

    public long getSellAmount1() {
        return sellAmount1;
    }

    public void setSellAmount1(long sellAmount1) {
        this.sellAmount1 = sellAmount1;
    }

    public float getSellPrice2() {
        return sellPrice2;
    }

    public void setSellPrice2(float sellPrice2) {
        this.sellPrice2 = sellPrice2;
    }

    public long getSellAmount2() {
        return sellAmount2;
    }

    public void setSellAmount2(long sellAmount2) {
        this.sellAmount2 = sellAmount2;
    }

    public float getSellPrice3() {
        return sellPrice3;
    }

    public void setSellPrice3(float sellPrice3) {
        this.sellPrice3 = sellPrice3;
    }

    public long getSellAmount3() {
        return sellAmount3;
    }

    public void setSellAmount3(long sellAmount3) {
        this.sellAmount3 = sellAmount3;
    }

    public float getSellPrice4() {
        return sellPrice4;
    }

    public void setSellPrice4(float sellPrice4) {
        this.sellPrice4 = sellPrice4;
    }

    public long getSellAmount4() {
        return sellAmount4;
    }

    public void setSellAmount4(long sellAmount4) {
        this.sellAmount4 = sellAmount4;
    }

    public float getSellPrice5() {
        return sellPrice5;
    }

    public void setSellPrice5(float sellPrice5) {
        this.sellPrice5 = sellPrice5;
    }

    public long getSellAmount5() {
        return sellAmount5;
    }

    public void setSellAmount5(long sellAmount5) {
        this.sellAmount5 = sellAmount5;
    }

    public double getOldTotalMoney() {
        return oldTotalMoney;
    }

    public void setOldTotalMoney(double oldTotalMoney) {
        this.oldTotalMoney = oldTotalMoney;
    }

    public long getOldTotalAmount() {
        return oldTotalAmount;
    }

    public void setOldTotalAmount(long oldTotalAmount) {
        this.oldTotalAmount = oldTotalAmount;
    }

    public String getPricrStatus() {
        return pricrStatus;
    }

    public void setPricrStatus(String pricrStatus) {
        this.pricrStatus = pricrStatus;
    }

    public String getTradingPhaseCode() {
        return tradingPhaseCode;
    }

    public void setTradingPhaseCode(String tradingPhaseCode) {
        this.tradingPhaseCode = tradingPhaseCode;
    }

    public String getIsSuspension() {
        return isSuspension;
    }

    public void setIsSuspension(String isSuspension) {
        this.isSuspension = isSuspension;
    }

    public float getPreClosePrice() {
        return preClosePrice;
    }

    public void setPreClosePrice(float preClosePrice) {
        this.preClosePrice = preClosePrice;
    }

    @Override
    public String toString() {
        return "Quotation{" +
                "isSuspension='" + isSuspension + '\'' +
                ", tradingPhaseCode='" + tradingPhaseCode + '\'' +
                ", pricrStatus='" + pricrStatus + '\'' +
                ", oldTotalAmount=" + oldTotalAmount +
                ", oldTotalMoney=" + oldTotalMoney +
                ", sellAmount5=" + sellAmount5 +
                ", sellPrice5=" + sellPrice5 +
                ", sellAmount4=" + sellAmount4 +
                ", sellPrice4=" + sellPrice4 +
                ", sellAmount3=" + sellAmount3 +
                ", sellPrice3=" + sellPrice3 +
                ", sellAmount2=" + sellAmount2 +
                ", sellPrice2=" + sellPrice2 +
                ", sellAmount1=" + sellAmount1 +
                ", sellPrice1=" + sellPrice1 +
                ", buyAmount5=" + buyAmount5 +
                ", buyPrice5=" + buyPrice5 +
                ", buyAmount4=" + buyAmount4 +
                ", buyPrice4=" + buyPrice4 +
                ", buyAmount3=" + buyAmount3 +
                ", buyPrice3=" + buyPrice3 +
                ", buyAmount2=" + buyAmount2 +
                ", buyPrice2=" + buyPrice2 +
                ", buyAmount1=" + buyAmount1 +
                ", buyPrice1=" + buyPrice1 +
                ", inAmount=" + inAmount +
                ", outAmount=" + outAmount +
                ", curAmount=" + curAmount +
                ", revenue=" + revenue +
                ", totalAmount=" + totalAmount +
                ", totalMoney=" + totalMoney +
                ", currentPrice=" + currentPrice +
                ", lowPrice=" + lowPrice +
                ", highPrice=" + highPrice +
                ", openPrice=" + openPrice +
                ", closePrice=" + closePrice +
                ", preClosePrice=" + preClosePrice +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", time=" + time +
                '}';
    }
}
