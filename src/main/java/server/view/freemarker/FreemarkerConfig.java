package server.view.freemarker;

import freemarker.template.Configuration;
import server.view.ModelGenerator;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:35
 * @Project tio-http-server
 */
public class FreemarkerConfig {

    private Configuration configuration;

    private ModelGenerator modelMaker;

    private String[] suffixes = null;

    public FreemarkerConfig(Configuration configuration, ModelGenerator modelMaker, String[] suffixes) {
        super();
        this.configuration = configuration;
        this.modelMaker = modelMaker;
        this.setSuffixes(suffixes);
    }



    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public ModelGenerator getModelMaker() {
        return modelMaker;
    }

    public void setModelMaker(ModelGenerator modelMaker) {
        this.modelMaker = modelMaker;
    }

    /**
     *
     * @author tanyaowu
     */
    public FreemarkerConfig() {
    }



    /**
     * @return the suffixes
     */
    public String[] getSuffixes() {
        return suffixes;
    }



    /**
     * @param suffixes the suffixes to set
     */
    public void setSuffixes(String[] suffixes) {
        this.suffixes = suffixes;
    }
}
