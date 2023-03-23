package com.ken.mksz358.contentcenter.configuration.ribbon;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class RibbonCustomRule extends AbstractLoadBalancerRule {
    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object o) {
        ILoadBalancer lb = this.getLoadBalancer();
        List<Server> upList = lb.getReachableServers();
        List<Server> allLIst = lb.getAllServers();
        int i = ThreadLocalRandom.current().nextInt(allLIst.size());
        return upList.get(i);
    }
}
