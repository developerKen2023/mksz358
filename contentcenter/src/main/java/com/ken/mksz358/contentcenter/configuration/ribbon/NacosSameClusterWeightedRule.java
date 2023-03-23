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
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * base on the same cluster to do balance on Nacos Weight
 *
 * @author Ken J Liang
 */

@Slf4j
public class NacosSameClusterWeightedRule extends AbstractLoadBalancerRule {

    @Autowired
    private NacosServiceManager nacosServiceManager;

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
    }

    @Override
    public Server choose(Object o) {
        //content center's cluster, consumer cluster name instead of provider cluster name
        String clusterName = nacosDiscoveryProperties.getClusterName();
        DynamicServerListLoadBalancer lb = (DynamicServerListLoadBalancer) getLoadBalancer();
        NamingService namingService = nacosServiceManager.getNamingService();
        List<Instance> instances;
        try {
            instances = namingService.selectInstances(lb.getName(), true);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
        List<Instance> sameClusterInstances = instances.stream()
                .filter(instance -> ObjectUtils.equals(instance.getClusterName(), clusterName))
                .collect(Collectors.toList());
        List<Instance> instancesToBeChosen = CollectionUtils.isEmpty(sameClusterInstances) ? instances : sameClusterInstances;
        Instance chosenInstance = MyBalancer.getHostByRandomWeight2(instancesToBeChosen);
        return new NacosServer(chosenInstance);
    }
}

class MyBalancer extends Balancer {
    public static Instance getHostByRandomWeight2(List<Instance> hosts) {
        return getHostByRandomWeight(hosts);
    }
}


