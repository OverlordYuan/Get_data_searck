package searchController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class get_numbers {
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
        conditionJSON.put("endTime", "2020-04-20 23:59:59");
        conditionJSON.put("sortField",0);
        conditionJSON.put("sortWay","asc");
        for(String name:names){
            conditionJSON.put("dataSource", Arrays.asList(source.get(name)));
            JSONArray uids = (JSONArray) JS.get(source.get(name));
            List<Integer> data = new ArrayList<Integer>();
            for (int i=0;i<uids.size();i++){
//                List<Integer> data = new ArrayList<Integer>();
                try{
                conditionJSON.put("mediaInfo", "{'"+source.get(name)+"':[{'uid':'"+uids.getString(i)+"'}]}");
                obj.put("data", conditionJSON.toString());
                long st = System.currentTimeMillis();
                String result = RequestUtils.request("WH_Search_ECRule", path, "POST", obj.toJSONString());
                JSONObject res = (JSONObject) JSON.parseObject(result).get("data");
                data.add((Integer) res.get("total"));
                long t = System.currentTimeMillis()-st;
                if(3010-t>0){
                    Thread.sleep(3010-t);
                }
                }catch (Exception e){
                    System.out.println(e);
                    Thread.sleep(12000);
                }
            }
            String DA = data.toString();
            saveDataToFile(name,"weibo_nums",0, DA);
        }
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
