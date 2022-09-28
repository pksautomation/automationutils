package com.innovaccer.utils.v2.dataHelper.pageobject;

import com.google.gson.Gson;
import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.fileutils.JSONUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PageObjectHelper {
    private Config configInstance;
    private JSONUtils JSONUtils;
    private LoggerUtils LoggerUtils;

    public PageObjectHelper(Config config) {
        init(config);
    }

    public PageObjectHelper() {
        init(Config.getConfig());
    }

    private void init(Config configInstance) {
        this.configInstance = configInstance;
        JSONUtils = new JSONUtils(configInstance);
        LoggerUtils = new LoggerUtils(this.configInstance);
    }

    public void initPage(String pageName) {
        this.loadPageLocators(pageName);
    }

    /**
     * load data from locator files
     *
     * @param fileName
     */
    synchronized public void loadPageLocators(String fileName) {
        Map<String, How> locators = new HashMap<String, How>();
        if (Config.locatorPageWiseData.containsKey(fileName))
            return;
        else {

            ///Pojo for all json
            try {

                ///generic path
                String filePah = System.getProperty("user.dir") + File.separator
                        + "src/test/resources/TestData/PageObjectLocators" + File.separator + fileName + ".json";
                File f = new File(filePah);
                if (!f.exists()) {
                    LoggerUtils.logComment(fileName + ".json locators file not found ");
                    return;
                }
                JSONArray jsonArray = JSONUtils.parseJSONFileInJSONArray(filePah);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);

                    String name = json.names().getString(0);
                    Gson gson = new Gson();
                    How how = gson.fromJson(json.getJSONObject(name).toString(), How.class);
                    //how.setKey(name);
                    locators.put(name, how);

                }
                Config.locatorPageWiseData.put(fileName, locators);
            } catch (Exception e) {
                LoggerUtils.logException(fileName + " file not found ", e, false);
            }
        }
    }

}
