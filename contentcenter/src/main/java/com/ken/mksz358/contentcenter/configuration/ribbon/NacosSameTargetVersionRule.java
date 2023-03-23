package com.ken.mksz358.contentcenter.configuration.ribbon;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.core.Balancer;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class NacosSameTargetVersionRule extends AbstractLoadBalancerRule {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Autowired
    private NacosServiceManager nacosServiceManager;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object key) {
        try {
            /**
             * the main logic:
             * 1. get version in metaData
             * 2. find all healthy instance by name(user-center)
             * 3. find all instances which are in same version,
             *    will return null if same version instances can't  be found
             * 4. find all instances which are in same cluster,if instances can not be found,
             *    will use step 3 List<instance>
             * 5. call nacos weight function to load balance
             */

            // get cluster name
            String clusterName = nacosDiscoveryProperties.getClusterName();
            Map<String, String> metadata = nacosDiscoveryProperties.getMetadata();
            String version = metadata.get("version");
            DynamicServerListLoadBalancer lb = (DynamicServerListLoadBalancer) this.getLoadBalancer();
            //name = user-center
            String name = lb.getName();
            NamingService namingService = nacosServiceManager.getNamingService();
            //select all healthy instances
            List<Instance> instances = namingService.selectInstances(name, true);

            List<Instance> metadataMatchInstances = instances;
            if (StringUtils.isNotBlank(version)) {
                metadataMatchInstances = instances.stream()
                        .filter(instance -> ObjectUtils.equals(version, instance.getMetadata().get("version")))
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(metadataMatchInstances)) {
                    log.warn("can not find version:{} in nacos metaData", version);
                    return null;
                }
            }

            List<Instance> clusterMetaDataInstances = metadataMatchInstances;
            if (StringUtils.isNotBlank(clusterName)) {
                clusterMetaDataInstances = metadataMatchInstances.stream()
                        .filter(instance -> ObjectUtils.equals(clusterName, instance.getClusterName()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(metadataMatchInstances)) {
                    clusterMetaDataInstances = metadataMatchInstances;
                    log.warn("can not find same cluster name:{}, use another cluster service", clusterName);
                }
            }

            //start to load balance by nacos weight rule
            Instance instance = CustomBalancer.getHostByRandomWeight2(clusterMetaDataInstances);
            return new NacosServer(instance);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
}

class CustomBalancer extends Balancer {
    public static Instance getHostByRandomWeight2(List<Instance> hosts) {
        return getHostByRandomWeight(hosts);
    }
}