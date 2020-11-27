package searchController;

import org.junit.Test;

public class GetSearchAccessToken {

    @Test
    public void getSearchToken() throws Exception {
        String path = "/openAPI/search/getAccessToken";
        String method = "POST";
        String authen_origion_code = "OPENAPIYULUNFENXI" + "_" + System.currentTimeMillis();
        String result = RequestUtils.request("WH_Search_ECRule", path, method, authen_origion_code);
        System.out.println(result);
    }
}
