package searchController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;


public class SearchController {
    @Test
    public void testGetData() throws Exception {
//        String Json_str = readJsonFile("src\\main\\resources\\uid.json");
        String Json_str = readJsonFile("D:\\工作\\程序\\Social_Robot_Detection\\output\\account_list_1.json");
        JSONObject JS = JSON.parseObject(Json_str);
//        JSONArray N =  JSON.parseArray(readJsonFile("D:\\工作\\程序\\Get_data_searck\\output\\miss\\weibo_miss_uid.json"));
        String path = "/openAPI/search/remoteSearchInfosByConditions";
        JSONObject obj =RequestUtils.getSearchTokenAccesstokenJson();
        JSONObject conditionJSON = new JSONObject();

        HashMap<String,String> source = new HashMap<String,String>();
        source.put("微博","9");
        source.put("推特","1");

        String[] names = new String[]{"微博"};

        conditionJSON.put("pageSize", 500);
        conditionJSON.put("startTime", "2020-01-20 00:00:00");
        conditionJSON.put("sortField",0);
        conditionJSON.put("sortWay","asc");
        for(String name:names){
            JSONArray miss_index = new JSONArray();
//            try{
//                miss_index = JSON.parseArray(readJsonFile("D:\\工作\\程序\\Get_data_searck\\output\\miss\\weibo_miss_uid.json"));
//            }catch (Exception e){
//                continue;
//            }
//            JSONArray miss_index = new JSONArray();
            conditionJSON.put("dataSource", Arrays.asList(source.get(name)));
            JSONArray uids = (JSONArray) JS.get(source.get(name));
//            JSONArray uids = JSON.parseArray(readJsonFile("D:\\工作\\程序\\Get_data_searck\\output\\miss\\weibo_miss_uid.json"));
//            for(int k=0;k<N.size();k++){
//            int i = 0;
            for (int i=12069;i<uids.size();i++){
                JSONArray data = new JSONArray() ;
                conditionJSON.put("endTime", "2020-04-20 23:59:59");
                conditionJSON.put("mediaInfo", "{'"+source.get(name)+"':[{'uid':'"+uids.getString(i)+"'}]}");
                obj.put("data", conditionJSON.toString());
                int total = 0;
                int data_size = 0;
                int j = 1;
                do {
                    long st = System.currentTimeMillis();
                    String result = RequestUtils.request("WH_Search_ECRule", path, "POST", obj.toJSONString());
                    JSONObject res = (JSONObject) JSON.parseObject(result).get("data");
                    if (res != null) {
                        if (j > 0) {
                            total = (int) res.get("total");
                            System.out.println(name + "用户-" + uids.getString(i) + "数据总数据总量为:" + total);
                        }
                        JSONArray data_temp = res.getJSONArray("data");
                        if (j < 1 && data_temp.size() > 0) {
                            data_temp = data_temp.fluentRemove(0);
                        }
                        j--;
                        data_size = data_temp.size();
                        total = total - data_temp.size();
                        System.out.println(name + "用户-" + uids.getString(i) + "数据剩余数据总量为:" + total);
                        data.addAll(data_temp);
                        if (total > 0 && data_temp.size() > 0) {
                            JSONObject last_item = (JSONObject) data_temp.get(data_temp.size() - 1);
                            conditionJSON.put("endTime", last_item.getString("pubtime"));
                            obj.put("data", conditionJSON.toString());
                        }
                        long t = System.currentTimeMillis() - st;
                        if (2010 - t > 0) {
                            Thread.sleep(2010 - t);
                        }
                        //                        if(total>0&&data_size==0){
                        //                            System.out.println(result);
                        //                            Thread.sleep(30000);
                        //                        }
                    } else {
                        System.out.println(result);
                        Thread.sleep(30000);
                    }
                } while (total > 0 & data_size > 0);
                String DA = data.toString();
                saveDataToFile(name,uids.getString(i),i, DA);
            }
//            saveDataToFile("miss","weibo_miss_uid",1, miss_index.toString());
        }
//        System.out.println(miss_index);
    }

    public String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveDataToFile(String name, String uid,int num,String data) {
        String root = "D:\\工作\\程序\\Social_Robot_Detection\\input\\微博-新冠\\";
        String filePath = root + name + "\\"  + uid + ".json";
        BufferedWriter writer = null;
        File file = new File(filePath);
        //如果文件不存在，则新建一个
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //写入
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,false), "UTF-8"));
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(name+"第"+num+"个文件写入成功！");
    }
}

