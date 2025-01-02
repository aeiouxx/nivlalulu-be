package com.nivlalulu.nnpro.configuration;

import com.nivlalulu.nnpro.security.ownership.IOwnershipChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class OwnershipCheckerConfiguration {
    @Bean
    public Map<Class<?>, IOwnershipChecker> ownershipCheckerMap(List<IOwnershipChecker> checkers) {
        Map<Class<?>, IOwnershipChecker> map = new HashMap<>();
        for (IOwnershipChecker fetcher : checkers) {
            if (map.containsKey(fetcher.getEntityClass())) {
                throw new IllegalArgumentException("Duplicate ownership checker for entity class "
                        + fetcher.getEntityClass());
            }
            map.put(fetcher.getEntityClass(), fetcher);
        }
        return map;
    }
}
