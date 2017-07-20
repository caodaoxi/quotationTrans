package com.jzsec.quotation;

import com.jzsec.quotation.core.client.KafkaClient;
import com.jzsec.quotation.entity.Quotation;
import net.sf.json.JSONObject;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import java.io.*;


/**
 * Created by caodaoxi on 17-7-12.
 */
public class SHQuotationClient {
    public static void main(String[] args) throws Exception {
        try {
            if(args.length < 1) {
                throw new Exception("quotation config file Not specified");
            }

            XMLConfiguration config = new XMLConfiguration(args[0]);
            KafkaClient kafkaClient = new KafkaClient(config);
            String hqFilePath = config.getString("shConfig.hqPath");
            File hqFile = new File(hqFilePath);
            BufferedReader in = null;
            String line = null;
            long lastMarkModified = -1;
            while (true) {
                //行情文件最后修改时间
                long lastModified = hqFile.lastModified();
                if (lastModified > lastMarkModified) {
                    in = new BufferedReader(new InputStreamReader(new FileInputStream(hqFile), config.getString("shConfig.charset")));
                    line = in.readLine();
                    if(line == null) {
                        in.close();
                        continue;
                    }
                    String[] headers = line.split("\\|");
                    int total = Integer.parseInt(headers[3].trim());
                    String timeStr = headers[6].trim();
//                    List<Quotation> quotations = new ArrayList<Quotation>();
                    Quotation quotation = null;
                    while ((line = in.readLine()) != null) {
                        String[] fields = line.split("\\|");
                        quotation = new Quotation();
                        if(fields.length > 32) {
                            quotation.setCode(fields[1].trim());
                            quotation.setName(fields[2].trim());
                            quotation.setTotalAmount(Long.parseLong(fields[3].trim()));
                            quotation.setTotalMoney(Double.parseDouble(fields[4].trim()));
                            quotation.setPreClosePrice(Float.parseFloat(fields[5].trim()));
                            quotation.setOpenPrice(Float.parseFloat(fields[6].trim()));
                            quotation.setHighPrice(Float.parseFloat(fields[7].trim()));
                            quotation.setLowPrice(Float.parseFloat(fields[8].trim()));
                            quotation.setCurrentPrice(Float.parseFloat(fields[9].trim()));
                            quotation.setBuyPrice1(Float.parseFloat(fields[11].trim()));
                            quotation.setBuyAmount1(Long.parseLong(fields[12].trim()));
                            quotation.setSellPrice1(Float.parseFloat(fields[13].trim()));
                            quotation.setSellAmount1(Long.parseLong(fields[14].trim()));

                            quotation.setBuyPrice2(Float.parseFloat(fields[15].trim()));
                            quotation.setBuyAmount2(Long.parseLong(fields[16].trim()));
                            quotation.setSellPrice2(Float.parseFloat(fields[17].trim()));
                            quotation.setSellAmount2(Long.parseLong(fields[18].trim()));

                            quotation.setBuyPrice3(Float.parseFloat(fields[19].trim()));
                            quotation.setBuyAmount3(Long.parseLong(fields[20].trim()));
                            quotation.setSellPrice3(Float.parseFloat(fields[21].trim()));
                            quotation.setSellAmount3(Long.parseLong(fields[22].trim()));

                            quotation.setBuyPrice4(Float.parseFloat(fields[23].trim()));
                            quotation.setBuyAmount4(Long.parseLong(fields[24].trim()));
                            quotation.setSellPrice4(Float.parseFloat(fields[25].trim()));
                            quotation.setSellAmount4(Long.parseLong(fields[26].trim()));

                            quotation.setBuyPrice5(Float.parseFloat(fields[27].trim()));
                            quotation.setBuyAmount5(Long.parseLong(fields[28].trim()));
                            quotation.setSellPrice5(Float.parseFloat(fields[29].trim()));
                            quotation.setSellAmount5(Long.parseLong(fields[30].trim()));
                        } else if(fields.length>8) {
                            quotation.setCode(fields[1].trim());
                            quotation.setName(fields[2].trim());
                            quotation.setTotalAmount(Long.parseLong(fields[3].trim()));
                            quotation.setTotalMoney(Double.parseDouble(fields[4].trim()));
                            quotation.setPreClosePrice(Float.parseFloat(fields[5].trim()));
                            quotation.setOpenPrice(Float.parseFloat(fields[6].trim()));
                            quotation.setHighPrice(Float.parseFloat(fields[7].trim()));
                            quotation.setLowPrice(Float.parseFloat(fields[8].trim()));
                            quotation.setCurrentPrice(Float.parseFloat(fields[9].trim()));
                        }
                        kafkaClient.send(quotation.getCode(), JSONObject.fromObject(quotation).toString());
//                        quotations.add(quotation);
                        System.out.println(quotation.toString());
                    }
                    lastMarkModified = lastModified;
                    in.close();
                }
            }
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
