package org.xpen.hello.search.lucene;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonTest {


    @Test
    public void test() throws Exception {
        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.enable(JsonParser.Feature.ALLOW_COMMENTS);
        ObjectMapper mapper = new ObjectMapper(jsonFactory);
        String[] files = new String[]{"1.json", "2.json", "3.json", "4.json"};
        
        Map<Integer, ShopBean> shopMap = new HashMap<Integer, ShopBean>();
        
        for (String file: files) {
            InputStream input = JacksonTest.class.getClassLoader().getResourceAsStream("search/lucene/" + file);
            JsonNode jsonNode = mapper.readTree(input);
            JsonNode shops = jsonNode.get("shopRecordBeanList");
            Iterator<JsonNode> shopIterator = shops.elements();
            while (shopIterator.hasNext()) {
                JsonNode shop = shopIterator.next();
                int id = shop.get("shopId").intValue();
                String name = shop.get("shopName").textValue();
                String address = shop.get("address").textValue();
                double latitude = shop.get("geoLat").doubleValue();
                double longitude = shop.get("geoLng").doubleValue();
                //System.out.println("id="+id+",name="+name+",address="+address+",lat="+lat+",lng="+lng);
                ShopBean shopBean = new ShopBean();
                shopBean.setId(id);
                shopBean.setName(name);
                shopBean.setAddress(address);
                shopBean.setLatitude(latitude);
                shopBean.setLongitude(longitude);
                shopMap.putIfAbsent(id, shopBean);
            }
        }
        
        //写入新文件
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("src/test/resources/search/lucene/shop.json"), "UTF-8"));
        for (ShopBean shopBean: shopMap.values()) {
            String writeValueAsString = mapper.writeValueAsString(shopBean);
            bw.write(writeValueAsString);
            bw.newLine();
        }
        bw.close();

    }


}
