package com.jzsec.quotation.parser;

import com.jzsec.quotation.entity.Quotation;
import com.jzsec.quotation.util.BytesUtil;
import com.jzsec.quotation.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by caodaoxi on 17-7-18.
 */
public class QuotationParser implements Parser{
    public Object parse(byte[] bytes) {
        Quotation quotation = new Quotation();
        String origTime = BytesUtil.byte2Long(Arrays.copyOfRange(bytes, 8, 16)) + "";// 数据生成时间
        String hqType = new String(Arrays.copyOfRange(bytes, 18, 21));// 行情类别
        String stkcode = new String(Arrays.copyOfRange(bytes, 21, 29)).trim();// 股票代码
        String tradingPhaseCode = new String(Arrays.copyOfRange(bytes, 33, 41)).trim();// 产品所处的交易阶段代码
        if(tradingPhaseCode.endsWith("0")){
            //正常
            quotation.setIsSuspension("0");
        }else if(tradingPhaseCode.endsWith("1")){
            //全天停盘
            quotation.setIsSuspension("2");
        }else{
            //全天停盘
            quotation.setIsSuspension(" ");
        }

        if(tradingPhaseCode.startsWith("T")){
            //连续竞价
            quotation.setTradingPhaseCode("1");
        }else if(tradingPhaseCode.startsWith("O")){
            //以O开头为集合竞价
            quotation.setTradingPhaseCode("0");
        }else if(tradingPhaseCode.startsWith("S")){
            //启动
            quotation.setTradingPhaseCode("0");
        }else if(tradingPhaseCode.startsWith("B")){
            //休市
            quotation.setTradingPhaseCode("0");
        }else if(tradingPhaseCode.startsWith("C")){
            //收盘集合竞价
            quotation.setTradingPhaseCode("1");
        }else if(tradingPhaseCode.startsWith("E")){
            //闭市
            quotation.setTradingPhaseCode("E");
        }else if(tradingPhaseCode.startsWith("H")){
            //临时停盘
            quotation.setTradingPhaseCode("1");
        }else if(tradingPhaseCode.startsWith("A")){//盘后交易
            return null;
        }

        float prevClosePx = BytesUtil.byte2Long(Arrays.copyOfRange(bytes, 41, 49));// 昨收价//
        // 成交笔数
        long totalVolumeTrade = BytesUtil.byte2Long(Arrays.copyOfRange(bytes, 57, 65));// 成交总量//
        // Int64//
        // 数量，N15(2)
        float totalValueTrade = BytesUtil.byte2Long(Arrays.copyOfRange(bytes, 65, 73));// 成交总金额//
        // Int64//
        // 金额，N18(4)

        quotation.setTime(DateUtil.formetDate(origTime));
        quotation.setCode(stkcode);
        quotation.setClosePrice(prevClosePx/10000);
        quotation.setTotalAmount(totalVolumeTrade/100);
        quotation.setTotalMoney(totalValueTrade/10000);
        if ("010".equals(hqType) || "020".equals(hqType)) {
            deCode010(bytes, 73, quotation);
        } else if ("900".equals(hqType)) {
            deCode900(bytes, 73, quotation);
        } else  {
            return null;
        }
        if(tradingPhaseCode.startsWith("O")) {
            //集合竞价期间当前价为开盘价
            if(quotation.getOpenPrice() == 0) {
                if(quotation.getBuyPrice1() == 0){
                    quotation.setCurrentPrice(quotation.getClosePrice());
                }else{
                    quotation.setCurrentPrice(quotation.getBuyPrice1());
                }
            } else {
                quotation.setCurrentPrice(quotation.getOpenPrice());
            }
            quotation.setCurrentPrice(quotation.getOpenPrice() == 0 ? quotation.getBuyPrice1() : quotation.getClosePrice());
        } else {
            if(quotation.getCurrentPrice() == 0){
                quotation.setCurrentPrice(quotation.getClosePrice());
            }
        }
        return quotation;
    }

