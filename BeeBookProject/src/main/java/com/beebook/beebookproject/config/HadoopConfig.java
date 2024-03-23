package com.beebook.beebookproject.config;


import com.beebook.beebookproject.hdfs.HadoopClient;
import com.beebook.beebookproject.props.HadoopProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

/**
 * @author zhangcx
 * on 2020/5/30 - 10:21
 */
@Configuration
@ConditionalOnProperty(name="hadoop.name-node")
@Slf4j
public class HadoopConfig {

    /**
     * Cấu hình
     */
    public org.apache.hadoop.conf.Configuration getConfiguration(HadoopProperties hadoopProperties) {
        //Đọc file cấu hình
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.setBoolean("dfs.client.use.datanode.hostname", true);
        conf.setBoolean("dfs.datanode.use.datanode.hostname", true);
        conf.set("dfs.replication", "1");
        conf.set("fs.defaultFS", hadoopProperties.getNameNode());
        conf.set("mapred.job.tracker", hadoopProperties.getNameNode());
        conf.set("mapred.job.tracker", hadoopProperties.getNameNode());
        UserGroupInformation.setLoginUser(UserGroupInformation.createRemoteUser("root"));
        return conf;
    }

    @Bean
    public FileSystem fs(HadoopProperties hadoopProperties){
        // Hệ thống tập tin
        FileSystem fs = null;
        try {
            URI uri = new URI(hadoopProperties.getDirectoryPath().trim());
            fs = FileSystem.get(uri, this.getConfiguration(hadoopProperties));
        } catch (Exception e) {
            log.error("【Khởi tạo cấu hình FileSystem không thành công】", e);
        }
        return fs;
    }

    @Bean
    @ConditionalOnBean(FileSystem.class)
    public HadoopClient hadoopClient(FileSystem fs, HadoopProperties hadoopProperties) {
        return new HadoopClient(fs, hadoopProperties);
    }
}