    /**
     * 解析集中竞价 交易 业务行情 快照扩展字段
     *
     * @param msg
     * @param index
     */
    private void deCode010(byte[] msg, int index, Quotation quotation) {
        int noMDEntries = BytesUtil.byte2Int(Arrays.copyOfRange(msg, index, index + 4));// 行情条目个数
        for (int i = 0; i < noMDEntries; i++) {

            String mdEntryType = new String(Arrays.copyOfRange(msg, index + 4, index + 6)).trim();// 行情条目类别
            float mdEntryPx = BytesUtil.byte2Long(Arrays.copyOfRange(msg, index + 6, index + 14));// 价格Int64
            // 行情条目价格,N18（6）
            long mdEntrySize = BytesUtil.byte2Long(Arrays.copyOfRange(msg, index + 14, index + 22));// 数量
            // 数量，N15(2)
            Integer mdPriceLevel = BytesUtil.byte2Int(Arrays.copyOfRange(msg, index + 22, index + 24));// 买卖盘档位
            // 0表示不揭示
            // Int64
            int noOrders = BytesUtil.byte2Int(Arrays.copyOfRange(msg, index + 32, index + 36));// 价位揭示委托笔数为0表示不揭示
            Integer inx = index + 36;
            List<Long> orderQtys = new ArrayList<Long>();
            for (int j = 0; j < noOrders; j++) {
                long orderQty = BytesUtil.byte2Long(Arrays.copyOfRange(msg, index + 36, index + 44));// 委托价格
                inx = inx + 8;
                orderQtys.add(orderQty);
            }
            if (mdEntryType.equals("xe")) {// 涨停价
            }
            if (mdEntryType.equals("xf")) {// 涨停价

            }
            if (mdEntryType.equals("0")) {// 0为买入
                if (mdPriceLevel == 1) {
                    quotation.setBuyPrice1(mdEntryPx/1000000);
                    quotation.setBuyAmount1(mdEntrySize/100);
                } else if (mdPriceLevel == 2) {
                    quotation.setBuyPrice2(mdEntryPx/1000000);
                    quotation.setBuyAmount2(mdEntrySize/100);
                } else if (mdPriceLevel == 3) {
                    quotation.setBuyPrice3(mdEntryPx/1000000);
                    quotation.setBuyAmount3(mdEntrySize/100);
                } else if (mdPriceLevel == 4) {
                    quotation.setBuyPrice4(mdEntryPx/1000000);
                    quotation.setBuyAmount4(mdEntrySize/100);
                } else if (mdPriceLevel == 5) {
                    quotation.setBuyPrice5(mdEntryPx/1000000);
                    quotation.setBuyAmount5(mdEntrySize/100);
                }
            }
            if (mdEntryType.equals("1")) {
                if (mdPriceLevel == 1) {
                    quotation.setSellPrice1(mdEntryPx/1000000);
                    quotation.setSellAmount1(mdEntrySize/100);
                } else if (mdPriceLevel == 2) {
                    quotation.setSellPrice2(mdEntryPx/1000000);
                    quotation.setSellAmount2(mdEntrySize/100);
                } else if (mdPriceLevel == 3) {
                    quotation.setSellPrice3(mdEntryPx/1000000);
                    quotation.setSellAmount3(mdEntrySize/100);
                } else if (mdPriceLevel == 4) {
                    quotation.setSellPrice4(mdEntryPx/1000000);
                    quotation.setSellAmount4(mdEntrySize/100);
                } else if (mdPriceLevel == 5) {
                    quotation.setSellPrice5(mdEntryPx/1000000);
                    quotation.setSellAmount5(mdEntrySize/100);
                }
            }
            if (mdEntryType.equals("2")) {
                // 2为最新价
                quotation.setCurrentPrice(mdEntryPx/1000000);
                if(quotation.getCurrentPrice() == 0){
                    quotation.setCurrentPrice(quotation.getClosePrice());
                }
            } else if (mdEntryType.equals("4")) {
                // 4为开盘价
                quotation.setOpenPrice(mdEntryPx/1000000);
            } else if (mdEntryType.equals("7")) {
                // 7为最高价
                quotation.setHighPrice(mdEntryPx/1000000);
            } else if (mdEntryType.equals("8")) {
                // 8为最低价
                quotation.setLowPrice(mdEntryPx/1000000);
            }
            index = index + 32 + noOrders * 8;
        }

    }

    /**
     * 解析指数行情快照扩展字段
     *
     * @param msg
     * @param index
     */
    private void deCode900(byte[] msg, int index, Quotation quotation) {
        int noMDEntries = BytesUtil.byte2Int(Arrays.copyOfRange(msg, index, index + 4));// 行情条目个数
        for (int i = 0; i < noMDEntries; i++) {
            String MDEntryType = new String(Arrays.copyOfRange(msg, index + 4, index + 6));// 行情条目类别
            float mdEntryPx = BytesUtil.byte2Long(Arrays.copyOfRange(msg, index + 6, index + 14));// 指数点位
            // 行情条目价格,N18（6）
            if (MDEntryType.trim().equals("3")) {
                // 当前指数
                quotation.setCurrentPrice(mdEntryPx/1000000);
            } else if (MDEntryType.trim().equals("xa")) {
                // 昨日收盘指数
                quotation.setClosePrice(mdEntryPx/1000000);
            } else if (MDEntryType.trim().equals("xb")) {
                // 开盘指数
                quotation.setOpenPrice(mdEntryPx/1000000);
            } else if (MDEntryType.trim().equals("xc")) {
                // 最高指数
                quotation.setHighPrice(mdEntryPx/1000000);
            } else if (MDEntryType.trim().equals("xd")) {
                // 最低指数
                quotation.setLowPrice(mdEntryPx/1000000);
            }

            index += 10;
        }
    }
}
